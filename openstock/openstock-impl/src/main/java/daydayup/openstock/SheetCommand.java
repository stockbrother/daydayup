package daydayup.openstock;

import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.ws.Holder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.star.container.NoSuchElementException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.sheet.XSpreadsheetDocument;
import com.sun.star.task.XStatusIndicator;
import com.sun.star.uno.XComponentContext;

import daydayup.jdbc.JdbcAccessTemplate;
import daydayup.jdbc.JdbcAccessTemplate.JdbcOperation;
import daydayup.jdbc.ResultSetProcessor;
import daydayup.openstock.cup.ColumnIdentifier;
import daydayup.openstock.cup.CupExpr;
import daydayup.openstock.cup.IndexSqlSelectFieldsResolveContext;
import daydayup.openstock.database.DataBaseService;
import daydayup.openstock.database.Tables;
import daydayup.openstock.netease.NeteaseUtil;
import daydayup.openstock.netease.WashedFileLoader;
import daydayup.openstock.netease.WashedFileLoader.DbWashedFileLoadContext;
import daydayup.openstock.netease.WashedFileLoader.WashedFileLoadContext;
import daydayup.openstock.util.DocUtil;
import daydayup_openstock_cup.parser;
import daydayup_openstock_cup.scanner;
import java_cup.runtime.Symbol;

public class SheetCommand extends CommandBase<Object> {

	private static final Logger LOG = LoggerFactory.getLogger(SheetCommand.class);

	private static final String SN_SYS_CMDS = "SYS_CMDS";

	private static final String SN_SYS_SQL_QUERY = "SYS_SQL_QUERY";

	public static final String SN_SYS_INDEX_DEFINE = "SYS_INDEX_DEFINE";

	private static final String SN_SYS_INDEX_TABLE = "SYS_INDEX_TABLE";

	private int maxRows = 1000;

	private static class SqlCommandInfo {
		public String id;
		public String sql;
		public String targetSheet;
	}

	@Override
	public Object doExecute(CommandContext cc) {
		XComponentContext xcc = cc.getComponentContext();
		DataBaseService dbs = cc.getDataBaseService();
		XSpreadsheet xSheet = DocUtil.getSpreadsheetByName(xcc, SN_SYS_CMDS, false);

		if (xSheet == null) {
			// "no sheet with name CMDS";
			return "no sheet with name " + SN_SYS_CMDS;
		}
		String invokeId = null;
		boolean body = false;
		String command = null;
		List<String> argL = new ArrayList<>();
		for (int i = 0; i < 1024 * 1024; i++) {
			String value0I = DocUtil.getText(xSheet, 0, i);

			if ("Invoke".equals(value0I)) {
				invokeId = DocUtil.getText(xSheet, 1, i);
				continue;
			}
			if (invokeId == null) {
				continue;
			}
			if ("ID".equals(value0I)) {
				body = true;
				continue;
			}

			if (!body) {
				continue;
			}
			if (value0I == null || value0I.trim().length() == 0) {
				break;
			}

			if (invokeId.equals(value0I)) {
				// found the command to invoke.
				command = DocUtil.getText(xSheet, 1, i);
				for (int j = 2;; j++) {
					String argJ = DocUtil.getText(xSheet, j, i);
					if (argJ == null || argJ.trim().length() == 0) {
						break;
					}
					argL.add(argJ);
				}

				break;
			}

		}

		if (invokeId == null) {
			LOG.warn("no invokeId found.");
			return "no invokeId found.";
		}

		if (command == null) {
			LOG.warn("no command found for invokeId:{}", invokeId);
			return "no command found for invokeId";
		}

		if (command.equals(SN_SYS_SQL_QUERY)) {
			return this.executeSqlQuery(cc, argL);
		} else if (command.equals(SN_SYS_INDEX_TABLE)) {
			return this.executeIndexTable(cc, argL);
		} else if (command.equals("NETEASE_WASHED_2_DB")) {
			return this.executeNeteaseWashed2Db(cc, argL);
		} else if (command.equals("RESET_SHEET")) {
			return this.executeResetSheet(cc);
		} else {
			return "not supporte:" + command;
		}
	}

	private List<String> getIndexNameList(CommandContext cc, String tableId, Holder<String> tableName) {
		XSpreadsheet xSheet = DocUtil.getSpreadsheetByName(cc.getComponentContext(), SN_SYS_INDEX_TABLE, false);
		//
		List<String> indexNameL = new ArrayList<>();
		for (int i = 0;; i++) {
			String id = DocUtil.getText(xSheet, 0, i);

			if (id == null || id.trim().length() == 0) {
				break;
			}

			if (tableId.equals(id)) {
				tableName.value = DocUtil.getText(xSheet, 1, i);
				for (int col = 2;; col++) {

					String idxNameC = DocUtil.getText(xSheet, col, i);
					if (idxNameC == null || idxNameC.trim().length() == 0) {
						break;
					}
					indexNameL.add(idxNameC);
				}
				break;
			}
		}
		return indexNameL;
	}

	private Object executeIndexTable(CommandContext cc, List<String> argL) {
		String tableId = argL.get(0);

		Holder<String> tableName = new Holder<>();
		List<String> indexNameL = this.getIndexNameList(cc, tableId, tableName);
		if (indexNameL.isEmpty()) {
			return "empty index name list";
		}

		StringBuffer sql = new StringBuffer();
		sql.append("select corpId as CORP,reportDate as DATE");

		Set<Integer> typeSet = new HashSet<>();

		for (int i = 0; i < indexNameL.size(); i++) {
			String indexName = indexNameL.get(i);
			IndexSqlSelectFieldsResolveContext src = new IndexSqlSelectFieldsResolveContext(cc,indexName);
			sql.append(",");
			src.resolveSqlSelectFields(sql);

			sql.append(" as " + indexNameL.get(i));			
			src.getReportTypeSet(typeSet,true);
		}

		// from
		int ts = 0;
		sql.append(" from ");
		for (Integer type : typeSet) {
			if (ts > 0) {
				sql.append(",");
			}
			sql.append(Tables.getReportTable(type) + " as r" + type);
			ts++;
		}

		// where join on.
		ts = 0;
		sql.append(" where 1=1 ");
		Integer preType = null;
		for (Integer type : typeSet) {
			if (preType == null) {
				preType = type;
				continue;
			}

			sql.append("and r" + type + " = r" + preType);
			ts++;
		}
		String targetSheetF = "" + tableName.value;
		cc.getDataBaseService().execute(new JdbcOperation<String>() {

			@Override
			public String execute(Connection con, JdbcAccessTemplate t) {
				return t.executeQuery(con, sql.toString(), new ResultSetProcessor<String>() {

					@Override
					public String process(ResultSet rs) throws SQLException {
						writeToSheet(cc, rs, targetSheetF);
						return null;
					}
				});
			}
		}, false);

		return "done";
	}

	private Object executeResetSheet(CommandContext cc) {
		XSpreadsheetDocument xDoc = DocUtil.getSpreadsheetDocument(cc.getComponentContext());

		String[] names = xDoc.getSheets().getElementNames();
		for (String name : names) {
			if (name.startsWith("SYS_")) {
				continue;
			}
			try {
				xDoc.getSheets().removeByName(name);
			} catch (NoSuchElementException e) {
				throw RtException.toRtException(e);
			} catch (WrappedTargetException e) {
				throw RtException.toRtException(e);
			} //
		}
		return "done";
	}

	private Object executeNeteaseWashed2Db(CommandContext cc, List<String> argL) {
		DataBaseService dbs = cc.getDataBaseService();
		XSpreadsheetDocument xDoc = DocUtil.getSpreadsheetDocument(cc.getComponentContext());
		WashedFileLoadContext flc = new DbWashedFileLoadContext(dbs);
		new WashedFileLoader(xDoc).load(NeteaseUtil.getDataWashedDir(), flc);
		return "done";
	}

	private Object executeSqlQuery(CommandContext cc, List<String> argL) {
		if (argL.isEmpty()) {
			LOG.warn("illegel argument for sql query.");
			return "illegel argument for sql query.";
		}
		String sqlId = argL.get(0);
		String sql = null;
		String targetSheet = null;
		XSpreadsheet xSheet = DocUtil.getSpreadsheetByName(cc.getComponentContext(), SN_SYS_SQL_QUERY, false);
		for (int i = 0;; i++) {
			String id = DocUtil.getText(xSheet, 0, i);
			if (id == null || id.trim().length() == 0) {
				break;
			}
			if (sqlId.equals(id)) {
				sql = DocUtil.getText(xSheet, 1, i);
				targetSheet = DocUtil.getText(xSheet, 2, i);
				break;
			}
		}
		final String sqlF = sql;
		final String targetSheetF = targetSheet;
		return cc.getDataBaseService().execute(new JdbcOperation<Object>() {

			@Override
			public Object execute(Connection con, JdbcAccessTemplate t) {

				return t.executeQuery(con, sqlF, new ResultSetProcessor<Object>() {

					@Override
					public Object process(ResultSet rs) throws SQLException {
						writeToSheet(cc, rs, targetSheetF);
						return "done.";
					}

				});

			}
		}, false);
	}

	private void writeToSheet(CommandContext cc, ResultSet rs, String targetSheet) throws SQLException {
		XComponentContext xcc = cc.getComponentContext();
		XStatusIndicator si = cc.getStatusIndicator();
		XSpreadsheet xSheet = DocUtil.getOrCreateSpreadsheetByName(xcc, targetSheet);
		DocUtil.setActiveSheet(xcc, xSheet);
		int cols = rs.getMetaData().getColumnCount();
		// write header
		for (int i = 0; i < cols; i++) {
			String colName = rs.getMetaData().getColumnLabel(i + 1);
			DocUtil.setText(xSheet, i, 0, colName);
		}
		// write rows
		int row = 1;

		while (rs.next()) {
			if (row > this.maxRows) {
				break;
			}
			for (int i = 0; i < cols; i++) {
				Object obj = rs.getObject(i + 1);
				DocUtil.setValue(xSheet, i, row, obj);

			}
			row++;
			si.setText("Row:" + row + ",Limit:" + this.maxRows);
			si.setValue(row * 100 / this.maxRows);

		}

	}
}

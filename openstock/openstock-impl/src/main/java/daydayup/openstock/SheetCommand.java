package daydayup.openstock;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
import daydayup.openstock.cninfo.CninfoCorpInfo2DbSheetCommand;
import daydayup.openstock.database.DataBaseService;
import daydayup.openstock.netease.NeteaseUtil;
import daydayup.openstock.netease.WashedFileLoader;
import daydayup.openstock.netease.WashedFileLoader.DbWashedFileLoadContext;
import daydayup.openstock.netease.WashedFileLoader.WashedFileLoadContext;
import daydayup.openstock.sheetcommand.IndexTableSheetCommand;
import daydayup.openstock.util.DocUtil;

public class SheetCommand extends CommandBase<Object> {

	private static final Logger LOG = LoggerFactory.getLogger(SheetCommand.class);

	private static final String SN_SYS_CMDS = "SYS_CMDS";

	private static final String SN_SYS_SQL_QUERY = "SYS_SQL_QUERY";

	public static final String SN_SYS_INDEX_DEFINE = "SYS_INDEX_DEFINE";

	public static final String SN_SYS_INDEX_TABLE = "SYS_INDEX_TABLE";

	private static int maxRows = 1000;

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
		SheetCommandContext scc = new SheetCommandContext(cc, argL);

		if (command.equals(SN_SYS_SQL_QUERY)) {
			return this.executeSqlQuery(cc, argL);
		} else if (command.equals(SN_SYS_INDEX_TABLE)) {
			return new IndexTableSheetCommand().execute(scc);
		} else if (command.equals("NETEASE_WASHED_2_DB")) {
			return this.executeNeteaseWashed2Db(cc, argL);
		} else if (command.equals("RESET_SHEET")) {
			return this.executeResetSheet(cc);
		} else if(command.equals("CNINFO_CORPINFO_2_DB")){
			return new CninfoCorpInfo2DbSheetCommand().execute(scc);
		}else {
			return "not supporte:" + command;
		}
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

	public static void writeToSheet(CommandContext cc, ResultSet rs, String targetSheet) throws SQLException {
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
			if (row > maxRows) {
				break;
			}
			for (int i = 0; i < cols; i++) {
				Object obj = rs.getObject(i + 1);
				DocUtil.setValue(xSheet, i, row, obj);

			}
			row++;
			si.setText("Row:" + row + ",Limit:" + maxRows);
			si.setValue(row * 100 / maxRows);

		}

	}
}

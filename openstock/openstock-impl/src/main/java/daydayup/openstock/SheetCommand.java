package daydayup.openstock;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.uno.XComponentContext;

import daydayup.jdbc.JdbcAccessTemplate;
import daydayup.jdbc.JdbcAccessTemplate.JdbcOperation;
import daydayup.jdbc.ResultSetProcessor;
import daydayup.openstock.database.DataBaseService;
import daydayup.openstock.util.DocUtil;

public class SheetCommand extends CommandBase<Object> {

	private static final Logger LOG = LoggerFactory.getLogger(SheetCommand.class);

	private static class SqlCommandInfo {
		public String id;
		public String sql;
		public String targetSheet;
	}

	@Override
	public Object execute(CommandContext cc) {
		XComponentContext xcc = cc.getComponentContext();
		DataBaseService dbs = cc.getDataBaseService();
		XSpreadsheet xSheet = DocUtil.getSpreadsheetByName(xcc, "SQL_QUERY", false);

		if (xSheet == null) {
			// "no sheet with name SQL";
			return null;
		}
		String invokeId = null;
		Map<String, SqlCommandInfo> sqlMap = new HashMap<>();
		boolean body = false;
		for (int i = 0; i < 1024 * 1024; i++) {
			String value0I = DocUtil.getText(xSheet, 0, i);
			if ("Invoke".equals(value0I)) {
				invokeId = DocUtil.getText(xSheet, 1, i);
				continue;
			}
			if ("Body".equals(value0I)) {
				body = true;
				continue;
			}

			if (!body) {
				continue;
			}
			if (value0I == null || value0I.trim().length() == 0) {
				break;
			}

			SqlCommandInfo ci = new SqlCommandInfo();
			ci.id = value0I;
			ci.sql = DocUtil.getText(xSheet, 1, i);
			ci.targetSheet = DocUtil.getText(xSheet, 2, i);
			sqlMap.put(ci.id, ci);
		}

		SqlCommandInfo sci = sqlMap.get(invokeId);
		if (sci == null) {
			LOG.warn("no sql command found for invokeId:{}", invokeId);
			return null;
		}

		dbs.execute(new JdbcOperation<Object>() {

			@Override
			public Object execute(Connection con, JdbcAccessTemplate t) {

				t.executeQuery(con, sci.sql, new ResultSetProcessor<Object>() {

					@Override
					public Object process(ResultSet rs) throws SQLException {
						writeToSheet(xcc, rs, sci.targetSheet);
						return null;
					}

				});

				return null;
			}
		}, false);
		return null;
	}

	private void writeToSheet(XComponentContext xcc, ResultSet rs, String targetSheet) throws SQLException {

		XSpreadsheet xSheet = DocUtil.getOrCreateSpreadsheetByName(xcc, targetSheet);
		int cols = rs.getMetaData().getColumnCount();
		// write header
		for (int i = 0; i < cols; i++) {
			String colName = rs.getMetaData().getColumnName(i + 1);
			DocUtil.setText(xSheet, i, 0, colName);
		}
		// write rows
		int row = 1;
		while (rs.next()) {

			for (int i = 0; i < cols; i++) {
				Object obj = rs.getObject(i + 1);
				DocUtil.setText(xSheet, i, row, String.valueOf(obj));
			}
			row++;
		}

	}
}

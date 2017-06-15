package daydayup.openstock.sheetcommand;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.star.sheet.XSpreadsheet;

import daydayup.jdbc.JdbcAccessTemplate;
import daydayup.jdbc.ResultSetProcessor;
import daydayup.jdbc.JdbcAccessTemplate.JdbcOperation;
import daydayup.openstock.BaseSheetCommand;
import daydayup.openstock.CommandContext;
import daydayup.openstock.SheetCommand;
import daydayup.openstock.SheetCommandContext;
import daydayup.openstock.util.DocUtil;

public class SqlQuerySheetCommand extends BaseSheetCommand<Object> {

	private static final Logger LOG = LoggerFactory.getLogger(SqlQuerySheetCommand.class);

	
	@Override
	protected Object doExecute(SheetCommandContext cc) {
		List<String> argL = cc.getArgumentList();
		if (argL.isEmpty()) {
			LOG.warn("illegel argument for sql query.");
			return "illegel argument for sql query.";
		}
		String sqlId = argL.get(0);
		String sql = null;
		String targetSheet = null;
		XSpreadsheet xSheet = DocUtil.getSpreadsheetByName(cc.getComponentContext(), SheetCommand.SN_SYS_SQL_QUERY, false);
		for (int i = 0;; i++) {
			String id = DocUtil.getText(xSheet, "ID", i);
			if (id == null || id.trim().length() == 0) {
				break;
			}
			if (sqlId.equals(id)) {
				sql = DocUtil.getText(xSheet, "SQL", i);
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
						DocUtil.writeToSheet(cc, rs, targetSheetF);
						return "done.";
					}

				});

			}
		}, false);
	}
}

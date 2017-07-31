package daydayup.openstock.sheetcommand;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import daydayup.jdbc.JdbcAccessTemplate;
import daydayup.jdbc.JdbcAccessTemplate.JdbcOperation;
import daydayup.jdbc.ResultSetProcessor;
import daydayup.openstock.BaseSheetCommand;
import daydayup.openstock.SheetCommand;
import daydayup.openstock.SheetCommandContext;
import daydayup.openstock.document.Spreadsheet;
import daydayup.openstock.ooa.DocUtil;

public class SqlQuerySheetCommand extends BaseSheetCommand<Object> {

	private static final Logger LOG = LoggerFactory.getLogger(SqlQuerySheetCommand.class);

	@Override
	protected Object doExecute(final SheetCommandContext cc) {

		Spreadsheet sheet = cc.getSheet();
		int dataRow = -1;
		String sql = null;
		for (int i = 0; i < 100; i++) {

			String key = sheet.getText(0, i);
			if (key.equals("Data")) {
				dataRow = i;
				break;
			}

			if (key == null || key.trim().length() == 0) {
				break;
			}

			if (key.equals("SQL")) {
				sql = sheet.getText(1, i);
			}
			if (dataRow == -1) {
				dataRow = i;
			}
		}
		if (sql == null) {
			throw new RuntimeException("no SQL argument provided.");
		}

		final String sqlF = sql;
		final int dataRowF = dataRow + 1;
		return cc.getDataBaseService().execute(new JdbcOperation<Object>() {

			@Override
			public Object execute(Connection con, JdbcAccessTemplate t) {

				return t.executeQuery(con, sqlF, new ResultSetProcessor<Object>() {

					@Override
					public Object process(ResultSet rs) throws SQLException {
						cc.getDocument().writeToSheet(rs, cc.getSheet(), dataRowF, cc.getStatusIndicator());
						return "done.";
					}

				});

			}
		}, false);
	}
}

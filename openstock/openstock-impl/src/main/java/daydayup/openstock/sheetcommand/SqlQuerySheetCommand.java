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
	protected Object doExecute(SheetCommandContext cc) {
		
		String sqlId = null;//TODO
		String sql = null;
		String targetSheet = null;
		Spreadsheet xSheet = cc.getSpreadsheetByName(SheetCommand.SN_SYS_SQL_QUERY, false);
		for (int i = 0;; i++) {
			String id = xSheet.getText( "ID", i);
			if (id == null || id.trim().length() == 0) {
				break;
			}
			if (sqlId.equals(id)) {
				sql = xSheet.getText( "SQL", i);
				targetSheet = xSheet.getText( 2, i);
				break;
			}
		}
		int dataRow = 10;
		final String sqlF = sql;
		final String targetSheetF = targetSheet;
		return cc.getDataBaseService().execute(new JdbcOperation<Object>() {

			@Override
			public Object execute(Connection con, JdbcAccessTemplate t) {

				return t.executeQuery(con, sqlF, new ResultSetProcessor<Object>() {

					@Override
					public Object process(ResultSet rs) throws SQLException {
						cc.getDocument().writeToSheet(rs, cc.getSheet(),dataRow,cc.getStatusIndicator());
						return "done.";
					}

				});

			}
		}, false);
	}
}

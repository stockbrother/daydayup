package daydayup.openstock.sheetcommand;

import java.sql.Connection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import daydayup.jdbc.JdbcAccessTemplate;
import daydayup.jdbc.JdbcAccessTemplate.JdbcOperation;
import daydayup.openstock.BaseSheetCommand;
import daydayup.openstock.SheetCommand;
import daydayup.openstock.SheetCommandContext;
import daydayup.openstock.document.Spreadsheet;

public class SqlUpdateSheetCommand extends BaseSheetCommand<Object> {

	private static final Logger LOG = LoggerFactory.getLogger(SqlUpdateSheetCommand.class);

	
	@Override
	protected Object doExecute(SheetCommandContext cc) {
		List<String> argL = cc.getArgumentList();
		if (argL.isEmpty()) {
			LOG.warn("illegel argument for sql query.");
			return "illegel argument for sql query.";
		}
		String sqlId = argL.get(0);
		String sql = null;
		
		Spreadsheet xSheet = cc.getSpreadsheetByName(SheetCommand.SN_SYS_SQL_UPDATE, false);
		for (int i = 0;; i++) {
			String id = xSheet.getText( "ID", i);
			if (id == null || id.trim().length() == 0) {
				break;
			}
			if (sqlId.equals(id)) {
				sql = xSheet.getText( "SQL", i);		
				break;
			}
		}
		
		final String sqlF = sql;
		
		return cc.getDataBaseService().execute(new JdbcOperation<Object>() {

			@Override
			public Object execute(Connection con, JdbcAccessTemplate t) {

				return t.executeUpdate(con, sqlF);

			}
		}, true);
	}
}

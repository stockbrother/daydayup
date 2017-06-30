package daydayup.openstock.sheetcommand;

import daydayup.openstock.BaseSheetCommand;
import daydayup.openstock.IndexSqlQuery;
import daydayup.openstock.SheetCommandContext;

public class FillIndexSheetCommand extends BaseSheetCommand<Object> {

	@Override
	protected Object doExecute(SheetCommandContext cc) {

		IndexSqlQuery isq = new IndexSqlQuery();

		return "done";
	}

}

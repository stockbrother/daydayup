package daydayup.openstock.sheetcommand;

import daydayup.openstock.SheetCommandContext;

public class ScopedIndexTableSheetCommand extends IndexTableSheetCommand {

	@Override
	protected String getTableId(SheetCommandContext scc) {
		return null;
	}

	@Override
	protected void appendSqlWhere(SheetCommandContext scc, StringBuffer sql) {

	}

	protected String getTargetSheet(SheetCommandContext scc, String tableName) {
		return null;
	}
}

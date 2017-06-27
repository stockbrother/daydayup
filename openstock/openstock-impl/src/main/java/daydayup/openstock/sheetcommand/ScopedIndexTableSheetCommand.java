package daydayup.openstock.sheetcommand;

import daydayup.openstock.SheetCommand;
import daydayup.openstock.SheetCommandContext;
import daydayup.openstock.document.Spreadsheet;

public class ScopedIndexTableSheetCommand extends IndexTableSheetCommand {

	private static class Attribute {
		String tableId = null;
		String scope = null;
		String name = null;
	}

	protected Attribute getAttribute(SheetCommandContext scc) {
		Attribute att = (Attribute) scc.getAttributes().getAttribute(ScopedIndexTableSheetCommand.class.getName());
		if (att == null) {
			String scopedId = scc.getArgumentList().get(0);

			Spreadsheet xSheet = scc.getSpreadsheetByName(
					SheetCommand.SN_SYS_SCOPED_INDEX_TABLE, false);
			att = new Attribute();
			for (int i = 0;; i++) {
				String id = xSheet.getText( 0, i);

				if (id == null || id.trim().length() == 0) {
					break;
				}

				if (scopedId.equals(id)) {
					att.tableId = xSheet.getText( "TABLEID", i);
					att.scope = xSheet.getText( "SCOPE", i);
					att.name = xSheet.getText( "NAME", i);
					break;
				}
			}
			scc.getAttributes().setAttribute(ScopedIndexTableSheetCommand.class.getName(), att);
		}
		return att;
	}

	@Override
	protected String getTableId(SheetCommandContext scc) {

		return this.getAttribute(scc).tableId;
	}

	@Override
	protected void appendSqlWhere(SheetCommandContext scc, StringBuffer sql) {
		sql.append(" " + this.getAttribute(scc).scope);

	}

	protected String getTargetSheet(SheetCommandContext scc, String tableName) {
		return this.getAttribute(scc).name;
	}
}

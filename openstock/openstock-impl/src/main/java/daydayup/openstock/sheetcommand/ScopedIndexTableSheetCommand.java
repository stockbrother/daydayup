package daydayup.openstock.sheetcommand;

import java.util.Date;

import com.sun.star.sheet.XSpreadsheet;

import daydayup.openstock.SheetCommand;
import daydayup.openstock.SheetCommandContext;
import daydayup.openstock.util.DocUtil;

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

			XSpreadsheet xSheet = DocUtil.getSpreadsheetByName(scc.getComponentContext(),
					SheetCommand.SN_SYS_SCOPED_INDEX_TABLE, false);
			att = new Attribute();
			for (int i = 0;; i++) {
				String id = DocUtil.getText(xSheet, 0, i);

				if (id == null || id.trim().length() == 0) {
					break;
				}

				if (scopedId.equals(id)) {
					att.tableId = DocUtil.getText(xSheet, "TABLEID", i);
					att.scope = DocUtil.getText(xSheet, "SCOPE", i);
					att.name = DocUtil.getText(xSheet, "NAME", i);
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

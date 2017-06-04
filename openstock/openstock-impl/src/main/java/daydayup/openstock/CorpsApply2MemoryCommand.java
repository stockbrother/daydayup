package daydayup.openstock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.star.container.XNamed;
import com.sun.star.frame.XController;
import com.sun.star.frame.XDesktop;
import com.sun.star.frame.XModel;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.lang.XComponent;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.sheet.XSpreadsheetDocument;
import com.sun.star.sheet.XSpreadsheetView;
import com.sun.star.table.XCell;
import com.sun.star.uno.Exception;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XInterface;

import daydayup.openstock.util.DocUtil;

public class CorpsApply2MemoryCommand extends CommandBase {
	private static final Logger LOG = LoggerFactory.getLogger(CorpsApply2MemoryCommand.class);

	@Override
	public void execute(CommandContext cc) {		

		CorpNameService cns = GlobalVars.getInstance().getCorpNameService();
		XSpreadsheet xSheet = DocUtil.getSpreadsheetByName(cc.componentContext, "CORPS");
		// https://wiki.openoffice.org/wiki/Documentation/DevGuide/Spreadsheets/Example:_Editing_Spreadsheet_Cells
		int i = 0;
		while (true) {

			String code = getText(xSheet, 0, i);
			if (code == null) {
				break;
			}
			String name = getText(xSheet, 1, i);
			cns.addCorpName(code, name);
			if (LOG.isTraceEnabled()) {
				LOG.trace("[{}]code:{},name:{}", i, code, name);
			}
			i++;
		}
	}

	public static String getText(XSpreadsheet xSheet, int col, int row) {
		try {
			XCell xCell = xSheet.getCellByPosition(col, row);
			String rt = getText(xCell);
			return rt;
		} catch (IndexOutOfBoundsException e) {
			throw new RuntimeException(e);
		}

	}

	public static String getText(XCell xCell) {
		com.sun.star.text.XText xCellText = UnoRuntime.queryInterface(com.sun.star.text.XText.class, xCell);
		String rt = xCellText.getText().getString();
		if (rt != null) {
			rt = rt.trim();
			if (rt.length() == 0) {
				rt = null;
			}
		}
		return rt;
	}
}

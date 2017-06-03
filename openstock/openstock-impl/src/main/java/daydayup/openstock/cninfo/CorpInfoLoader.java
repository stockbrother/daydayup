package daydayup.openstock.cninfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import com.sun.star.container.XNamed;
import com.sun.star.frame.XController;
import com.sun.star.frame.XDesktop;
import com.sun.star.frame.XModel;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.lang.XComponent;
import com.sun.star.sheet.XSheetCellCursor;
import com.sun.star.sheet.XSheetCellRange;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.sheet.XSpreadsheetDocument;
import com.sun.star.sheet.XSpreadsheetView;
import com.sun.star.table.XCell;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XInterface;

import au.com.bytecode.opencsv.CSVReader;
import daydayup.openstock.CorpNameService;

public class CorpInfoLoader {

	public void loadCorpInfoIntoMemory(File csvFile, CorpNameService cns) {

		try {
			Charset cs = Charset.forName("GBK");
			Reader fr = new InputStreamReader(new FileInputStream(csvFile), cs);
			CSVReader reader = new CSVReader(fr);

			// skip header1
			String[] next = reader.readNext();
			// skip header2
			next = reader.readNext();
			while (true) {
				next = reader.readNext();
				if (next == null) {
					break;
				}
				String code = next[0].trim();
				String name = next[1].trim();
				cns.addCorpName(code, name);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void loadCorpInfoToSheet(CorpNameService cns, XDesktop xDesktop) {

		XComponent xComp = xDesktop.getCurrentComponent();

		XInterface xDoc = UnoRuntime.queryInterface(XInterface.class, xComp);
		XSpreadsheetDocument xDoc2 = UnoRuntime.queryInterface(XSpreadsheetDocument.class, xDoc);

		XModel xModel = UnoRuntime.queryInterface(XModel.class, xComp);
		XController xControl = xModel.getCurrentController();
		Object viewData = xControl.getViewData();

		XSpreadsheetView xView = (XSpreadsheetView) UnoRuntime.queryInterface(XSpreadsheetView.class,
				xModel.getCurrentController());

		XSpreadsheet xSheet = xView.getActiveSheet();

		XNamed xName = UnoRuntime.queryInterface(XNamed.class, xSheet);

		if (!"CORPS".equals(xName.getName())) {
			throw new RuntimeException("sheet name:" + "CORPS" + " expected,actually is:" + xName.getName());
		}
		// https://wiki.openoffice.org/wiki/Documentation/DevGuide/Spreadsheets/Example:_Editing_Spreadsheet_Cells
		String[] codes = cns.getSortedCorpCodeArray();
		for (int i = 0; i < codes.length; i++) {

			String code = codes[i];
			String name = cns.getName(codes[i]);

			setText(xSheet, 0, i, code);
			setText(xSheet, 1, i, name);

		}
		Object selected = xModel.getCurrentSelection();
		XSheetCellRange xSelected2 = UnoRuntime.queryInterface(XSheetCellRange.class, selected);

	}

	public static void setText(XSpreadsheet xSheet, int col, int row, String text) {
		try {
			XCell xCell = xSheet.getCellByPosition(col, row);
			setText(xCell, text);
		} catch (IndexOutOfBoundsException e) {
			throw new RuntimeException(e);
		}

	}

	public static void setText(XCell xCell, String text) {
		com.sun.star.text.XText xCellText = UnoRuntime.queryInterface(com.sun.star.text.XText.class, xCell);
		com.sun.star.text.XTextCursor xTextCursor = xCellText.createTextCursor();
		xCellText.insertString(xTextCursor, text, false);
	}
}

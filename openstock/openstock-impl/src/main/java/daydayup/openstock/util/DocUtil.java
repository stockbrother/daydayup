package daydayup.openstock.util;

import com.sun.star.container.NoSuchElementException;
import com.sun.star.container.XNamed;
import com.sun.star.frame.XController;
import com.sun.star.frame.XDesktop;
import com.sun.star.frame.XModel;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.lang.XComponent;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.sheet.XSpreadsheetDocument;
import com.sun.star.sheet.XSpreadsheetView;
import com.sun.star.table.XCell;
import com.sun.star.uno.Exception;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.uno.XInterface;

public class DocUtil {
	public static XSpreadsheetDocument getSpreadsheetDocument(XComponentContext cc) {
		Object desktop = null;
		try {
			desktop = cc.getServiceManager().createInstanceWithContext("com.sun.star.frame.Desktop", cc);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		XDesktop xDesktop = UnoRuntime.queryInterface(XDesktop.class, desktop);

		XComponent xComp = xDesktop.getCurrentComponent();

		XInterface xDoc = UnoRuntime.queryInterface(XInterface.class, xComp);
		XSpreadsheetDocument xDoc2 = UnoRuntime.queryInterface(XSpreadsheetDocument.class, xDoc);

		return xDoc2;
	}

	public static XSpreadsheet getActiveSheet(XComponentContext cc, String name) {

		Object desktop = null;
		try {
			desktop = cc.getServiceManager().createInstanceWithContext("com.sun.star.frame.Desktop", cc);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		XDesktop xDesktop = UnoRuntime.queryInterface(XDesktop.class, desktop);

		XComponent xComp = xDesktop.getCurrentComponent();

		XModel xModel = UnoRuntime.queryInterface(XModel.class, xComp);
		XController xControl = xModel.getCurrentController();
		Object viewData = xControl.getViewData();

		XSpreadsheetView xView = (XSpreadsheetView) UnoRuntime.queryInterface(XSpreadsheetView.class,
				xModel.getCurrentController());

		XSpreadsheet xSheet = xView.getActiveSheet();

		XNamed xName = UnoRuntime.queryInterface(XNamed.class, xSheet);
		if (!name.equals(xName.getName())) {
			throw new RuntimeException("sheet name:" + name + " expected,actually is:" + xName.getName());
		}
		return xSheet;

	}

	public static XSpreadsheet getSpreadsheetByName(XComponentContext cc, String name) {
		return getSpreadsheetByName(getSpreadsheetDocument(cc), name);
	}

	public static XSpreadsheet getSpreadsheetByName(XSpreadsheetDocument xDoc, String name) {

		try {
			return (XSpreadsheet) UnoRuntime.queryInterface(XSpreadsheet.class, xDoc.getSheets().getByName(name));
		} catch (NoSuchElementException e) {
			throw new RuntimeException(e);
		} catch (WrappedTargetException e) {
			throw new RuntimeException(e);
		}

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

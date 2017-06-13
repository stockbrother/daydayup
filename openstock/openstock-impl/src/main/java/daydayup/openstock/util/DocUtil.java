package daydayup.openstock.util;

import java.util.Date;

import com.sun.star.container.NoSuchElementException;
import com.sun.star.container.XNamed;
import com.sun.star.frame.XController;
import com.sun.star.frame.XDesktop;
import com.sun.star.frame.XFrame;
import com.sun.star.frame.XModel;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.lang.XComponent;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.sheet.XSpreadsheetDocument;
import com.sun.star.sheet.XSpreadsheetView;
import com.sun.star.table.XCell;
import com.sun.star.task.XStatusIndicator;
import com.sun.star.task.XStatusIndicatorFactory;
import com.sun.star.uno.Exception;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.uno.XInterface;

import daydayup.openstock.OpenStock;
import daydayup.openstock.RtException;
import daydayup.openstock.netease.NeteaseUtil;

public class DocUtil {
	public static XSpreadsheetDocument getSpreadsheetDocument(XComponentContext xcc) {
		Object desktop = null;
		try {
			desktop = xcc.getServiceManager().createInstanceWithContext("com.sun.star.frame.Desktop", xcc);
		} catch (Exception e) {
			throw new RtException(e);
		}
		XDesktop xDesktop = UnoRuntime.queryInterface(XDesktop.class, desktop);
		XComponent xComp = xDesktop.getCurrentComponent();

		XInterface xDoc = UnoRuntime.queryInterface(XInterface.class, xComp);
		XSpreadsheetDocument xDoc2 = UnoRuntime.queryInterface(XSpreadsheetDocument.class, xDoc);

		return xDoc2;
	}

	public static XStatusIndicator createStatusIndicator(XComponentContext xcc) {
		Object desktop = null;
		try {
			desktop = xcc.getServiceManager().createInstanceWithContext("com.sun.star.frame.Desktop", xcc);
		} catch (Exception e) {
			throw new RtException(e);
		}
		XDesktop xDesktop = UnoRuntime.queryInterface(XDesktop.class, desktop);
		XFrame frame = xDesktop.getCurrentFrame();
		XStatusIndicatorFactory sif = UnoRuntime.queryInterface(XStatusIndicatorFactory.class, frame);
		XStatusIndicator si = sif.createStatusIndicator();
		return si;
	}

	private static XStatusIndicator createStatusIndicator2(XComponentContext xcc) {
		Object frame;
		try {
			frame = xcc.getServiceManager().createInstanceWithContext("com.sun.star.frame.Frame", xcc);
		} catch (Exception e) {
			throw new RtException(e);
		}
		// Object service =
		// xcc.getServiceManager().createInstanceWithContext("com.sun.star.frame.Desktop",
		// xcc);

		XStatusIndicatorFactory sif = UnoRuntime.queryInterface(XStatusIndicatorFactory.class, frame);
		XStatusIndicator si = sif.createStatusIndicator();
		return si;
	}

	public static void setActiveSheet(XComponentContext xcc, XSpreadsheet xSheet) {
		Object desktop = OpenStock.getInstance().getDesktop(xcc);
		XDesktop xDesktop = UnoRuntime.queryInterface(XDesktop.class, desktop);

		XComponent xComp = xDesktop.getCurrentComponent();

		XModel xModel = UnoRuntime.queryInterface(XModel.class, xComp);
		XController xControl = xModel.getCurrentController();
		Object viewData = xControl.getViewData();

		XSpreadsheetView xView = (XSpreadsheetView) UnoRuntime.queryInterface(XSpreadsheetView.class,
				xModel.getCurrentController());
		xView.setActiveSheet(xSheet);

	}

	public static XSpreadsheet getActiveSheet(XComponentContext xcc, String name) {

		Object desktop = OpenStock.getInstance().getDesktop(xcc);
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

	public static XSpreadsheet getOrCreateSpreadsheetByName(XComponentContext cc, String name) {
		XSpreadsheetDocument xDoc = getSpreadsheetDocument(cc);

		XSpreadsheet rt = getSpreadsheetByName(xDoc, name, false);
		if (rt == null) {
			rt = createSheet(xDoc, name);
		}
		return rt;
	}

	public static XSpreadsheet getSpreadsheetByName(XComponentContext cc, String name, boolean force) {
		return getSpreadsheetByName(getSpreadsheetDocument(cc), name, force);
	}

	public static XSpreadsheet getSpreadsheetByName(XSpreadsheetDocument xDoc, String name, boolean force) {

		try {
			return (XSpreadsheet) UnoRuntime.queryInterface(XSpreadsheet.class, xDoc.getSheets().getByName(name));
		} catch (NoSuchElementException e) {
			if (force) {
				throw new RtException(e);
			} else {
				return null;
			}
		} catch (WrappedTargetException e) {
			throw new RtException(e);
		}

	}

	public static void setValue(XSpreadsheet xSheet, int col, int row, Object value) {

		try {

			XCell xCell = xSheet.getCellByPosition(col, row);
			if (value == null) {
				// do nothing.
			} else if (value instanceof Date) {
				String str = NeteaseUtil.DF.format((Date) value);
				setText(xCell, str);
			} else if (value instanceof Number) {
				xCell.setValue(((Number) value).doubleValue());
			} else {
				setText(xCell, value.toString());
			}

		} catch (IndexOutOfBoundsException e) {
			throw RtException.toRtException(e);
		}

	}

	public String format(Object obj) {
		if (obj == null) {
			return "";
		} else if (obj instanceof Date) {
			return NeteaseUtil.DF.format((Date) obj);
		} else {
			return obj.toString();
		}

	}

	public static void setText(XSpreadsheet xSheet, int col, int row, String text) {
		try {
			XCell xCell = xSheet.getCellByPosition(col, row);
			setText(xCell, text);
		} catch (IndexOutOfBoundsException e) {
			throw new RtException(e);
		}

	}

	public static int getColIndex(XSpreadsheet xSheet, String name) {
		int rt = -1;
		for (int i = 0;; i++) {
			String nameI = getText(xSheet, i, 0);
			if (nameI == null || nameI.trim().length() == 0) {
				break;
			}
			if (name.equals(nameI)) {
				rt = i;
				break;
			}
		}

		return rt;
	}

	public static String getText(XSpreadsheet xSheet, String col, int row) {
		return getText(xSheet, getColIndex(xSheet, col), row);
	}

	public static String getText(XSpreadsheet xSheet, int col, int row) {
		try {
			XCell xCell = xSheet.getCellByPosition(col, row);
			return getText(xCell);
		} catch (IndexOutOfBoundsException e) {
			throw new RtException(e);
		}

	}

	public static String getText(XCell xCell) {
		com.sun.star.text.XText xCellText = UnoRuntime.queryInterface(com.sun.star.text.XText.class, xCell);

		return xCellText.getString();
	}

	public static void setText(XCell xCell, String text) {
		com.sun.star.text.XText xCellText = UnoRuntime.queryInterface(com.sun.star.text.XText.class, xCell);
		xCellText.setString(text);
	}

	public static XSpreadsheet createSheet(XSpreadsheetDocument xDoc, String name) {
		String[] names = xDoc.getSheets().getElementNames();
		xDoc.getSheets().insertNewByName(name, (short) names.length);
		return getSpreadsheetByName(xDoc, name, true);
	}

	public static Double getValue(XSpreadsheet xSheet, int col, int row) {
		try {
			XCell cell = xSheet.getCellByPosition(col, row);
			return cell.getValue();
		} catch (IndexOutOfBoundsException e) {
			throw new RtException(e);
		}

	}

	public static Double getValueByNameVertically(XSpreadsheet xSheet, String nameCol, String name, String valueCol) {
		int nameC = DocUtil.getColIndex(xSheet, nameCol);
		int valueC = DocUtil.getColIndex(xSheet, valueCol);
		for (int i = 1;; i++) {
			String nameI = DocUtil.getText(xSheet, nameC, i);
			if (nameI == null || nameI.trim().length() == 0) {
				break;
			}
			if (name.equals(nameI)) {
				return DocUtil.getValue(xSheet, valueC, i);
			}

		}
		return null;
	}
}

package daydayup.openstock.ooa;

import java.util.Date;

import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.table.XCell;
import com.sun.star.uno.UnoRuntime;

import daydayup.openstock.RtException;
import daydayup.openstock.document.Spreadsheet;

public class OoaSpreadsheet implements Spreadsheet {
	XSpreadsheet xSheet;
	OoaSpreadsheetDocument doc;

	public OoaSpreadsheet(XSpreadsheet xSheet, OoaSpreadsheetDocument doc) {
		this.xSheet = xSheet;
		this.doc = doc;
	}

	@Override
	public String getText(int col, int row) {
		try {
			XCell xCell = xSheet.getCellByPosition(col, row);
			return getText(xCell);
		} catch (IndexOutOfBoundsException e) {
			throw new RtException(e);
		}

	}

	public Double getValue(int col, int row) {
		try {
			XCell cell = xSheet.getCellByPosition(col, row);
			return cell.getValue();
		} catch (IndexOutOfBoundsException e) {
			throw new RtException(e);
		}

	}

	@Override
	public Double getValueByNameVertically(String nameCol, String name, String valueCol) {
		int nameC = getColIndex(nameCol);
		int valueC = getColIndex(valueCol);
		for (int i = 1;; i++) {
			String nameI = getText(nameC, i);
			if (nameI == null || nameI.trim().length() == 0) {
				break;
			}
			if (name.equals(nameI)) {
				return this.getValue(valueC, i);
			}

		}
		return null;
	}

	public static void setValue(XSpreadsheet xSheet, int col, int row, Object value) {

		try {

			XCell xCell = xSheet.getCellByPosition(col, row);
			if (value == null) {
				// do nothing.
			} else if (value instanceof Date) {
				String str = DocUtil.DF.format((Date) value);
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
			return DocUtil.DF.format((Date) obj);
		} else {
			return obj.toString();
		}

	}

	public void setText(int col, int row, String text) {
		try {
			XCell xCell = xSheet.getCellByPosition(col, row);
			setText(xCell, text);
		} catch (IndexOutOfBoundsException e) {
			throw new RtException(e);
		}

	}

	public int getColIndex(String name) {
		int rt = -1;
		for (int i = 0;; i++) {
			String nameI = getText(i, 0);
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

	public String getText(String col, int row) {
		return getText(getColIndex(col), row);
	}

	public static String getText(XSpreadsheet xSheet, int col, int row) {
		try {
			XCell xCell = xSheet.getCellByPosition(col, row);
			return getText(xCell);
		} catch (IndexOutOfBoundsException e) {
			throw new RtException(e);
		}

	}

	private static String getText(XCell xCell) {
		com.sun.star.text.XText xCellText = UnoRuntime.queryInterface(com.sun.star.text.XText.class, xCell);

		return xCellText.getString();
	}

	public static void setText(XCell xCell, String text) {
		com.sun.star.text.XText xCellText = UnoRuntime.queryInterface(com.sun.star.text.XText.class, xCell);
		if(text == null){
			text = "";
		}
		xCellText.setString(text);
	}

	@Override
	public void setValue(int col, int row, Object value) {
		try {

			XCell xCell = xSheet.getCellByPosition(col, row);
			if (value == null) {				
				setText(xCell, null);
			} else if (value instanceof Date) {
				String str = DocUtil.DF.format((Date) value);
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

	@Override
	public void active() {
		doc.activeSheet(this);//
	}

}

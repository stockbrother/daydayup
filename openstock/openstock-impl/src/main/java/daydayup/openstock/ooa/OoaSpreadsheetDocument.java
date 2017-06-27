package daydayup.openstock.ooa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.sun.star.container.NoSuchElementException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.sheet.XSpreadsheetDocument;
import com.sun.star.uno.UnoRuntime;

import daydayup.openstock.RtException;
import daydayup.openstock.SheetCommand;
import daydayup.openstock.document.Spreadsheet;
import daydayup.openstock.document.SpreadsheetDocument;
import daydayup.openstock.document.StatusIndicator;

public class OoaSpreadsheetDocument implements SpreadsheetDocument {

	XSpreadsheetDocument xDoc;

	private Map<String, Spreadsheet> sheetMap = new HashMap<String, Spreadsheet>();

	public OoaSpreadsheetDocument(XSpreadsheetDocument xDoc2) {
		this.xDoc = xDoc2;
	}

	@Override
	public void removeByName(String name) {
		this.sheetMap.remove(name);
		try {
			xDoc.getSheets().removeByName(name);
		} catch (NoSuchElementException | WrappedTargetException e) {
			throw RtException.toRtException(e);
		}
	}

	@Override
	public String[] getSheetNames() {
		return xDoc.getSheets().getElementNames();
	}

	public int getSheetMaxRows() {
		Spreadsheet xSheet = getSpreadsheetByName(SheetCommand.SN_SYS_CFG, false);
		if (xSheet == null) {
			return 10000;
		}
		Double value = xSheet.getValueByNameVertically("Name", "sheet.max.rows", "Value");

		if (value == null) {
			return 10000;
		}
		return value.intValue();
	}

	private XSpreadsheet doGetSpreadsheetByName(String name) {
		try {
			XSpreadsheet rt = (XSpreadsheet) UnoRuntime.queryInterface(XSpreadsheet.class,
					xDoc.getSheets().getByName(name));
			return rt;
		} catch (NoSuchElementException e) {
			return null;
		} catch (WrappedTargetException e) {
			throw new RtException(e);
		}
	}

	@Override
	public Spreadsheet getSpreadsheetByName(String name, boolean force) {
		Spreadsheet rt = this.sheetMap.get(name);

		if (rt == null) {
			XSpreadsheet xSheet = this.doGetSpreadsheetByName(name);
			if (xSheet != null) {
				rt = new OoaSpreadsheet(xSheet);
				this.sheetMap.put(name, rt);
			}
		}
		if (rt == null && force) {
			throw new RtException("no sheet with name:" + name);
		}
		return rt;

	}

	public Spreadsheet getOrCreateSpreadsheetByName(String name) {

		Spreadsheet rt = getSpreadsheetByName(name, false);
		if (rt == null) {
			rt = createSheet(name);
		}
		return rt;
	}

	public Spreadsheet createSheet(String name) {
		String[] names = xDoc.getSheets().getElementNames();
		xDoc.getSheets().insertNewByName(name, (short) names.length);
		return getSpreadsheetByName(name, true);
	}

	public void activeSheet(Spreadsheet sheet) {

	}

	@Override
	public void writeToSheet(ResultSet rs, String targetSheet, StatusIndicator si) throws SQLException {
		int maxRows = getSheetMaxRows();
		Spreadsheet xSheet = this.getOrCreateSpreadsheetByName(targetSheet);
		activeSheet(xSheet);
		int cols = rs.getMetaData().getColumnCount();
		// write header
		for (int i = 0; i < cols; i++) {
			String colName = rs.getMetaData().getColumnLabel(i + 1);
			xSheet.setText(i, 0, colName);
		}
		// write rows
		int row = 1;

		while (rs.next()) {
			if (row > maxRows) {
				break;
			}
			for (int i = 0; i < cols; i++) {
				Object obj = rs.getObject(i + 1);
				xSheet.setValue(i, row, obj);

			}
			row++;
			si.setText("Row:" + row + ",Limit:" + maxRows);
			si.setValue(row * 100 / maxRows);

		}

	}
}

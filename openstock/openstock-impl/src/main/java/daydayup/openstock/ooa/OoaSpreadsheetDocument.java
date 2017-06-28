package daydayup.openstock.ooa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.sun.star.container.NoSuchElementException;
import com.sun.star.container.XNamed;
import com.sun.star.frame.XController;
import com.sun.star.frame.XDesktop;
import com.sun.star.frame.XModel;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.lang.XComponent;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.sheet.XSpreadsheetDocument;
import com.sun.star.sheet.XSpreadsheetView;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

import daydayup.openstock.OpenStock;
import daydayup.openstock.RtException;
import daydayup.openstock.SheetCommand;
import daydayup.openstock.document.Spreadsheet;
import daydayup.openstock.document.SpreadsheetDocument;
import daydayup.openstock.document.StatusIndicator;

public class OoaSpreadsheetDocument implements SpreadsheetDocument {

	XSpreadsheetDocument xDoc;

	private Map<String, Spreadsheet> sheetMap = new HashMap<String, Spreadsheet>();
	OoaDocService docService;

	public OoaSpreadsheetDocument(XSpreadsheetDocument xDoc2, OoaDocService docService) {
		this.xDoc = xDoc2;
		this.docService = docService;
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
				rt = new OoaSpreadsheet(xSheet, this);
				this.sheetMap.put(name, rt);
			}
		}
		if (rt == null && force) {
			throw new RtException("no sheet with name:" + name);
		}
		return rt;

	}

	@Override
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

	public void activeSheet(Spreadsheet xSheet) {

		Object desktop = OpenStock.getInstance().getDesktop(this.docService.xcc);
		XDesktop xDesktop = UnoRuntime.queryInterface(XDesktop.class, desktop);

		XComponent xComp = xDesktop.getCurrentComponent();

		XModel xModel = UnoRuntime.queryInterface(XModel.class, xComp);
		XController xControl = xModel.getCurrentController();
		Object viewData = xControl.getViewData();

		XSpreadsheetView xView = (XSpreadsheetView) UnoRuntime.queryInterface(XSpreadsheetView.class,
				xModel.getCurrentController());

		xView.setActiveSheet(((OoaSpreadsheet) xSheet).xSheet);

	}

	private String getActiveSheetName() {

		Object desktop = OpenStock.getInstance().getDesktop(this.docService.xcc);
		XDesktop xDesktop = UnoRuntime.queryInterface(XDesktop.class, desktop);

		XComponent xComp = xDesktop.getCurrentComponent();

		XModel xModel = UnoRuntime.queryInterface(XModel.class, xComp);
		XController xControl = xModel.getCurrentController();
		Object viewData = xControl.getViewData();

		XSpreadsheetView xView = (XSpreadsheetView) UnoRuntime.queryInterface(XSpreadsheetView.class,
				xModel.getCurrentController());

		XSpreadsheet xSheet = xView.getActiveSheet();

		XNamed xName = UnoRuntime.queryInterface(XNamed.class, xSheet);

		return xName.getName();

	}

	@Override
	public void writeToSheet(ResultSet rs, Spreadsheet sheet, int firstRow, StatusIndicator si) throws SQLException {
		int maxRows = getSheetMaxRows();
		int cols = rs.getMetaData().getColumnCount();
		// write header
		int row = firstRow;
		for (int i = 0; i < cols; i++) {
			String colName = rs.getMetaData().getColumnLabel(i + 1);
			sheet.setText(i, row, colName);
		}
		// write rows
		row ++;
		while (rs.next()) {
			if (row > maxRows) {
				break;
			}
			for (int i = 0; i < cols; i++) {
				Object obj = rs.getObject(i + 1);
				sheet.setValue(i, row, obj);

			}
			row++;
			si.setText("Row:" + row + ",Limit:" + maxRows);
			si.setValue(row * 100 / maxRows);

		}

	}

	@Override
	public Spreadsheet getActiveSpreadsheet() {
		String name = getActiveSheetName();
		return this.getSpreadsheetByName(name, true);

	}
}

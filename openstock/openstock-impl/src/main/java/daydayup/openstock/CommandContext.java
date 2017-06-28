package daydayup.openstock;

import daydayup.openstock.database.DataBaseService;
import daydayup.openstock.document.ComponentContext;
import daydayup.openstock.document.DocumentService;
import daydayup.openstock.document.Spreadsheet;
import daydayup.openstock.document.SpreadsheetDocument;
import daydayup.openstock.document.StatusIndicator;

public class CommandContext {

	ComponentContext cc;
	StatusIndicator si;

	public CommandContext(ComponentContext cc) {
		this.cc = cc;
	}

	public ComponentContext getComponentContext() {
		return this.cc;
	}

	public DataBaseService getDataBaseService() {
		return OpenStock.getInstance().getDataBaseService();
	}

	public DocumentService getDocumentService() {
		return this.cc.getDocumentService();
	}

	public Spreadsheet getSpreadsheetByName(String name, boolean force) {

		return this.getDocumentService().getDocument().getSpreadsheetByName(name, force);
	}

	public SpreadsheetDocument getDocument() {
		return this.getDocumentService().getDocument();
	}

	public StatusIndicator getStatusIndicator() {
		return this.si;
	}

	public void setStatusIndicator(StatusIndicator si) {
		this.si = si;
	}

	public Spreadsheet getActiveSpreadsheet() {
		return this.getDocumentService().getDocument().getActiveSpreadsheet();
	}
}

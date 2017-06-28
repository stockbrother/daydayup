package daydayup.openstock.document;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface SpreadsheetDocument {

	public Spreadsheet getSpreadsheetByName(String name, boolean force);

	public void removeByName(String name);

	public String[] getSheetNames();
	
	public Spreadsheet getOrCreateSpreadsheetByName(String name);
	
	public void writeToSheet(ResultSet rs, Spreadsheet sheet,int firstRow, StatusIndicator si) throws SQLException;

	public Spreadsheet getActiveSpreadsheet();
	

}

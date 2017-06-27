package daydayup.openstock.document;

public interface Spreadsheet {

	public String getText(String col,int row);
	public String getText(int col, int row);
	public Double getValueByNameVertically(String nameCol, String name, String value);
	public void setValue(int col, int row, Object value);
	public void setText(int col, int row, String value);
}

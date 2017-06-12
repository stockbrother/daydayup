package daydayup.openstock.database;

public class Tables {
	public static final String TN_PROPERTY = "property";
	public static final String TN_ALIAS_INFO = "alias_info";
	public static final String TN_CORP_INFO = "corp_info";
	

	public static String getReportTable(int reportType) {
		return "corp_report_" + reportType;
	}

	public static String getReportColumn(int columnIndex) {
		return "d_" + columnIndex;
	}

}

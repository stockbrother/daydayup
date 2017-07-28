package daydayup.openstock.sheetcommand;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import daydayup.openstock.RtException;

public class DatedIndex {
	private static final DateFormat ASDF = new SimpleDateFormat("yyyy_MM_dd");

	public String indexName;
	private Date reportDate;

	protected DatedIndex(Date rDateL, String idxNameC) {
		this.indexName = idxNameC;
		this.reportDate = rDateL;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public static DatedIndex valueOf(String index, String dateS) {
		Date date = null;
		try {
			date = IndexTableSheetCommand.DF.parse(dateS);
		} catch (ParseException e) {
			throw RtException.toRtException(e);
		}

		return valueOf(index,date);
	}

	public static DatedIndex valueOf(String indexName,Date date) {
		return new DatedIndex(date, indexName);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.indexName + "@");
		sb.append(IndexTableSheetCommand.DF.format(this.reportDate));

		return sb.toString();

	}

	public String as() {
		//
		StringBuffer sb = new StringBuffer();
		sb.append(this.indexName + "_");
		sb.append(ASDF.format(this.reportDate));

		return sb.toString();

	}

}

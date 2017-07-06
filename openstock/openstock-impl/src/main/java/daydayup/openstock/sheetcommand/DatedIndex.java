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

	public static DatedIndex parse(String datedIndexS) {

		int idx = datedIndexS.indexOf('@');
		if (idx < 0) {
			throw new RtException("cannot parse:" + datedIndexS);
		}
		String idxName = datedIndexS.substring(0, idx);

		String dateS = datedIndexS.substring(idx + 1, datedIndexS.length());
		Date date = null;
		try {
			date = IndexTableSheetCommand.DF.parse(dateS);
		} catch (ParseException e) {
			throw RtException.toRtException(e);
		}

		return valueOf(date, idxName);
	}

	public static DatedIndex valueOf(Date date, String indexName) {
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

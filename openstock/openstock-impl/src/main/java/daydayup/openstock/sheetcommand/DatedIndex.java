package daydayup.openstock.sheetcommand;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import daydayup.openstock.RtException;

public class DatedIndex {
	private static final DateFormat ASDF = new SimpleDateFormat("yyyy_MM_dd");

	private DatedIndex(Date rDate, String idxNameC) {
		this.reportDate = rDate;
		this.indexName = idxNameC;
	}

	public Date reportDate;

	public String indexName;

	public static DatedIndex parse(String datedIndexS) {

		int idx = datedIndexS.indexOf('@');
		if (idx < 0) {
			throw new RtException("cannot parse:" + datedIndexS);
		}
		String idxName = datedIndexS.substring(0, idx);
		String dateS = datedIndexS.substring(idx + 1, datedIndexS.length());
		Date date;
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
		return this.indexName + "@" + IndexTableSheetCommand.DF.format(this.reportDate) + "";
	}

	public String as() {
		//
		return this.indexName + "_" + ASDF.format(this.reportDate);

	}

}

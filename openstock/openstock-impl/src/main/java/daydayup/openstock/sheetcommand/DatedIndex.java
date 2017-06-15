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

	protected DatedIndex(List<Date> rDateL, String idxNameC) {
		this.reportDate.addAll(rDateL);
		this.indexName = idxNameC;
	}

	private List<Date> reportDate = new ArrayList<>();

	public List<Date> getReportDateList() {
		return reportDate;
	}

	public String indexName;

	public static DatedIndex parse(String datedIndexS) {

		int idx = datedIndexS.indexOf('@');
		if (idx < 0) {
			throw new RtException("cannot parse:" + datedIndexS);
		}
		String idxName = datedIndexS.substring(0, idx);
		String dateListS = datedIndexS.substring(idx + 1, datedIndexS.length());
		String[] dateSA = dateListS.split(",");
		List<Date> dateL = new ArrayList<>();
		for (String dateS : dateSA) {

			Date date;
			try {
				date = IndexTableSheetCommand.DF.parse(dateS);
			} catch (ParseException e) {
				throw RtException.toRtException(e);
			}
			dateL.add(date);
		}

		return valueOf(dateL, idxName);
	}

	public static DatedIndex valueOf(List<Date> date, String indexName) {
		return new DatedIndex(date, indexName);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.indexName + "@");
		for(Date date:this.reportDate){
			sb.append(IndexTableSheetCommand.DF.format(date));
			sb.append(",");
		}
		return  sb.toString();
		
	}

	public String as() {
		//
		StringBuffer sb = new StringBuffer();
		sb.append(this.indexName + "_");
		for(Date date:this.reportDate){
			sb.append(ASDF.format(date));
			sb.append("_");
		}
		return  sb.toString();
		

	}

}

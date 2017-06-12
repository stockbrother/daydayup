package daydayup.openstock.sheetcommand;

import java.util.Date;

public class DatedIndex {

	public DatedIndex(Date rDate, String idxNameC) {
		this.reportDate = rDate;
		this.indexName = idxNameC;
	}

	public Date reportDate;

	public String indexName;

}

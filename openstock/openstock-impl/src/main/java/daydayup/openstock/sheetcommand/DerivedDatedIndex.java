package daydayup.openstock.sheetcommand;

import java.util.Date;

public class DerivedDatedIndex extends DatedIndex {

	private String dateVar;

	private DatedIndex parent;

	public DerivedDatedIndex(DatedIndex parent, String indexName, String date) {
		super(indexName, null);
		this.parent = parent;
		this.dateVar = date;
	}

	public String getDateVar() {
		return dateVar;
	}

	@Override
	public Date getReportDate() {		
		return parent.getReportDate();
	}
	
	

}

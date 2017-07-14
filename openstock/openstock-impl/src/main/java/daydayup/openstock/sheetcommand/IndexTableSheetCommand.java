package daydayup.openstock.sheetcommand;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import daydayup.jdbc.JdbcAccessTemplate;
import daydayup.jdbc.JdbcAccessTemplate.JdbcOperation;
import daydayup.jdbc.ResultSetProcessor;
import daydayup.openstock.BaseSheetCommand;
import daydayup.openstock.SheetCommandContext;
import daydayup.openstock.cup.IndexSqlSelectFieldsResolveContext;
import daydayup.openstock.database.Tables;
import daydayup.openstock.document.Spreadsheet;

public class IndexTableSheetCommand extends BaseSheetCommand<Object> {

	public static final DateFormat DF = new SimpleDateFormat("yyyy/MM/dd");

	@Override
	protected Object doExecute(SheetCommandContext scc) {

		Spreadsheet sheet = scc.getSheet();
		int dataRow = 0;
		String title = null;
		String scope = null;
		//
		List<DatedIndex> indexNameL = new ArrayList<>();
		List<String> indexAliasL = new ArrayList<>();
		String lastDateS = null;
		for (int i = 0; i < 100; i++) {

			String key = sheet.getText(0, i);
			if (key.equals("Data")) {
				dataRow = i;
				break;
			}

			if (key == null || key.trim().length() == 0) {
				break;
			}

			if (key.equals("Index/Date/Title")) {
				String idxS = sheet.getText(1, i);
				String dateS = sheet.getText(2, i);
				String titleS = sheet.getText(3, i);
				if(dateS == null){
					dateS = lastDateS;
				} else {
					lastDateS = dateS;
				}
				if(titleS == null){
					titleS = dateS;
				}
				indexNameL.add(DatedIndex.parse(idxS+"@"+dateS));
				indexAliasL.add(titleS);
			}
			if (key.equals("Title")) {
				title = sheet.getText(1, i);
			}
			if (key.equals("Scope")) {
				scope = sheet.getText(1, i);
			}
		}
		if (indexNameL.isEmpty()) {
			return "empty index name list";
		}

		StringBuffer sql = new StringBuffer();
		sql.append("select corpId as CORP,corpName as NAME");

		Set<Integer> typeSet = new HashSet<>();
		String corpInfoTableAlias = "ci";
		List<Object> sqlArgL = new ArrayList<>();

		for (int i = 0; i < indexNameL.size(); i++) {
			DatedIndex indexName = indexNameL.get(i);
			IndexSqlSelectFieldsResolveContext src = new IndexSqlSelectFieldsResolveContext(scc, indexName, sql,
					sqlArgL);

			src.corpInfoTableAlias = corpInfoTableAlias;
			sql.append(",");
			src.resolveSqlSelectFields();

			sql.append(" as " + indexAliasL.get(i));
			src.getReportTypeSet(typeSet, true);
		}
		// from
		int ts = 0;
		sql.append(" from " + Tables.TN_CORP_INFO + " as " + corpInfoTableAlias);
		/**
		 * <code>
		for (Integer type : typeSet) {
			if (ts > 0) {
				sql.append(",");
			}
			sql.append(Tables.getReportTable(type) + " as r" + type);
			ts++;
			</code> }
		 */

		// where join on.
		ts = 0;
		sql.append(" where 1=1");

		if (scope != null) {
			sql.append(scope);
		}

		sql.append(" order by corpId");

		int dataRowF = dataRow;
		return scc.getDataBaseService().execute(new JdbcOperation<String>() {

			@Override
			public String execute(Connection con, JdbcAccessTemplate t) {
				return t.executeQuery(con, sql.toString(), sqlArgL, new ResultSetProcessor<String>() {

					@Override
					public String process(ResultSet rs) throws SQLException {
						scc.getDocument().writeToSheet(rs, sheet, dataRowF + 1, scc.getStatusIndicator());
						return "done";
					}
				});
			}
		}, false);

	}

	protected String getTargetSheet(SheetCommandContext scc, String tableName) {
		return "" + tableName;
	}

}

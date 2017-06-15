package daydayup.openstock.sheetcommand;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.ws.Holder;

import com.sun.star.sheet.XSpreadsheet;

import daydayup.jdbc.JdbcAccessTemplate;
import daydayup.jdbc.JdbcAccessTemplate.JdbcOperation;
import daydayup.jdbc.ResultSetProcessor;
import daydayup.openstock.BaseSheetCommand;
import daydayup.openstock.CommandContext;
import daydayup.openstock.RtException;
import daydayup.openstock.SheetCommand;
import daydayup.openstock.SheetCommandContext;
import daydayup.openstock.cup.IndexSqlSelectFieldsResolveContext;
import daydayup.openstock.database.Tables;
import daydayup.openstock.util.DocUtil;

public class IndexTableSheetCommand extends BaseSheetCommand<Object> {

	public static final DateFormat DF = new SimpleDateFormat("yyyy/MM/dd");

	@Override
	protected Object doExecute(SheetCommandContext scc) {
		List<String> argL = scc.getArgumentList();
		String tableId = argL.get(0);

		Holder<String> tableName = new Holder<>();
		List<DatedIndex> indexNameL = this.getIndexNameList(scc, tableId, tableName);
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
			IndexSqlSelectFieldsResolveContext src = new IndexSqlSelectFieldsResolveContext(scc, indexName, sql, sqlArgL);
			
			src.corpInfoTableAlias = corpInfoTableAlias;
			sql.append(",");
			src.resolveSqlSelectFields();

			sql.append(" as " + indexNameL.get(i).as());
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
		sql.append(" order by corpId");

		/**
		 * 
		 * <code>
		Integer preType = null;
		for (Integer type : typeSet) {
			if (preType == null) {
				preType = type;
				continue;
			}
		
			sql.append("and r" + type + " = r" + preType);
			ts++;
		}</code>
		 */
		String targetSheetF = "" + tableName.value;
		scc.getDataBaseService().execute(new JdbcOperation<String>() {

			@Override
			public String execute(Connection con, JdbcAccessTemplate t) {
				return t.executeQuery(con, sql.toString(),sqlArgL, new ResultSetProcessor<String>() {

					@Override
					public String process(ResultSet rs) throws SQLException {
						DocUtil.writeToSheet(scc, rs, targetSheetF);
						return null;
					}
				});
			}
		}, false);

		return "done";
	}

	private List<DatedIndex> getIndexNameList(CommandContext cc, String tableId, Holder<String> tableName) {
		XSpreadsheet xSheet = DocUtil.getSpreadsheetByName(cc.getComponentContext(), SheetCommand.SN_SYS_INDEX_TABLE,
				false);
		//
		List<DatedIndex> indexNameL = new ArrayList<>();
		for (int i = 0;; i++) {
			String id = DocUtil.getText(xSheet, 0, i);

			if (id == null || id.trim().length() == 0) {
				break;
			}

			if (tableId.equals(id)) {
				tableName.value = DocUtil.getText(xSheet, "TABLE", i);
				Date rDate = null;

				for (int idx = 1;; idx++) {

					String idxNameC = DocUtil.getText(xSheet, "INDEX" + idx, i);
					if (idxNameC == null || idxNameC.trim().length() == 0) {
						break;
					}

					indexNameL.add(DatedIndex.parse(idxNameC));

				}
				break;
			}
		}
		return indexNameL;
	}

}

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

	private static final DateFormat DF = new SimpleDateFormat("yyyy/MM/dd");
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
		sql.append("select corpId as CORP,reportDate as DATE");

		Set<Integer> typeSet = new HashSet<>();

		for (int i = 0; i < indexNameL.size(); i++) {
			DatedIndex indexName = indexNameL.get(i);
			IndexSqlSelectFieldsResolveContext src = new IndexSqlSelectFieldsResolveContext(scc, indexName);
			sql.append(",");
			src.resolveSqlSelectFields(sql);

			sql.append(" as " + indexNameL.get(i));
			src.getReportTypeSet(typeSet, true);
		}

		// from
		int ts = 0;
		sql.append(" from ");
		for (Integer type : typeSet) {
			if (ts > 0) {
				sql.append(",");
			}
			sql.append(Tables.getReportTable(type) + " as r" + type);
			ts++;
		}

		// where join on.
		ts = 0;
		sql.append(" where 1=1 ");
		Integer preType = null;
		for (Integer type : typeSet) {
			if (preType == null) {
				preType = type;
				continue;
			}

			sql.append("and r" + type + " = r" + preType);
			ts++;
		}
		String targetSheetF = "" + tableName.value;
		scc.getDataBaseService().execute(new JdbcOperation<String>() {

			@Override
			public String execute(Connection con, JdbcAccessTemplate t) {
				return t.executeQuery(con, sql.toString(), new ResultSetProcessor<String>() {

					@Override
					public String process(ResultSet rs) throws SQLException {
						SheetCommand.writeToSheet(scc, rs, targetSheetF);
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
				for (int col = 2;; col++) {

					String idxNameC = DocUtil.getText(xSheet, col, i);
					if (idxNameC == null || idxNameC.trim().length() == 0) {
						break;
					}
					if (idxNameC.startsWith("INDEX")) {
						indexNameL.add(new DatedIndex(rDate, idxNameC));
					} else if (idxNameC.startsWith("Date")) {
						try {
							rDate = DF.parse(idxNameC);
						} catch (ParseException e) {
							throw RtException.toRtException(e);
						}
					}
				}
				break;
			}
		}
		return indexNameL;
	}

}

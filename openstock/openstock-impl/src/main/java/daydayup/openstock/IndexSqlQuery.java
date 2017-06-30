package daydayup.openstock;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import daydayup.jdbc.JdbcAccessTemplate;
import daydayup.jdbc.JdbcAccessTemplate.JdbcOperation;
import daydayup.jdbc.ResultSetProcessor;
import daydayup.openstock.cup.IndexSqlSelectFieldsResolveContext;
import daydayup.openstock.database.Tables;
import daydayup.openstock.sheetcommand.DatedIndex;

public class IndexSqlQuery {
	List<DatedIndex> indexNameL = new ArrayList<>();
	List<String> indexAliasL = new ArrayList<>();
	String scope;

	public IndexSqlQuery() {

	}

	public IndexSqlQuery scope(String scope) {
		this.scope = scope;
		return this;
	}

	public IndexSqlQuery addIndex(DatedIndex di, String alias) {
		this.indexNameL.add(di);
		indexAliasL.add(alias);
		return this;
	}

	public void execute(CommandContext scc, ResultSetProcessor<String> rsp) {
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

		sql.append(" from " + Tables.TN_CORP_INFO + " as " + corpInfoTableAlias);

		// where join on.

		sql.append(" where 1=1");

		if (scope != null) {
			sql.append(scope);
		}

		sql.append(" order by corpId");

		scc.getDataBaseService().execute(new JdbcOperation<String>() {
			@Override
			public String execute(Connection con, JdbcAccessTemplate t) {
				return t.executeQuery(con, sql.toString(), sqlArgL, rsp);
			}
		}, false);

	}
}

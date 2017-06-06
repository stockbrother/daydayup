package daydayup.openstock.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daydayup.jdbc.JdbcAccessTemplate;
import daydayup.jdbc.JdbcAccessTemplate.JdbcOperation;
import daydayup.jdbc.ResultSetProcessor;

public class AliasInfos {

	private Map<Integer, Map<String, Integer>> reportAliasColumnMap = new HashMap<>();

	public void initialize(Connection con, JdbcAccessTemplate t) {
		String sql = "select reportType,aliasName,columnIndex from " + Tables.TN_ALIAS_INFO + "";
		t.executeQuery(con, sql, new ResultSetProcessor<Object>() {

			@Override
			public Object process(ResultSet rs) throws SQLException {

				while (rs.next()) {
					Integer reportType = rs.getInt("reportType");
					Map<String, Integer> tc = reportAliasColumnMap.get(reportType);
					if (tc == null) {
						tc = new HashMap<>();
						reportAliasColumnMap.put(reportType, tc);
					}
					String aliasName = rs.getString("aliasName");
					Integer columnIndex = rs.getInt("columnIndex");
					tc.put(aliasName, columnIndex);
				}

				return null;
			}
		});

	}

	public List<Integer> getOrCreateColumnIndexByAliasList(DataBaseService dbs, int reportType,
			List<String> aliasList) {
		Map<String, Integer> aliasMap = reportAliasColumnMap.get(reportType);
		List<Integer> rt = new ArrayList<>();
		for (String alias : aliasList) {
			Integer columnIndex = null;
			if (aliasMap != null) {
				columnIndex = aliasMap.get(alias);
			}

			if (columnIndex == null) {

				columnIndex = dbs.execute(new JdbcOperation<Integer>() {

					@Override
					public Integer execute(Connection con, JdbcAccessTemplate t) {

						int tmpIndex = getMaxColumIndex(con, t, reportType) + 1;

						String sql = "insert into " + Tables.TN_ALIAS_INFO
								+ "(reportType,aliasName,columnIndex) values(?,?,?)";
						t.executeUpdate(con, sql, new Object[] { reportType, alias, tmpIndex });
						return tmpIndex;
					}
				}, true);

			}

			rt.add(columnIndex);
		}
		return rt;

	}

	protected int getMaxColumIndex(Connection con, JdbcAccessTemplate t, int reportType) {
		String sql = "select max(columnIndex) from " + Tables.TN_ALIAS_INFO + " where reportType=?";

		return t.executeQuery(con, sql, reportType, new ResultSetProcessor<Integer>() {

			@Override
			public Integer process(ResultSet rs) throws SQLException {
				while (rs.next()) {
					return rs.getInt(1);
				}
				return null;
			}
		});

	}

}

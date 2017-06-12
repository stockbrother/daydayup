package daydayup.openstock.database;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import daydayup.jdbc.ConnectionProvider;
import daydayup.jdbc.JdbcAccessTemplate;
import daydayup.jdbc.ResultSetProcessor;
import daydayup.openstock.RtException;

public class DataBaseService extends JdbcAccessTemplate {

	private static final Logger LOG = LoggerFactory.getLogger(DataBaseService.class);

	ConnectionProvider pool;

	private static Map<String, DataBaseService> MAP = new HashMap<>();

	private static List<DBUpgrader> upgraderList = new ArrayList<DBUpgrader>();

	static {
		upgraderList.add(new DBUpgrader_001());
		upgraderList.add(new DBUpgrader_002());
	}
	//the target data version to be upgraded to.
	private DataVersion targetDataVersion = DataVersion.V_0_0_2;

	private DataVersion dataVersion;

	private AliasInfos aliasInfos = new AliasInfos();

	private DataBaseService(ConnectionProvider pool) {
		this.pool = pool;

	}

	public static DataBaseService getInstance(File dbHome, String dbName) {

		String dbUrl = "jdbc:h2:" + dbHome.getAbsolutePath().replace('\\', '/') + "/" + dbName;
		LOG.info("dbUrl:" + dbUrl);

		DataBaseService rt = MAP.get(dbUrl);
		if (rt == null) {
			ConnectionProvider pool = H2ConnectionPoolWrapper.newInstance(dbUrl, "sa", "sa");
			rt = new DataBaseService(pool);
			rt.initialize();
			MAP.put(dbUrl, rt);
		}

		return rt;
	}

	public void mergeReport(int reportType, String corpId, Date reportDate, List<String> aliasList,
			List<BigDecimal> valueList) {

		List<Integer> columnIndexList = this.aliasInfos.getOrCreateColumnIndexByAliasList(this, reportType, aliasList);

		this.execute(new JdbcOperation<Object>() {

			@Override
			public Object execute(Connection con, JdbcAccessTemplate t) {
				StringBuffer sb = new StringBuffer();
				sb.append("merge into ");
				sb.append(Tables.getReportTable(reportType));
				sb.append("(corpId,reportDate,");
				for (int i = 0; i < columnIndexList.size(); i++) {
					Integer cIdx = columnIndexList.get(i);
					sb.append(Tables.getReportColumn(cIdx));
					if (i < columnIndexList.size() - 1) {
						sb.append(",");
					}
				}

				sb.append(")key(corpId,reportDate)values(");
				sb.append("?,?,");//
				for (int i = 0; i < columnIndexList.size(); i++) {
					sb.append("?");
					if (i < columnIndexList.size() - 1) {
						sb.append(",");
					}
				}
				sb.append(")");
				List<Object> ps = new ArrayList<Object>();
				ps.add(corpId);
				ps.add(reportDate);
				ps.addAll(valueList);
				t.executeUpdate(con, sb.toString(), ps);

				return null;
			}
		}, true);

	}

	public <T> T queryReport(int reportType, String corpId, Date reportDate, List<String> aliasList,
			ReportResultProcessor<T> rrp) {
		List<Integer> columnIndexList = this.aliasInfos.getOrCreateColumnIndexByAliasList(this, reportType, aliasList);
		return this.execute(new JdbcOperation<T>() {

			@Override
			public T execute(Connection con, JdbcAccessTemplate t) {
				StringBuffer sb = new StringBuffer();
				sb.append("select ");
				for (int i = 0; i < columnIndexList.size(); i++) {
					Integer cIdx = columnIndexList.get(i);
					sb.append(Tables.getReportColumn(cIdx));
					if (i < columnIndexList.size() - 1) {
						sb.append(",");
					}
				}

				sb.append(" from ");
				sb.append(Tables.getReportTable(reportType));
				sb.append(" where 1=1");

				List<Object> ps = new ArrayList<Object>();
				if (corpId != null) {
					sb.append(" and corpId=?");
					ps.add(corpId);
				}
				if (reportDate != null) {
					sb.append(" and reportDate=?");
					ps.add(reportDate);
				}
				return t.executeQuery(con, sb.toString(), ps, new ResultSetProcessor<T>() {

					@Override
					public T process(ResultSet rs) throws SQLException {
						return rrp.process(reportType, aliasList, rs);
					}
				});

			}
		}, false);
	}

	public boolean isReportExist(int reportType, String corpId, Date reportDate) {
		return false;
	}

	private void initialize() {

		String schema = "test";

		this.execute(new JdbcOperation<Object>() {

			@Override
			public Object execute(Connection con, JdbcAccessTemplate t) {
				final List<String> schemaList = new ArrayList<String>();
				t.executeQuery(con, "show schemas", new ResultSetProcessor<Object>() {

					@Override
					public Object process(ResultSet rs) throws SQLException {
						while (rs.next()) {
							String name = rs.getString(1);
							schemaList.add(name);
							// System.out.println(rs.getString(1));
						}
						return null;
					}
				});

				if (!schemaList.contains(schema.toUpperCase())) {
					t.executeUpdate(con, "create schema " + schema);
				}
				if (!isTableExists(con, t, Tables.TN_PROPERTY)) {
					// create property table
					{

						String sql = "create table " + Tables.TN_PROPERTY
								+ "(category varchar,key varchar,value varchar,";
						sql += "primary key(category,key))";
						t.executeUpdate(con, sql);

					}
					{
						String sql = "insert into " + Tables.TN_PROPERTY + "(category,key,value)values(?,?,?)";
						t.executeUpdate(con, sql,
								new Object[] { "core", "data-version", DataVersion.V_UNKNOW.toString() });
					}

				}

				upgrade(con, t);

				aliasInfos.initialize(con, t);
				return null;
			}
		}, true);

	}

	public <T> T execute(JdbcOperation<T> op, boolean transaction) {

		try {
			Connection con = pool.openConnection();
			try {

				if (transaction) {

					boolean oldAuto = con.getAutoCommit();
					con.setAutoCommit(false);
					try {
						return op.execute(con, this);
					} catch (Exception e) {
						con.rollback();
						throw RtException.toRtException(e);
					} finally {
						con.commit();
						con.setAutoCommit(oldAuto);
					}

				} else {
					return op.execute(con, this);
				}
			} finally {
				con.close();
			}
		} catch (SQLException e) {
			throw RtException.toRtException(e);
		}
	}

	private void upgrade(Connection con, JdbcAccessTemplate t) {
		this.dataVersion = resolveDataVersion(con, t);

		LOG.info("dataVersion:" + dataVersion + ",targetVersion:" + this.targetDataVersion);
		while (true) {
			if (this.dataVersion == this.targetDataVersion) {
				// upgrade complete
				break;
			}

			DataVersion pre = this.dataVersion;
			DataVersion dv = this.tryUpgrade(con, t);
			if (dv == null) {
				LOG.warn("cannot upgrade from:" + pre + " to target:" + this.targetDataVersion);
				break;
			}
			LOG.info("successfuly upgrade from:" + pre + " to target:" + dv);
		}

	}

	private DataVersion tryUpgrade(Connection con, JdbcAccessTemplate t) {
		DataVersion rt = null;
		for (DBUpgrader up : this.upgraderList) {
			if (this.dataVersion == up.getSourceVersion()) {
				up.upgrade(con, t);//
				rt = up.getTargetVersion();
			}
		}
		if (rt != null) {
			this.dataVersion = rt;
		}
		return rt;
	}

	private DataVersion resolveDataVersion(Connection con, JdbcAccessTemplate t) {
		String sql = "select category,key,value from " + Tables.TN_PROPERTY + " t where t.category=? and t.key=?";
		List<Object[]> ll = t.executeQuery(con, sql, new Object[] { "core", "data-version" });
		if (ll.isEmpty()) {
			throw new RtException("bad data base.");
		} else {
			Object[] row = ll.get(0);
			return DataVersion.valueOf((String) row[2]);
		}
	}

	private boolean isTableExists(Connection con, JdbcAccessTemplate t, String tableName) {
		// table_schema
		String sql = "select * from information_schema.tables where table_name=?";
		List<Object[]> ll = t.executeQuery(con, sql, tableName.toUpperCase());
		if (ll.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	public Double[] getReport(int reportType, String corpId, Date reportDate, List<String> aliasList) {

		List<Double[]> rt = this.queryReport(reportType, corpId, reportDate, aliasList,
				new DoubleArrayListReportResultProcessor());

		if (rt.isEmpty()) {
			return null;
		} else if (rt.size() == 1) {
			return rt.get(0);
		} else {
			throw RtException.toRtException("");
		}

	}
}

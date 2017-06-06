package daydayup.openstock.database;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import daydayup.jdbc.ConnectionProvider;
import daydayup.jdbc.JdbcAccessTemplate;
import daydayup.jdbc.JdbcAccessTemplate.JdbcOperation;
import daydayup.jdbc.ResultSetProcessor;
import daydayup.openstock.RtException;

public class DataBaseService {

	private static final Logger LOG = LoggerFactory.getLogger(DataBaseService.class);

	ConnectionProvider pool;

	private static Map<String, DataBaseService> MAP = new HashMap<>();

	private static List<DBUpgrader> upgraderList = new ArrayList<DBUpgrader>();
	{
		upgraderList.add(new DBUpgrader_001());
	}

	private DataVersion targetDataVersion = DataVersion.V_0_0_1;

	private DataVersion dataVersion;

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

	private void initialize() {

		String schema = "test";

		JdbcAccessTemplate t = new JdbcAccessTemplate();
		t.execute(this.pool, new JdbcOperation<Object>() {

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

				return null;
			}
		}, true);

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
}

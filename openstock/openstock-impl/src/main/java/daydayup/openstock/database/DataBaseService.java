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

public class DataBaseService {

	private static final Logger LOG = LoggerFactory.getLogger(DataBaseService.class);

	ConnectionProvider pool;

	private static Map<String, DataBaseService> MAP = new HashMap<>();

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

				for (int j = 0; j < 100; j++) {
					String sql = "create table corp_report_" + j
							+ "(corpId varchar,corpName varchar,reportDate datetime,";
					for (int i = 0; i < 200; i++) {
						sql += "d_" + i + " double,";
					}
					sql += "primary key(corpId,datetime))";
					t.executeUpdate(con, sql);
				}
				return null;
			}
		}, false);

	}

}

package daydayup.openstock.database;

import java.sql.Connection;

import daydayup.jdbc.JdbcAccessTemplate;

public class DBUpgrader_001 extends DBUpgrader {

	public DBUpgrader_001() {
		super(DataVersion.V_UNKNOW, DataVersion.V_0_0_1);
	}

	@Override
	public void doUpgrade(Connection con, JdbcAccessTemplate t) {
		// create report tables
		for (int j = 0; j < 100; j++) {
			String sql = "create table corp_report_" + j + "(corpId varchar,corpName varchar,reportDate datetime,";
			for (int i = 0; i < 200; i++) {
				sql += "d_" + i + " double,";
			}
			sql += "primary key(corpId,reportDate))";
			t.executeUpdate(con, sql);
		}
	}

}

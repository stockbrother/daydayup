package daydayup.openstock.database;

import java.sql.Connection;

import daydayup.jdbc.JdbcAccessTemplate;

public class DBUpgrader_002 extends DBUpgrader {

	public DBUpgrader_002() {
		super(DataVersion.V_0_0_1, DataVersion.V_0_0_2);
	}

	@Override
	public void doUpgrade(Connection con, JdbcAccessTemplate t) {
		//create corpInfo
		{
			String sql = "create table " + Tables.TN_CORP_INFO + "(corpId varchar,corpName varchar,";
			sql += "primary key(corpId))";
			t.executeUpdate(con, sql);
		}

	}

}

package daydayup.openstock.database;

import java.sql.Connection;

import daydayup.jdbc.JdbcAccessTemplate;

public class DBUpgrader_003 extends DBUpgrader {

	public DBUpgrader_003() {
		super(DataVersion.V_0_0_2, DataVersion.V_0_0_3);
	}

	@Override
	public void doUpgrade(Connection con, JdbcAccessTemplate t) {
		//create corpInfo
		{
			String sql = "drop table "+Tables.TN_CORP_INFO	;
			t.executeUpdate(con, sql);
		}
		{
			String sql = "create table " + Tables.TN_CORP_INFO					+ "("//
					+ "corpId varchar,"//
					+ "corpName varchar,"//
					+ "category varchar,"//
					+ "fullName varchar,"//
					+ "ipoDate datetime,"//
					+ "province varchar,"//
					+ "city varchar,"//
					+ "webSite varchar,"//
					+ "address varchar,"//					
					;
			sql += "primary key(corpId))";
			t.executeUpdate(con, sql);
		}
		

	}

}

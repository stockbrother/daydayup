package daydayup.openstock.database;

import daydayup.jdbc.JdbcAccessTemplate;

import java.sql.Connection;

public class DBUpgrader_004 extends DBUpgrader {

	public DBUpgrader_004() {
		super(DataVersion.V_0_0_3, DataVersion.V_0_0_4);
	}

	@Override
	public void doUpgrade(Connection con, JdbcAccessTemplate t) {
		//create corp_group

		{
			String sql = "create table " + Tables.TN_GROUP_INFO					+ "("//
					+ "groupId varchar,"//
					+ "groupName varchar,"//
					+ "groupType varchar,"//
					+ "userId varchar,"//
					;
			sql += "primary key(groupId))";
			t.executeUpdate(con, sql);
		}
		{
			String sql = "create table " + Tables.TN_GROUP_ITEM					+ "("//
					+ "groupId varchar,"//
					+ "corpId varchar,"//
					+ "date_ datetime,"//
					+ "formula varchar"//,
					;
			sql += ")";
			t.executeUpdate(con, sql);
		}

		{
			String sql = "create table " + Tables.TN_CHART_INFO					+ "("//
					+ "chartId varchar,"//
					+ "chartName varchar,"//
					+ "userId varchar,"//
					+ "chartType varchar,"//
					+ "corpId varchar,"//
					+ "groupId1 varchar,"//
					+ "groupId2 varchar,"//
					+ "groupId3 varchar,"//
					;
			sql += "primary key(chartId))";
			t.executeUpdate(con, sql);
		}


	}

}

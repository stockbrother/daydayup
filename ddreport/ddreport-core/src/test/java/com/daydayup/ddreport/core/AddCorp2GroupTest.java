package com.daydayup.ddreport.core;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import daydayup.AddCorpIdToGroupHandler;
import daydayup.AddGroupHandler;
import daydayup.GetGroupHandler;
import daydayup.jdbc.JdbcAccessTemplate;
import daydayup.jdbc.ResultSetProcessor;
import daydayup.openstock.DdrContext;
import daydayup.openstock.database.Tables;

public class AddCorp2GroupTest {

    private static Logger LOG = LoggerFactory.getLogger(AddCorp2GroupTest.class);
    private DdrContext old;

    @Before
    public void setUp() {
        old = DdrContext.get();
        DdrContext ddr = new TestDdrContext();
        DdrContext.set(ddr);
        //truncate data from tables

    }

    private void dumpTable(final String table) {
        DdrContext.get().getDataBaseService().execute(new JdbcAccessTemplate.JdbcOperation<Object>
                () {
            @Override
            public Object execute(Connection con, JdbcAccessTemplate t) {
                t.executeQuery(con, "select * from " + table, new ResultSetProcessor() {
                    @Override
                    public Object process(ResultSet rs) throws SQLException {
                        ResultSetMetaData rsmd = rs.getMetaData();

                        System.out.print(",");
                        for (int i = 0; i < rsmd.getColumnCount(); i++) {
                            System.out.print(rsmd.getColumnLabel(i + 1));
                            System.out.print(",");
                        }
                        System.out.println();
                        int rowNum = 0;
                        while (rs.next()) {
                            System.out.print(rowNum++);
                            System.out.print(",");
                            for (int i = 0; i < rsmd.getColumnCount(); i++) {
                                Object value = rs.getObject(i + 1);
                                System.out.print(String.valueOf(value));
                                System.out.print(",");
                            }
                            System.out.println();
                        }
                        return rowNum;
                    }
                });
                return null;
            }
        }, false);
    }

    public void testA() {
        dumpTable(Tables.TN_GROUP_INFO);
    }

    @Test
    public void testAddCorp2Group() {

        String groupName = "MyGroup1";
        AddGroupHandler handler1 = new AddGroupHandler();
        final String groupId = handler1.execute(new String[]{groupName});

        Map<String, Object> groupRow = new GetGroupHandler().execute(groupId);
        Assert.assertNotNull("no group found for id:" + groupId, groupRow);
        Assert.assertEquals(groupName, groupRow.get("GROUPNAME"));

        String corpId = "Test000001";
        AddCorpIdToGroupHandler handler = new AddCorpIdToGroupHandler();
        handler.execute(new String[]{groupId, corpId});

        final String sql = "select groupId,corpId from " + Tables.TN_GROUP_ITEM + " gi where gi.groupId=?";
        List<Object[]> data = DdrContext.get().getDataBaseService().execute(new JdbcAccessTemplate.JdbcOperation<List<Object[]>>() {

            @Override
            public List<Object[]> execute(Connection con, JdbcAccessTemplate t) {
                List<Object[]> data = t.executeQuery(con, sql, new Object[]{groupId});
                return data;
            }

        }, false);
        Assert.assertEquals(1, data.size());
        Object[] row = data.get(0);
        Assert.assertEquals(groupId, row[0]);
        Assert.assertEquals(corpId, row[1]);
    }

    @After
    public void tearDown() {
        DdrContext.set(old);
    }

}

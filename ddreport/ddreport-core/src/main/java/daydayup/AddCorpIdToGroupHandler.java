package daydayup;

import java.sql.Connection;

import daydayup.jdbc.JdbcAccessTemplate;
import daydayup.openstock.DdrContext;
import daydayup.openstock.database.DataBaseService;
import daydayup.openstock.database.Tables;

public class AddCorpIdToGroupHandler extends Handler<String[], Void> {

    @Override
    public Void execute(final String[] groupId_corpId) {


        DataBaseService db = DdrContext.get().getDataBaseService();
        final Object[] args = new Object[]{groupId_corpId[0], groupId_corpId[1], null, null};

        db.execute(new JdbcAccessTemplate.JdbcOperation<Object>() {

            @Override
            public Object execute(Connection con, JdbcAccessTemplate t) {
                //String groupId = UUID.randomUUID().toString();
                String sql = "insert into " + Tables.TN_GROUP_ITEM + "(groupId,corpId,date_,formula)values(?,?,?,?)";
                t.executeUpdate(con, sql, args);

                return null;
            }

        }, true);
        return null;
    }
}

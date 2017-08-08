package daydayup;

import daydayup.jdbc.JdbcAccessTemplate;
import daydayup.openstock.DdrContext;
import daydayup.openstock.database.DataBaseService;
import daydayup.openstock.database.Tables;

import java.sql.Connection;
import java.util.UUID;

public class AddCorpIdToGroupHandler extends Handler<String, Void> {

    @Override
    public Void execute(final String corpId) {

        DataBaseService db = DdrContext.get().getDataBaseService();
        db.execute(new JdbcAccessTemplate.JdbcOperation<Object>() {

            @Override
            public Object execute(Connection con, JdbcAccessTemplate t) {
                String groupId = UUID.randomUUID().toString();
                {

                    String sql = "insert into " + Tables.TN_GROUP_INFO + "(groupId,groupName,groupType,userId)values(?,?,?,?)";
                    t.executeUpdate(con, sql, new Object[]{groupId, "FavoriteCorps", "corps", "unknown"});
                }
                {
                    String sql = "insert into " + Tables.TN_GROUP_ITEM + "(groupId,corpId,date_,formula)values(?,?,?,?)";
                    t.executeUpdate(con, sql, new Object[]{groupId, corpId, null, null});
                }

                return null;
            }

        }, true);
        return null;
    }
}

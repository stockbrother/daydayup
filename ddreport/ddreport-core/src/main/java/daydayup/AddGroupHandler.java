package daydayup;

import java.sql.Connection;
import java.util.UUID;

import daydayup.jdbc.JdbcAccessTemplate;
import daydayup.openstock.DdrContext;
import daydayup.openstock.database.DataBaseService;
import daydayup.openstock.database.Tables;

public class AddGroupHandler extends Handler<String[], String> {

    @Override
    public String execute(final String[] groupName_groupType_userId) {

        DataBaseService db = DdrContext.get().getDataBaseService();
        return db.execute(new JdbcAccessTemplate.JdbcOperation<String>() {

            @Override
            public String execute(Connection con, JdbcAccessTemplate t) {
                String groupId = UUID.randomUUID().toString();

                String sql = "insert into " + Tables.TN_GROUP_INFO + "(groupId,groupName,groupType,userId)values(?,?,?,?)";
                t.executeUpdate(con, sql, new Object[]{groupId, groupName_groupType_userId[0], "corps", "unknown"});

                return groupId;
            }

        }, true);
    }
}

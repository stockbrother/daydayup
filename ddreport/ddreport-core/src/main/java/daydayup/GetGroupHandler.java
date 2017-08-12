package daydayup;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import daydayup.jdbc.JdbcAccessTemplate;
import daydayup.jdbc.MapRowResultSetProcessor;
import daydayup.openstock.DdrContext;
import daydayup.openstock.RtException;
import daydayup.openstock.database.DataBaseService;
import daydayup.openstock.database.Tables;

public class GetGroupHandler extends Handler<String, Map<String, Object>> {

    @Override
    public Map<String, Object> execute(final String groupId) {

        DataBaseService db = DdrContext.get().getDataBaseService();
        return db.execute(new JdbcAccessTemplate.JdbcOperation<Map<String, Object>>() {

            @Override
            public Map<String, Object> execute(Connection con, JdbcAccessTemplate t) {
                String sql = "select * from " + Tables.TN_GROUP_INFO + " where groupId = ?";
                List<Map<String, Object>> list = t.executeQuery(con, sql, new Object[]{groupId}, new MapRowResultSetProcessor());
                if (list.size() > 1) {
                    throw new RtException("too many row found for group:" + groupId);
                }
                return list.isEmpty() ? null : list.get(0);

            }

        }, false);
    }
}

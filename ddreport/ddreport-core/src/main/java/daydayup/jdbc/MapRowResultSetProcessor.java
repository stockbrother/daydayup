package daydayup.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapRowResultSetProcessor implements ResultSetProcessor<List<Map<String, Object>>> {
    @Override
    public List<Map<String, Object>> process(ResultSet rs) throws SQLException {
        List<Map<String, Object>> rt = new ArrayList<>();
        ResultSetMetaData rsmd = rs.getMetaData();
        while (rs.next()) {
            Map<String, Object> row = new HashMap<>();
            for (int i = 0; i < rsmd.getColumnCount(); i++) {
                String key = rsmd.getColumnLabel(i+1);
                Object value = rs.getObject(i+1);
                row.put(key, value);
            }
            rt.add(row);
        }

        return rt;
    }
}
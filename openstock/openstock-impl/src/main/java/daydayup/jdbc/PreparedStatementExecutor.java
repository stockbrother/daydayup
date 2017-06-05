package daydayup.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface PreparedStatementExecutor<T> {

	public T execute(PreparedStatement ps) throws SQLException;

}
package daydayup.jdbc;

import java.sql.Connection;

public class DeleteOperation extends JdbcOperation<Long> {

	private String table;

	@Override
	protected Long doExecute(Connection con) {
		return this.template.executeUpdate(con, "delete from " + table);
	}

	public DeleteOperation table(String tableName) {
		//
		this.table = tableName;
		return this;
	}

}

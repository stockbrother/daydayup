package daydayup.jdbc;

import java.sql.Connection;

public abstract class JdbcOperation<T> {
	protected JdbcDataAccessTemplate template;

	public JdbcOperation() {
		this.template = new JdbcDataAccessTemplate();
	}

	public T execute(Connection con) {
		return this.doExecute(con);
	}

	protected abstract T doExecute(Connection con);
}

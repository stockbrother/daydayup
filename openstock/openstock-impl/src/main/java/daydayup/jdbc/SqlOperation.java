package daydayup.jdbc;

public abstract class SqlOperation<T> extends JdbcOperation<T> {

	protected String sql;

	protected int parameterSize;

	public SqlOperation(String sql, int size) {
		super();
		this.sql = sql;
		this.parameterSize = size;
	}

	public int getParameterSize() {
		return this.parameterSize;
	}

	public String getSql() {
		return sql;
	}

}

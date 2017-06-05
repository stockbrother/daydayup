package daydayup.jdbc;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class InsertRowOperation extends JdbcOperation<Void> {

	private String tableName;

	private List<String> columnList = new ArrayList<String>();

	private List<Object> valueList = new ArrayList<Object>();

	public InsertRowOperation(String table) {
		super();
		this.tableName = table;
	}

	public void addValue(String name, Object value) {
		this.columnList.add(name);//
		this.valueList.add(value);
	}

	@Override
	public Void doExecute(Connection c) {
		//TODO static sql.
		StringBuffer sql = new StringBuffer().append("insert into ").append(this.tableName).append(" (");
		for (int i = 0; i < valueList.size(); i++) {
			String name = columnList.get(i);
			sql.append(name);
			if (i < valueList.size() - 1) {
				sql.append(",");
			}
		}
		sql.append(")values(");
		for (int i = 0; i < valueList.size(); i++) {
			sql.append("?");
			if (i < valueList.size() - 1) {
				sql.append(",");
			}
		}
		sql.append(")");

		this.template.executeUpdate(c, sql.toString(), valueList);
		return null;
	}

}

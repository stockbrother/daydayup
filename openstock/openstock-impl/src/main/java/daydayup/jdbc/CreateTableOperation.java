package daydayup.jdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateTableOperation extends JdbcOperation<Void> {

	// private static String SQL0 = "create table " +
	// PropertiesTable.T_PROPERTIES + "(name varchar,value varchar) ";
	private static Map<Class, String> typeMap = new HashMap<Class, String>();

	static {
		typeMap.put(String.class, "varchar");
		typeMap.put(Date.class, "datetime");
		typeMap.put(Long.class, "bigint");
		typeMap.put(BigDecimal.class, "decimal");
		typeMap.put(Integer.class, "int");
		typeMap.put(Double.class, "double");
		typeMap.put(Float.class, "real");
		

	}
	private String tableName;
	private List<Tuple2<String, Class>> columnList = new ArrayList<Tuple2<String, Class>>();
	private List<String> primaryKeyList = new ArrayList<String>();

	public CreateTableOperation(String table) {
		super();
		this.tableName = table;
	}

	@Override
	public Void doExecute(Connection c) {
		StringBuffer sql = new StringBuffer().append("create table ").append(this.tableName).append("(");
		for (int i = 0; i < columnList.size(); i++) {
			Tuple2<String, Class> column = columnList.get(i);
			sql.append(column.a);
			sql.append(" ");
			String typeS = typeMap.get(column.b);
			if (typeS == null) {
				throw new RuntimeException("not supported type:" + column.b + ",tableName:" + tableName);
			}
			sql.append(typeS);

			if (i < columnList.size() - 1) {
				sql.append(",");
			}
		}
		if (!this.primaryKeyList.isEmpty()) {
			sql.append(",primary key(");
			for (int i = 0; i < this.primaryKeyList.size(); i++) {
				String name = this.primaryKeyList.get(i);
				sql.append(name);
				if (i < this.primaryKeyList.size() - 1) {
					sql.append(",");
				}
			}
			sql.append(")");
		}
		sql.append(")");
		this.template.executeUpdate(c, sql.toString());

		return null;
	}

	public void addPrimaryKey(String name) {
		this.primaryKeyList.add(name);
	}

	public void addColumn(String name, Class type) {
		this.columnList.add(new Tuple2<String, Class>(name, type));
	}

	public void addColumns(List<Tuple2<String, Class>> columList) {
		this.columnList.addAll(columList);
	}

}
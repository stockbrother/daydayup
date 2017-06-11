package daydayup.openstock.cup;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import daydayup.jdbc.JdbcAccessTemplate;
import daydayup.jdbc.ResultSetProcessor;
import daydayup.jdbc.JdbcAccessTemplate.JdbcOperation;
import daydayup.openstock.CommandContext;
import daydayup.openstock.database.DataBaseService;
import daydayup.openstock.database.Tables;

public class IndexSqlSelectFieldsResolveContext {

	public String indexName;

	public List<ColumnIdentifier> columnIdentifierList = new ArrayList<>();

	private CommandContext commandContext;

	public IndexSqlSelectFieldsResolveContext(CommandContext commandContext) {
		this.commandContext = commandContext;
	}

	public List<ColumnIdentifier> getColumnIdentifierList() {
		return columnIdentifierList;
	}

	public CommandContext getCommandContext() {
		return commandContext;
	}

	public ColumnIdentifier addColumnIdentifierByAlias(String alias) {
		ColumnIdentifier rt = this.resolveColumnIdentifierByAlias(alias);
		this.columnIdentifierList.add(rt);
		return rt;
	}

	private ColumnIdentifier resolveColumnIdentifierByAlias(String alias) {
		DataBaseService dbs = this.commandContext.getDataBaseService();
		String sql = "select reportType,columnIndex from " + Tables.TN_ALIAS_INFO + " where aliasName = ?";

		return dbs.execute(new JdbcOperation<ColumnIdentifier>() {

			@Override
			public ColumnIdentifier execute(Connection con, JdbcAccessTemplate t) {
				return t.executeQuery(con, sql, alias, new ResultSetProcessor<ColumnIdentifier>() {

					@Override
					public ColumnIdentifier process(ResultSet rs) throws SQLException {
						while (rs.next()) {
							ColumnIdentifier rt = new ColumnIdentifier();
							rt.reportType = rs.getInt("reportType");
							rt.columnNumber = rs.getInt("columnIndex");
							return rt;
						}
						return null;
					}
				});
			}
		}, false);

	}

	public Set<Integer> getReportTypeSet() {
		//
		Set<Integer> rt = new HashSet<>();
		for (ColumnIdentifier ci : this.columnIdentifierList) {
			rt.add(ci.reportType);
		}
		return rt;
	}
}

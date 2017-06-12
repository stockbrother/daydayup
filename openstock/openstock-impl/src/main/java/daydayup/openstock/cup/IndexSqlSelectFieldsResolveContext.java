package daydayup.openstock.cup;

import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.sun.star.sheet.XSpreadsheet;

import daydayup.jdbc.JdbcAccessTemplate;
import daydayup.jdbc.JdbcAccessTemplate.JdbcOperation;
import daydayup.jdbc.ResultSetProcessor;
import daydayup.openstock.CommandContext;
import daydayup.openstock.RtException;
import daydayup.openstock.SheetCommand;
import daydayup.openstock.database.DataBaseService;
import daydayup.openstock.database.Tables;
import daydayup.openstock.sheetcommand.DatedIndex;
import daydayup.openstock.util.DocUtil;
import daydayup_openstock_cup.parser;
import daydayup_openstock_cup.scanner;
import java_cup.runtime.Symbol;

public class IndexSqlSelectFieldsResolveContext {

	public DatedIndex indexName;

	public List<ColumnIdentifier> columnIdentifierList = new ArrayList<>();

	private CommandContext commandContext;

	private IndexSqlSelectFieldsResolveContext parent;

	private List<IndexSqlSelectFieldsResolveContext> childList = new ArrayList<>();

	public String corpInfoTableAlias = "ci";

	public IndexSqlSelectFieldsResolveContext(CommandContext commandContext, DatedIndex indexName) {
		this(null, commandContext, indexName);
	}

	public IndexSqlSelectFieldsResolveContext newChild(DatedIndex indexName) {
		IndexSqlSelectFieldsResolveContext rt = new IndexSqlSelectFieldsResolveContext(this, indexName);
		this.childList.add(rt);
		return rt;
	}

	private IndexSqlSelectFieldsResolveContext(IndexSqlSelectFieldsResolveContext parent, DatedIndex indexName) {
		this(parent, parent.commandContext, indexName);
	}

	private IndexSqlSelectFieldsResolveContext(IndexSqlSelectFieldsResolveContext parent, CommandContext commandContext,
			DatedIndex indexName) {
		this.parent = parent;
		this.commandContext = commandContext;
		this.indexName = indexName;
	}

	public List<ColumnIdentifier> getColumnIdentifierList(boolean includeChildren) {
		return columnIdentifierList;
	}

	public CommandContext getCommandContext() {
		return commandContext;
	}

	private String getFormulaByIndexName(CommandContext cc, DatedIndex indexName) {
		XSpreadsheet xSheet = DocUtil.getSpreadsheetByName(cc.getComponentContext(), SheetCommand.SN_SYS_INDEX_DEFINE,
				false);
		//

		String formula = null;
		for (int i = 0;; i++) {
			String name = DocUtil.getText(xSheet, 1, i);
			if (name == null || name.trim().length() == 0) {
				break;
			}
			if (name.equals(indexName.indexName)) {
				formula = DocUtil.getText(xSheet, 2, i);
				break;
			}
		}
		return formula;
	}

	public static IndexSqlSelectFieldsResolveContext resolveSqlSelectFields(IndexSqlSelectFieldsResolveContext parent,
			CommandContext cc, DatedIndex indexName, StringBuffer sql) {
		IndexSqlSelectFieldsResolveContext src = new IndexSqlSelectFieldsResolveContext(parent, cc, indexName);
		src.resolveSqlSelectFields(sql);
		return src;
	}

	public StringBuffer resolveSqlSelectFields(StringBuffer sql) {

		String formula = this.getFormulaByIndexName(this.commandContext, this.indexName);
		if (formula == null) {
			throw new RtException("no formula found for index:" + this.indexName);
		}

		Reader r = new StringReader(formula);
		Symbol result;
		try {
			result = new parser(new scanner(r)).parse();
		} catch (Exception e) {
			throw new RtException("failed to parse formula:" + formula, e);
		}
		CupExpr expr = (CupExpr) result.value;
		expr.resolveSqlSelectFields4Index(null, this, sql);

		return sql;
	}

	public void addColumnIdentifier(ColumnIdentifier rt) {
		this.columnIdentifierList.add(rt);
	}

	public ColumnIdentifier getColumnIdentifierByAlias(String alias) {
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

	public Set<Integer> getReportTypeSet(Set<Integer> set, boolean recusive) {
		//
		for (ColumnIdentifier ci : this.columnIdentifierList) {
			set.add(ci.reportType);
			if (recusive) {
				for (IndexSqlSelectFieldsResolveContext c : this.childList) {
					c.getReportTypeSet(set, true);
				}
			}
		}
		return set;
	}
}

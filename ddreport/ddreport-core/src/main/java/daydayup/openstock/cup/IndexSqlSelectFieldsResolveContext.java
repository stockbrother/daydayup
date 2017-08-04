package daydayup.openstock.cup;

import daydayup.jdbc.JdbcAccessTemplate;
import daydayup.jdbc.JdbcAccessTemplate.JdbcOperation;
import daydayup.jdbc.ResultSetProcessor;
import daydayup.openstock.CommandContext;
import daydayup.openstock.RtException;
import daydayup.openstock.database.DataBaseService;
import daydayup.openstock.database.Tables;
import daydayup.openstock.sheetcommand.DatedIndex;
import daydayup_openstock_cup.parser;
import daydayup_openstock_cup.scanner;
import java_cup.runtime.Symbol;

import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class IndexSqlSelectFieldsResolveContext {

    private DatedIndex datedIndex;

    public DatedIndex getDatedIndex() {
        return datedIndex;
    }

    public List<ColumnIdentifier> columnIdentifierList = new ArrayList<>();

    private CommandContext commandContext;

    private IndexSqlSelectFieldsResolveContext parent;

    private List<IndexSqlSelectFieldsResolveContext> childList = new ArrayList<>();

    public String corpInfoTableAlias = "ci";

    private Map<String, Object> variableMap = new HashMap<String, Object>();

    private List<Object> sqlArgumentList;

    private StringBuffer buf;

    public List<Object> getSqlArgumentList() {
        return sqlArgumentList;
    }

    public StringBuffer getBuf() {
        return buf;
    }

    public IndexSqlSelectFieldsResolveContext(CommandContext commandContext, DatedIndex indexName, StringBuffer sql,
                                              List<Object> argL) {
        this(null, commandContext, indexName, sql, argL);
    }

    public IndexSqlSelectFieldsResolveContext newChild(DatedIndex indexName) {
        IndexSqlSelectFieldsResolveContext rt = new IndexSqlSelectFieldsResolveContext(this, indexName, this.buf,
                this.sqlArgumentList);
        this.childList.add(rt);
        return rt;
    }

    private IndexSqlSelectFieldsResolveContext(IndexSqlSelectFieldsResolveContext parent, DatedIndex indexName,
                                               StringBuffer sql, List<Object> argL) {
        this(parent, parent.commandContext, indexName, sql, argL);
    }

    private IndexSqlSelectFieldsResolveContext(IndexSqlSelectFieldsResolveContext parent, CommandContext commandContext,
                                               DatedIndex indexName, StringBuffer sql, List<Object> argL) {
        this.buf = sql;
        this.sqlArgumentList = argL;
        this.parent = parent;
        this.commandContext = commandContext;
        this.datedIndex = indexName;
    }

    public List<ColumnIdentifier> getColumnIdentifierList(boolean includeChildren) {
        return columnIdentifierList;
    }

    public CommandContext getCommandContext() {
        return commandContext;
    }

    private String getFormulaByIndexName(CommandContext cc, String indexName) {
        //Spreadsheet xSheet = cc.getSpreadsheetByName(SheetCommand.SN_SYS_INDEX_DEFINE, false);
        return null;//
    }

    public static IndexSqlSelectFieldsResolveContext resolveSqlSelectFields(IndexSqlSelectFieldsResolveContext parent,
                                                                            CommandContext cc, DatedIndex indexName, StringBuffer sql, List<Object> argL) {
        IndexSqlSelectFieldsResolveContext src = new IndexSqlSelectFieldsResolveContext(parent, cc, indexName, sql,
                argL);
        src.resolveSqlSelectFields();
        return src;
    }

    public void resolveSqlSelectFields() {
        String idxName = this.datedIndex.indexName;

        String formula = this.getFormulaByIndexName(this.commandContext, idxName);

        if (formula == null) {//not defined with a name.so define it as anonymous,if it is an raw index.
            formula = idxName;
            //throw new RtException("no formula found for index:" + this.datedIndex);
        }

        Reader r = new StringReader(formula);
        Symbol result;
        try {
            result = new parser(new scanner(r)).parse();
        } catch (Exception e) {
            throw new RtException("failed to parse formula:" + formula, e);
        } catch (Error e) {
            throw new RtException("failed to parse formula:" + formula, e);
        }
        CupExpr expr = (CupExpr) result.value;
        expr.resolveSqlSelectFields4Index(null, this);

    }

    public void addColumnIdentifier(ColumnIdentifier rt) {
        this.columnIdentifierList.add(rt);
    }

    public ColumnIdentifier getColumnIdentifierByAlias(final String alias, final boolean force) {
        DataBaseService dbs = this.commandContext.getDataBaseService();
        final String sql = "select reportType,columnIndex from " + Tables.TN_ALIAS_INFO + " where aliasName = ?";

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
                        if (force) {
                            throw new RtException("not found alias:" + alias + " from table:" + Tables.TN_ALIAS_INFO);
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

    public Object setVariable(String key, Object value) {
        return this.variableMap.put(key, value);
    }

    public Object getVariable(String dateVar, boolean force) {
        Object rt = this.variableMap.get(dateVar);
        if (rt == null) {
            if (parent != null) {
                rt = parent.getVariable(dateVar, false);
            }
        }
        if (rt == null && force) {
            throw new RtException("no var found:" + dateVar);
        }
        return rt;
    }

    public void addSqlArgument(Object arg) {
        this.sqlArgumentList.add(arg);
    }
}

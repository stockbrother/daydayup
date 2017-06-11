package daydayup.openstock.cup;

import daydayup.openstock.database.Tables;

public abstract class CupExpr {
	public static final int PLUS = 1;
	public static final int MINUS = 2;
	public static final int TIMES = 3;
	public static final int DIV = 4;

	public static class CupExprBinary extends CupExpr {
		int oper;
		CupExpr exprLeft;
		CupExpr exprRight;

		public CupExprBinary(int pLUS, CupExpr e1, CupExpr e2) {
			this.oper = pLUS;
			this.exprLeft = e1;
			this.exprRight = e2;
		}

		@Override
		public StringBuffer resolveSqlSelectFields4Index(IndexSqlSelectFieldsResolveContext cc, StringBuffer buf) {
			exprLeft.resolveSqlSelectFields4Index(cc, buf);
			String opStr = null;
			switch (this.oper) {
			case PLUS:
				opStr = "+";
				break;
			case MINUS:
				opStr = "-";
				break;
			case TIMES:
				opStr = "*";
				break;
			case DIV:
				opStr = "/";
				break;

			}
			buf.append(opStr);
			exprRight.resolveSqlSelectFields4Index(cc, buf);
			return buf;
		}

	}

	public static class CupExprNumber extends CupExpr {
		Integer value;

		CupExprNumber(Integer value) {
			this.value = value;
		}

		@Override
		public StringBuffer resolveSqlSelectFields4Index(IndexSqlSelectFieldsResolveContext cc, StringBuffer buf) {
			buf.append(value);
			return buf;
		}

	}

	public static class CupExprIdentifier extends CupExpr {
		String value;

		CupExprIdentifier(String value) {
			this.value = value;
		}

		@Override
		public StringBuffer resolveSqlSelectFields4Index(IndexSqlSelectFieldsResolveContext src, StringBuffer buf) {
			ColumnIdentifier ci = src.addColumnIdentifierByAlias(this.value);
			
			buf.append("r" + ci.reportType + "." + Tables.getReportColumn(ci.columnNumber));

			return buf;
		}

	}

	public static class CupExprParen extends CupExpr {
		CupExpr expr;

		public CupExprParen(CupExpr e) {

			this.expr = e;
		}

		@Override
		public StringBuffer resolveSqlSelectFields4Index(IndexSqlSelectFieldsResolveContext cc, StringBuffer buf) {
			buf.append("(");
			expr.resolveSqlSelectFields4Index(cc, buf);
			buf.append(")");
			return buf;
		}

	}

	public static CupExpr plus(CupExpr e1, CupExpr e2) {
		return new CupExprBinary(PLUS, e1, e2);
	}

	public static CupExpr minus(CupExpr e1, CupExpr e2) {
		return new CupExprBinary(MINUS, e1, e2);
	}

	public static CupExpr times(CupExpr e1, CupExpr e2) {
		return new CupExprBinary(TIMES, e1, e2);
	}

	public static CupExpr div(CupExpr e1, CupExpr e2) {
		return new CupExprBinary(DIV, e1, e2);
	}

	public static CupExpr minus(CupExpr e) {
		return new CupExprBinary(PLUS, null, e);
	}

	public static CupExpr paren(CupExpr e) {
		return new CupExprParen(e);
	}

	public static CupExpr number(Integer value) {
		return new CupExprNumber(value);
	}

	public static CupExpr identifier(String value) {
		return new CupExprIdentifier(value);
	}

	public abstract StringBuffer resolveSqlSelectFields4Index(IndexSqlSelectFieldsResolveContext cc, StringBuffer buf);
}

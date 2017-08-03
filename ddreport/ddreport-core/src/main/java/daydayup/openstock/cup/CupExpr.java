package daydayup.openstock.cup;

import java.util.Calendar;
import java.util.Date;

import daydayup.openstock.database.Tables;

/**
 * Expression recognized by CUP parser.
 * 
 * @author wuzhen
 *
 */
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
		public void resolveSqlSelectFields4Index(CupExpr parent, IndexSqlSelectFieldsResolveContext cc) {

			StringBuffer buf = cc.getBuf();
			exprLeft.resolveSqlSelectFields4Index(this, cc);
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
			cc.getBuf().append(opStr);
			if (this.oper == DIV) {
				buf.append(" nullif(");//
			}
			exprRight.resolveSqlSelectFields4Index(this, cc);
			if (this.oper == DIV) {
				buf.append(",0)");
			}

		}

	}

	public static class CupExprNumber extends CupExpr {
		Integer value;

		CupExprNumber(Integer value) {
			this.value = value;
		}

		@Override
		public void resolveSqlSelectFields4Index(CupExpr parent, IndexSqlSelectFieldsResolveContext cc) {
			cc.getBuf().append(value);
		}

	}

	/**
	 * Index Expression, for instance: RateA@date0
	 * 
	 * @author wuzhen
	 *
	 */
	public static class CupExprIndex extends CupExpr {

		String identifier;

		String dateVar;

		CupExprIndex(String identifier, String dateS) {
			this.identifier = identifier;
			this.dateVar = dateS;
		}

		private int getDateArgIndex() {
			if (dateVar == null) {
				return 0;
			}
			String rtI = dateVar.substring("date".length());
			return Integer.parseInt(rtI);
		}

		@Override
		public void resolveSqlSelectFields4Index(CupExpr parent, IndexSqlSelectFieldsResolveContext src) {

			StringBuffer buf = src.getBuf();

			ColumnIdentifier ci = src.getColumnIdentifierByAlias(this.identifier, true);

			String field = " r." + Tables.getReportColumn(ci.columnNumber) + "";

			buf.append("(");//
			buf.append("select");//
			buf.append(" ifnull(" + field + ",0)");//
			buf.append(" from " + Tables.getReportTable(ci.reportType) + " as r");//
			buf.append(" where r.corpId = " + src.corpInfoTableAlias + ".corpId");
			// buf.append(" and r.reportDate = PARSEDATETIME('"+ dateLiteral +
			// "','yyyy/MM/dd')");
			buf.append(" and r.reportDate = ?");

			int argI = getDateArgIndex();

			Date date = resolveDate(src.getDatedIndex().getReportDate(), argI);

			src.addSqlArgument(date);

			buf.append(")");
		}

		protected Date resolveDate(Date date0, int idx) {
			Calendar c = Calendar.getInstance();
			c.setTime(date0);
			c.add(Calendar.YEAR, -idx);
			return c.getTime();
		}

		public StringBuffer xresolveSqlSelectFields4Index(IndexSqlSelectFieldsResolveContext src, StringBuffer buf) {
			ColumnIdentifier ci = src.getColumnIdentifierByAlias(this.identifier, false);
			if (ci == null) {// is not a alias, it must be an index defined in
								// sheet.
				buf.append("(");
				Date date = null;
				// try {
				// date = IndexTableSheetCommand.DF.parse(this.dateLiteral);
				// } catch (ParseException e) {
				// throw RtException.toRtException(e);
				// }

				// DerivedDatedIndex did = new
				// DerivedDatedIndex(this.identifier,"date");
				IndexSqlSelectFieldsResolveContext childSrc = src.newChild(null);
				childSrc.resolveSqlSelectFields();
				buf.append(")");

			} else {
				src.addColumnIdentifier(ci);
				buf.append("r" + ci.reportType + "." + Tables.getReportColumn(ci.columnNumber));
			}

			return buf;
		}

	}

	public static class CupExprParen extends CupExpr {
		CupExpr expr;

		public CupExprParen(CupExpr e) {

			this.expr = e;
		}

		@Override
		public void resolveSqlSelectFields4Index(CupExpr parent, IndexSqlSelectFieldsResolveContext cc) {
			cc.getBuf().append("(");
			expr.resolveSqlSelectFields4Index(this, cc);
			cc.getBuf().append(")");
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

	public static CupExpr index(String identifier, String date) {
		return new CupExprIndex(identifier, date);
	}

	public abstract void resolveSqlSelectFields4Index(CupExpr parent, IndexSqlSelectFieldsResolveContext cc);
}

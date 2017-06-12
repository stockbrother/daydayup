package daydayup.openstock.cup;

import java.text.ParseException;
import java.util.Date;

import daydayup.openstock.RtException;
import daydayup.openstock.database.Tables;
import daydayup.openstock.sheetcommand.DatedIndex;
import daydayup.openstock.sheetcommand.IndexTableSheetCommand;

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
		public StringBuffer resolveSqlSelectFields4Index(CupExpr parent, IndexSqlSelectFieldsResolveContext cc,
				StringBuffer buf) {

			exprLeft.resolveSqlSelectFields4Index(this, cc, buf);

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

			exprRight.resolveSqlSelectFields4Index(this, cc, buf);

			return buf;
		}

	}

	public static class CupExprNumber extends CupExpr {
		Integer value;

		CupExprNumber(Integer value) {
			this.value = value;
		}

		@Override
		public StringBuffer resolveSqlSelectFields4Index(CupExpr parent, IndexSqlSelectFieldsResolveContext cc,
				StringBuffer buf) {
			buf.append(value);
			return buf;
		}

	}

	public static class CupExprIndex extends CupExpr {

		String identifier;

		String dateLiteral;

		CupExprIndex(String identifier, String dateS) {
			this.identifier = identifier;
			this.dateLiteral = dateS;
		}

		@Override
		public StringBuffer resolveSqlSelectFields4Index(CupExpr parent, IndexSqlSelectFieldsResolveContext src,
				StringBuffer buf) {
			boolean isDivRight = false;
			if (parent != null && parent instanceof CupExprBinary) {
				CupExprBinary ceb = (CupExprBinary) parent;
				if (ceb.oper == DIV && this == ceb.exprRight) {
					isDivRight = true;
				}
			}
			ColumnIdentifier ci = src.getColumnIdentifierByAlias(this.identifier);
			String field = " r." + Tables.getReportColumn(ci.columnNumber) + "";

			buf.append("(");//
			buf.append("select");//
			if (isDivRight) {
				buf.append(" casewhen(" + field + "=0,NULL," + field + ")");
			} else {
				buf.append(" ifnull(" + field + ",0)");//
			}

			buf.append(" from " + Tables.getReportTable(ci.reportType) + " as r");//
			buf.append(" where r.corpId = " + src.corpInfoTableAlias + ".corpId and r.reportDate = PARSEDATETIME('"
					+ dateLiteral + "','yyyy/MM/dd')")//
					.append(")")//
					;
			return buf;
		}

		public StringBuffer xresolveSqlSelectFields4Index(IndexSqlSelectFieldsResolveContext src, StringBuffer buf) {
			ColumnIdentifier ci = src.getColumnIdentifierByAlias(this.identifier);
			if (ci == null) {// is not a alias, it must be an index defined in
								// sheet.
				buf.append("(");
				Date date = null;
				try {
					date = IndexTableSheetCommand.DF.parse(this.dateLiteral);
				} catch (ParseException e) {
					throw RtException.toRtException(e);
				}

				DatedIndex di = DatedIndex.valueOf(date, this.identifier);
				IndexSqlSelectFieldsResolveContext childSrc = src.newChild(di);
				childSrc.resolveSqlSelectFields(buf);
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
		public StringBuffer resolveSqlSelectFields4Index(CupExpr parent, IndexSqlSelectFieldsResolveContext cc,
				StringBuffer buf) {
			buf.append("(");
			expr.resolveSqlSelectFields4Index(this, cc, buf);
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

	public static CupExpr index(String identifier, String date) {
		return new CupExprIndex(identifier, date);
	}

	public abstract StringBuffer resolveSqlSelectFields4Index(CupExpr parent, IndexSqlSelectFieldsResolveContext cc,
			StringBuffer buf);
}

package daydayup.openstock.cup;

import java.util.Date;

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

	public static class CupExprIndex extends CupExpr {

		String identifier;

		String dateVar;

		CupExprIndex(String identifier, String dateS) {
			this.identifier = identifier;
			this.dateVar = dateS;
		}
		
		private int getDateArgIndex(){
			if(dateVar == null){
				return 0;
			}
			String rtI = dateVar.substring("date".length());
			return Integer.parseInt(rtI);
		}

		@Override
		public void resolveSqlSelectFields4Index(CupExpr parent, IndexSqlSelectFieldsResolveContext src) {

			StringBuffer buf = src.getBuf();

			ColumnIdentifier ci = src.getColumnIdentifierByAlias(this.identifier);
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
			
			Date date = src.getDatedIndex().getReportDateList().get(argI);
			
			src.addSqlArgument(date);

			buf.append(")");
		}

		public StringBuffer xresolveSqlSelectFields4Index(IndexSqlSelectFieldsResolveContext src, StringBuffer buf) {
			ColumnIdentifier ci = src.getColumnIdentifierByAlias(this.identifier);
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

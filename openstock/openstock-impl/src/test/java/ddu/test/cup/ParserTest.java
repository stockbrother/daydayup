package ddu.test.cup;

import java.io.Reader;

import daydayup.openstock.CommandContext;
import daydayup.openstock.cup.CupExpr;
import daydayup_openstock_cup.parser;
import daydayup_openstock_cup.scanner;
import java_cup.runtime.Symbol;
import junit.framework.TestCase;

public class ParserTest extends TestCase {
	public void test() throws Exception {
		CommandContext cc = null;
		{

			Reader r = new java.io.StringReader("负债合计/资产总计");
			Symbol result = new parser(new scanner(r)).parse();
			CupExpr expr = (CupExpr) result.value;
			System.out.println(expr);
			//System.out.println("result:" + expr.resolveSql(cc, new StringBuffer()));
		}
		{

			Reader r = new java.io.StringReader("(1+1)*2+3");
			Symbol result = new parser(new scanner(r)).parse();
			CupExpr expr = (CupExpr) result.value;
			//System.out.println("result:" + expr.resolveSql(cc, new StringBuffer()));
		}

		{

			Reader r = new java.io.StringReader("(a+b)*c+d");
			Symbol result = new parser(new scanner(r)).parse();
			CupExpr expr = (CupExpr) result.value;
			//System.out.println("result:" + expr.resolveSql(cc, new StringBuffer()));
		}

		{

			Reader r = new java.io.StringReader("(指标1+指标2)*指标3+指标x");
			Symbol result = new parser(new scanner(r)).parse();
			CupExpr expr = (CupExpr) result.value;
			//System.out.println("result:" + expr.resolveSql(cc, new StringBuffer()));
		}
	}

}

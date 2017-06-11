package ddu.test.formula;

import java.io.Reader;

import daydayup.openstock.cup.CupExpr;
import daydayup_openstock_cup.parser;
import daydayup_openstock_cup.scanner;
import java_cup.runtime.Symbol;
import junit.framework.TestCase;

public class FormulaParserTest extends TestCase {
	public void test() throws Exception {
		{

			Reader r = new java.io.StringReader("1+1");
			Symbol result = new parser(new scanner(r)).parse();
			CupExpr expr = (CupExpr) result.value;
			System.out.println("result:" + expr.resolve(new StringBuffer()));
		}
		{

			Reader r = new java.io.StringReader("(1+1)*2+3");
			Symbol result = new parser(new scanner(r)).parse();
			CupExpr expr = (CupExpr) result.value;
			System.out.println("result:" + expr.resolve(new StringBuffer()));
		}
		
		{

			Reader r = new java.io.StringReader("(a+b)*c+d");
			Symbol result = new parser(new scanner(r)).parse();
			CupExpr expr = (CupExpr) result.value;
			System.out.println("result:" + expr.resolve(new StringBuffer()));
		}
	}

}

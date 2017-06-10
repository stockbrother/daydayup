package ddu.test.formula;

import daydayup.openstock.formula.parser.ParseException;
import daydayup.openstock.formula.parser.Parser;
import junit.framework.TestCase;

public class FormulaParserTest extends TestCase {
	public void test() throws ParseException {
		Object result = new Parser(new java.io.StringReader("(1+1)*2")).S();
		System.out.println(result);
	}
}

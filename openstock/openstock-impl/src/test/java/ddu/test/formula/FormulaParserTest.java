package ddu.test.formula;

import java.io.Reader;

import daydayup_openstock_formula_parser.parser;
import daydayup_openstock_formula_parser.scanner;
import junit.framework.TestCase;

public class FormulaParserTest extends TestCase {
	public void test() throws Exception {
		Reader r = new java.io.StringReader("1+1");
		Object result = new parser(new scanner(r)).parse();
		System.out.println(result);
		
	}
}

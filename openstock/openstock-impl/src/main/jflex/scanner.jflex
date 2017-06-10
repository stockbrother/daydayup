
package daydayup_openstock_formula_parser;

import java_cup.runtime.*;

%%

%public
%class scanner
%unicode
%cup
%line
%column

%{
	StringBuffer string = new StringBuffer();

	private Symbol symbol(int type) {
		return new Symbol(type, yyline, yycolumn);
	}
	private Symbol symbol(int type, Object value) {
		return new Symbol(type, yyline, yycolumn, value);
	}
%}

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace     = {LineTerminator} | [ \t\f]

Identifier = [:jletter:] [:jletterdigit:]*

DecIntegerLiteral = 0 | [1-9][0-9]*

%%

// keywords
	
<YYINITIAL> {
	// literals 
    {DecIntegerLiteral}            { return symbol(sym.NUMBER); }
    // operators     
    "+"                            { return symbol(sym.PLUS); }
	"-"                            { return symbol(sym.MINUS); }
	"*"                            { return symbol(sym.TIMES); }
	"/"                            { return symbol(sym.UMINUS); }
	"("                            { return symbol(sym.LPAREN); }
	")"                            { return symbol(sym.RPAREN); }
      
    // whitespace
    {WhiteSpace}                   {  }
}

    // error fallback
[^]                              { throw new Error("Illegal character <"+yytext()+">"); }

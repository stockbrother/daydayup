package com.daydayup.ddreport.core;

import daydayup.openstock.RtException;
import daydayup.openstock.cup.CupExpr;
import daydayup_openstock_cup.parser;
import daydayup_openstock_cup.scanner;
import java_cup.runtime.Symbol;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.Reader;
import java.io.StringReader;

import org.junit.Assert;

/**
 *
 */

public class CupExprParserTest {
   @Test
   public void testUseCup(){
      String formula = "A_@date0";
      System.out.println(formula);
      Reader r = new StringReader(formula);
      Symbol result;
      try {
         result = new parser(new scanner(r)).parse();
      } catch (Exception e) {
         throw new RtException("failed to parse formula:" + formula, e);
      }
      CupExpr expr = (CupExpr) result.value;
      Assert.assertNotNull("",expr);
      Assert.assertTrue(expr instanceof CupExpr.CupExprIndex);
   }
}

package com.daydayup.ddreport;

import android.support.test.runner.AndroidJUnit4;
import daydayup.openstock.RtException;
import daydayup.openstock.cup.CupExpr;
import daydayup_openstock_cup.parser;
import daydayup_openstock_cup.scanner;
import java_cup.runtime.Symbol;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.Reader;
import java.io.StringReader;

/**
 *
 */
@RunWith(AndroidJUnit4.class)
public class CupInstrumentedTest {
   @Test
   public void testUseCup(){
      String formula = "A@2016/12/31";
      Reader r = new StringReader(formula);
      Symbol result;
      try {
         result = new parser(new scanner(r)).parse();
      } catch (Exception e) {
         throw new RtException("failed to parse formula:" + formula, e);
      }
      CupExpr expr = (CupExpr) result.value;
   }
}

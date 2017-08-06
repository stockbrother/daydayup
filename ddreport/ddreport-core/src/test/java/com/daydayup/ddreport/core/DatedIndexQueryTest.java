package com.daydayup.ddreport.core;

import daydayup.openstock.CommandContext;
import daydayup.openstock.DdrContext;
import daydayup.openstock.SimpleDdrContext;
import daydayup.openstock.cup.IndexSqlSelectFieldsResolveContext;
import daydayup.openstock.sheetcommand.DatedIndex;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DatedIndexQueryTest {

    private static Logger LOG = LoggerFactory.getLogger(DatedIndexQueryTest.class);

    @Test
    public void testParseAndQuery(){
        DdrContext ddr = new SimpleDdrContext();
        CommandContext cc = new CommandContext(ddr);
        String idx = "A_资产总计@date0";

        DatedIndex di = DatedIndex.valueOf(idx,"2016/12/31");
        StringBuffer sql = new StringBuffer();
        List<Object> args = new ArrayList<>();

        IndexSqlSelectFieldsResolveContext rc = new IndexSqlSelectFieldsResolveContext(cc,di,sql,args);
        rc.resolveSqlSelectFields();
        System.out.println(""+sql);
    }
}

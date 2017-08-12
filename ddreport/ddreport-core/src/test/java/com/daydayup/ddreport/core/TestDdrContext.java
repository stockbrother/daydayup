package com.daydayup.ddreport.core;

import java.io.File;

import daydayup.openstock.DdrContext;

public class TestDdrContext extends DdrContext {

    public TestDdrContext() {

    }

    @Override
    public File getDbFolder() {
        return new File("build" + File.separator + "test-db");
    }

    @Override
    public String getDbName() {
        return "test";
    }
}

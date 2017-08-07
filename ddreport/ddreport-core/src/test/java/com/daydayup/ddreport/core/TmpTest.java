package com.daydayup.ddreport.core;

import daydayup.csvloader.SseCorpInfoLoadHandler;
import daydayup.openstock.SimpleDdrContext;
import org.junit.Test;

public class TmpTest {

    @Test
    public void load() {

        if(System.getProperty("isRunInIde") == null){
            System.out.println("cannot test tmp.");
            return;
        }

        SimpleDdrContext dc = new SimpleDdrContext();

        dc.getHandlerService().handle(SseCorpInfoLoadHandler.class, "c:\\openstock\\sse\\sse.corplist.csv");

        System.out.println("tmp tested.");
    }
}

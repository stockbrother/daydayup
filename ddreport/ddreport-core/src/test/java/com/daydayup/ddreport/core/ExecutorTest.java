package com.daydayup.ddreport.core;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExecutorTest {
    @Test
    public void testExectutor() throws Exception{
        ExecutorService es = Executors.newCachedThreadPool();

        Future<Object> f =  es.submit(new Callable<Object>
                () {
            @Override
            public Object call() throws Exception {
                return "done";
            }
        });
        Object rst = f.get();
        Assert.assertEquals("done",rst);

    }
}

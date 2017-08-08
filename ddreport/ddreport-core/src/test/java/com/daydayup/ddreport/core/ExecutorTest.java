package com.daydayup.ddreport.core;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.*;

public class ExecutorTest {
    public static class MyThread extends Thread{
        public MyThread(Runnable r){
            super(r);
        }
        @Override
        public void run(){
            super.run();
        }
    }

    public static class MyThreadFactory implements ThreadFactory{

        @Override
        public Thread newThread(Runnable r) {
            return new MyThread(r);
        }
    }
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

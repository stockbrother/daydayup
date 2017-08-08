package com.daydayup.ddreport;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(AndroidJUnit4.class)
public class ContextActivityTest extends ActivityTestRule<TmpActivity> {

    private static final Logger LOG = LoggerFactory.getLogger(ContextActivityTest.class);

    public ContextActivityTest() {
        super(TmpActivity.class);
    }


    @Override
    protected void beforeActivityLaunched() {


        super.beforeActivityLaunched();

        LOG.info("beforeActivityLaunched,thread:" + Thread.currentThread().getName());

    }

    @Override
    protected void afterActivityLaunched() {

        super.afterActivityLaunched();
        LOG.info("afterActivityLaunched,thread:" + Thread.currentThread().getName());
    }

    @Override
    protected void afterActivityFinished() {
        super.afterActivityFinished();
        LOG.info("afterActivityFinished,thread:" + Thread.currentThread().getName());
    }

    @Test
    public void testExecuteAsync() throws Throwable {

        LOG.info("testExecuteAsync,thread:" + Thread.currentThread().getName());
        this.launchActivity(null);

        //ActivityContext ac = ActivityContext.get();
        //Assert.assertNotNull(ac);
//        Future<Object> f = ActivityContext.executeAsync(new Callable<Object>
//                () {
//            @Override
//            public Object call() throws Exception {
//
//                return "done";
//            }
//        });
//
//        Object rt = f.get();
//        Assert.assertEquals("done", rt);

    }
}

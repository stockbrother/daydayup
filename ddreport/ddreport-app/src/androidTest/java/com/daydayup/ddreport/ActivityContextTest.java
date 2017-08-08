package com.daydayup.ddreport;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(AndroidJUnit4.class)
public class ActivityContextTest extends ActivityTestRule<TmpActivity> {

    private static final Logger LOG = LoggerFactory.getLogger(ActivityContextTest.class);

    public ActivityContextTest() {
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
        this.apply(new Statement() {
            @Override
            public void evaluate() throws Throwable {

                doTestExecuteAsync();

            }
        }, Description.createTestDescription(ActivityContext.class, "testExecuteAsync")).evaluate();

    }

    private void doTestExecuteAsync() throws Exception {
        LOG.info("doTestExecuteAsync");
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

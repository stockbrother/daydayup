package com.daydayup.ddreport;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(AndroidJUnit4.class)
public class EmptyActivityTest extends ActivityTestRule<SearchActivity> {

    private static final Logger LOG = LoggerFactory.getLogger(EmptyActivityTest.class);

    public EmptyActivityTest() {
        super(SearchActivity.class);
    }

    @Override
    protected void beforeActivityLaunched() {
//        ActivityLifecycleMonitorRegistry.getInstance().addLifecycleCallback(new ActivityLifecycleCallback() {
//            @Override
//            public void onActivityLifecycleChanged(Activity activity, Stage stage) {
//                LOG.warn("state:" + stage);
//                if (stage == Stage.PRE_ON_CREATE) {
//                    LOG.warn("set flag for screen on.");
//                    activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//                }
//            }
//        });

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

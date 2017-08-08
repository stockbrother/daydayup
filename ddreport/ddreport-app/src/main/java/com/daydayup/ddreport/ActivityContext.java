package com.daydayup.ddreport;

import android.app.Activity;
import daydayup.Callback;
import daydayup.Handler;
import daydayup.openstock.DdrContext;
import daydayup.openstock.DdrContext.DdrThread;
import daydayup.openstock.RtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class ActivityContext implements ThreadFactory {
    private static final Logger LOG = LoggerFactory.getLogger(ActivityContext.class);
    public static class ActivityThread extends DdrThread {
        ActivityContext activityContext;

        public ActivityThread(ActivityContext ctx) {
            super(ctx.ddr);
            this.activityContext = ctx;
        }

        @Override
        public void run() {
            ActivityContext.set(this.activityContext);

            super.run();
        }
    }


    private static ThreadLocal<ActivityContext> THREAD_LOCAL = new ThreadLocal<>();

    private Activity activity;

    private DdrContext ddr;

    private ExecutorService executor = Executors.newCachedThreadPool(this);

    public ActivityContext(Activity activity, DdrContext ddr) {
        this.activity = activity;
        this.ddr = ddr;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new ActivityThread(this);
    }

    public static ActivityContext get() {
        return get(true);
    }

    public static ActivityContext get(boolean force) {
        ActivityContext rt = THREAD_LOCAL.get();
        if (force && rt == null) {

            LOG.error("ActivityContext not set in thread local of thread:" + Thread.currentThread().getName());

            //throw new RtException("ActivityContext not set in thread local of thread:" + Thread.currentThread().getName());
        }
        return rt;

    }

    public static void set(ActivityContext act) {
        THREAD_LOCAL.set(act);
        if(LOG.isInfoEnabled()){
            LOG.info("set ActivityContext for thread:"+Thread.currentThread().getName());
        }
    }

    public Activity getActivity() {
        return this.activity;
    }

    public DdrContext getDdrContext(){
        return this.ddr;
    }

    public static <T> Future<T> executeAsync(Callable<T> callable) {
        ActivityContext act = ActivityContext.get(true);
        return act.executor.submit(callable);
    }

    public static <T, R> R executeSync(Handler<T, R> handler, T arg) {
        return handler.execute(arg);
    }

    public static <T, R> void executeAsync(final Handler<T, R> handler, final T arg, final Callback<R> callback) {
        executeAsync(handler, arg, callback, true);
    }

    public static <T, R> void executeAsync(final Handler<T, R> handler, final T arg, final Callback<R> callback, final boolean callbackRunOnUiThread) {

        executeAsync(new Callable<Void>() {
            @Override
            public Void call() throws Exception {

                final R rst = executeSync(handler, arg);
                if (callbackRunOnUiThread) {
                    ActivityContext.get().getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onResult(rst);
                        }
                    });
                } else {
                    callback.onResult(rst);
                }

                return null;
            }
        });

    }

    public static <T, R, H extends Handler<T, R>> R executeSync(Class<H> handlerClass, T arg) {

        try {
            return handlerClass.newInstance().execute(arg);
        } catch (InstantiationException e) {
            throw new RtException(e);
        } catch (IllegalAccessException e) {
            throw new RtException(e);
        }
    }

    public static <T, R, H extends Handler<T, R>> Future<R> executeAsync(final Class<H> handlerClass, final T arg) {
        return executeAsync(new Callable<R>() {
            @Override
            public R call() throws Exception {

                return executeSync(handlerClass, arg);
            }
        });
    }

    public static <T, R> void executeAsync(final UiTask<T, R> task, T arg) {
        executeAsync(task, arg, task, true);
    }
}

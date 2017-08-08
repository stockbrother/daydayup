package daydayup.openstock;

import daydayup.openstock.database.DataBaseService;

import java.io.File;
import java.util.concurrent.ThreadFactory;

public abstract class DdrContext implements ThreadFactory {

    public static class DdrThread extends Thread {
        DdrContext ddr;

        public DdrThread(DdrContext ddr) {
            this.ddr = ddr;
        }

        @Override
        public void run() {
            DdrContext.set(this.ddr);
            super.run();
        }
    }

    private static ThreadLocal<DdrContext> THREAD_LOCAL = new ThreadLocal<>();

    protected DataBaseService dataBase;
//
//    protected BackGroundTaskScheduler background;

    private Object lock = new Object();

    public abstract File getDbFolder();

    public abstract String getDbName();


    protected DdrContext() {
//        this.background = new BackGroundTaskScheduler();
//        this.background.runTask(new Callable<Object>() {
//            @Override
//            public Object call() throws Exception {
//                doInitInBackground();
//                return null;
//            }
//        });
    }


    private void doInitInBackground() {

        getDataBaseService();//init DB for time saving.

    }
//
//    public BackGroundTaskScheduler getBackGroundTaskScheduler() {
//        return this.background;
//    }

    public boolean isReady() {
        return this.dataBase != null;
    }

    public void utilReady() {
        this.getDataBaseService();
    }

    public DataBaseService getDataBaseService() {

        if (dataBase == null) {
//            synchronized (lock) {
//                if (dataBase == null) {
//
//                    File dbHome = getDbFolder();
//                    String dbName = getDbName();
//                    dataBase = DataBaseService.getInstance(dbHome, dbName);
//                }
//            }

        }
        return dataBase;
    }

    @Override
    public Thread newThread(Runnable r) {
        DdrThread rt = new DdrThread(this);
        return rt;
    }


    public static DdrContext get() {
        return THREAD_LOCAL.get();
    }

    public static void set(DdrContext ddr) {
        THREAD_LOCAL.set(ddr);
    }
}

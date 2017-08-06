package daydayup.openstock;

import daydayup.HandlerService;
import daydayup.openstock.database.DataBaseService;

import java.io.File;
import java.util.concurrent.Callable;

public abstract class DdrContext {
    protected DataBaseService dataBase;

    protected HandlerService handler;

    protected BackGroundTaskScheduler background;

    private Object lock = new Object();

    public abstract File getDbFolder();

    public abstract String getDbName();


    protected DdrContext() {
        this.handler = new HandlerService(this);
        this.background = new BackGroundTaskScheduler();
        this.background.runTask(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                doInitInBackground();
                return null;
            }
        });
    }

    private void doInitInBackground() {

        getDataBaseService();//init DB for time saving.

    }

    public BackGroundTaskScheduler getBackGroundTaskScheduler() {
        return this.background;
    }

    public boolean isReady() {
        return this.dataBase != null;
    }

    public void utilReady() {
        this.getDataBaseService();
    }

    public HandlerService getHandlerService() {
        return this.handler;
    }

    public DataBaseService getDataBaseService() {

        if (dataBase == null) {
            synchronized (lock) {
                if (dataBase == null) {

                    File dbHome = getDbFolder();
                    String dbName = getDbName();
                    dataBase = DataBaseService.getInstance(dbHome, dbName);
                }
            }

        }
        return dataBase;
    }
}

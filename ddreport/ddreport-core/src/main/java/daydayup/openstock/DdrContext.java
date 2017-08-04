package daydayup.openstock;

import daydayup.openstock.database.DataBaseService;

import java.io.File;

public abstract class DdrContext {
    DataBaseService dataBase;

    public abstract File getDbFolder();

    public abstract String getDbName();

    public DataBaseService getDataBaseService() {
        if(dataBase == null){
            File dbHome = getDbFolder();
            String dbName = getDbName();
            dataBase= DataBaseService.getInstance(dbHome,dbName);

        }
        return dataBase;
    }
}

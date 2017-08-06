package daydayup.openstock;

import java.io.File;

public class SimpleDdrContext extends DdrContext{

    public SimpleDdrContext() {

    }


    @Override
    public File getDbFolder() {
        return new File("c:\\openstock");
    }

    @Override
    public String getDbName() {
        return "openstock";
    }
}

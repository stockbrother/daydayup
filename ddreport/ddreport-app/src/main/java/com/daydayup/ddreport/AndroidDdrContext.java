package com.daydayup.ddreport;

import android.os.Environment;
import daydayup.openstock.DdrContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class AndroidDdrContext extends DdrContext {

    private static final Logger LOG = LoggerFactory.getLogger(AndroidDdrContext.class);

    private static AndroidDdrContext ME;

    private static Object LOCK = new Object();

    private AndroidDdrContext() {
        super();
    }

    public static AndroidDdrContext getInstance() {
        if (ME == null) {
            synchronized (LOCK) {
                if (ME == null) {
                    ME = new AndroidDdrContext();
                }

            }
        }
        return ME;
    }

    @Override
    public File getDbFolder() {
        File storage = Environment.getExternalStorageDirectory();

        File file = new File(new File(storage, ".ddreport"), "h2");
        if (!file.exists()) {
            LOG.warn("no folder found: " + file.getAbsolutePath());
            return null;
        }
        return file;
    }

    @Override
    public String getDbName() {
        return "test";
    }
}

package com.daydayup.ddreport;

import android.content.Context;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import daydayup.jdbc.JdbcAccessTemplate;
import daydayup.openstock.database.DataBaseService;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.sql.Connection;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 * <p>
 * Run with command line: gradlew connectedAndroidTest, do not try to running this under IDE env.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.daydayup.ddreport", appContext.getPackageName());
        //assertTrue("test failure",false);

        System.out.println("test done:" + ExampleInstrumentedTest.class.getName());

    }

    @Test
    public void testH2DB() {
        File storage = Environment.getExternalStorageDirectory();

        File file = new File(new File(storage, ".ddreport"), "h2");

        if (!file.exists()) {
            assertTrue("no folder found: " + file.getAbsolutePath(),false);
        } else {
            DataBaseService dbs = DataBaseService.getInstance(file, "test");
            String rt = dbs.execute(new JdbcAccessTemplate.JdbcOperation<String>() {

                @Override
                public String execute(Connection con, JdbcAccessTemplate t) {

                    return "done";
                }
            }, false);
        }
    }
}

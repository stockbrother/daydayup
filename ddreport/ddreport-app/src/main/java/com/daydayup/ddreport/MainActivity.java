package com.daydayup.ddreport;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import daydayup.jdbc.JdbcAccessTemplate;
import daydayup.openstock.database.DataBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private static final Logger LOG = LoggerFactory.getLogger(MainActivity.class);
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    onTest();
                    return true;
            }
            return false;
        }

    };

    private void onTest() {
        mTextMessage.setText(R.string.title_notifications + ",please wait....");
        File storage = Environment.getExternalStorageDirectory();

        File file = new File(new File(storage, ".ddreport"), "h2");
        if (!file.exists()) {
            LOG.warn("no folder found: " + file.getAbsolutePath());

        } else {
            DataBaseService dbs = DataBaseService.getInstance(file, "test");
            String rt = dbs.execute(new JdbcAccessTemplate.JdbcOperation<String>() {

                @Override
                public String execute(Connection con, JdbcAccessTemplate t) {

                    return "done";
                }
            }, false);
            mTextMessage.setText(R.string.title_notifications + ",rt:" + rt);
        }
    }

    public void onTestx() {
        LOG.info("onTest");
        File file = new File(this.getBaseContext().getFilesDir(), "h2");
        File f2 = file;
        while (f2.exists()) {
            long i = System.currentTimeMillis();
            f2 = new File(this.getBaseContext().getFilesDir(), "h2_backup" + i);
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {

            }

        }
        if (f2 != file) {

            file.renameTo(f2);
            file = new File(this.getBaseContext().getFilesDir(), "h2");
            LOG.info("back up db folder to:" + f2.getAbsolutePath());
        }
        File[] files = file.listFiles();
        if (files != null) {

            for (File f : files) {
                LOG.info(f.getAbsolutePath());
            }
        }

        DataBaseService dbs = DataBaseService.getInstance(file, "test");
        String rt = dbs.execute(new JdbcAccessTemplate.JdbcOperation<String>() {

            @Override
            public String execute(Connection con, JdbcAccessTemplate t) {

                return "done";
            }
        }, false);
        mTextMessage.setText(R.string.title_notifications);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}

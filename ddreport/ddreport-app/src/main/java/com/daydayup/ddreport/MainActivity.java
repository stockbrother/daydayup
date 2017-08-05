package com.daydayup.ddreport;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import daydayup.jdbc.JdbcAccessTemplate;
import daydayup.openstock.database.DataBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;

public class MainActivity extends AppCompatActivity {


    private static final Logger LOG = LoggerFactory.getLogger(MainActivity.class);
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    return true;
                case R.id.navigation_dashboard:

                    return true;
                case R.id.navigation_notifications:
                    onTest();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        {

            Button button = (Button) this.findViewById(R.id.corpCompareButtonView);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this, CorpComparePlotActivity.class));
                }
            });
        }
        {

            Button button = (Button) this.findViewById(R.id.corpFindButtonView);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this, CorpFindActivity.class));
                }
            });
        }

        BottomNavigationView navigation = (BottomNavigationView) this.findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void onTest() {


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
        //        mTextMessage.setText(R.string.title_notifications);
    }

}

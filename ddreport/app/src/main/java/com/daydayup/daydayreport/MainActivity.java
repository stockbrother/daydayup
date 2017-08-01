package com.daydayup.daydayreport;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;

import daydayup.jdbc.JdbcAccessTemplate;
import daydayup.openstock.database.DataBaseService;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private static final Logger LOG = LoggerFactory.getLogger(MainActivity.class);

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            LOG.info("onNavigationItemSelected.");
            switch (item.getItemId()) {

                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
                case R.id.test:
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
        this.getBaseContext().getFilesDir();
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    protected void onTest(){
        File file = new File(this.getBaseContext().getFilesDir(),"h2");
        LOG.error("onTest,file:"+file.getAbsolutePath());
        String rt =file.getAbsolutePath();

        DataBaseService dbs = DataBaseService.getInstance(file,"test");

        rt += dbs.execute(new JdbcAccessTemplate.JdbcOperation<String>() {

            @Override
            public String execute(Connection con, JdbcAccessTemplate t) {

                return "done";
            }
        },false);

        mTextMessage.setText(R.string.test+",file:"+file.getAbsolutePath());

    }

}

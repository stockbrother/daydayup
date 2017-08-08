package com.daydayup.ddreport;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import daydayup.openstock.DdrContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseActivity extends AppCompatActivity {

    private static final Logger LOG = LoggerFactory.getLogger(BaseActivity.class);

    protected ActivityContext activityContext;

    protected static DdrContext ddr = AndroidDdrContext.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activityContext = new ActivityContext(this, ddr);
        ActivityContext.set(activityContext);


    }
    @Override
    protected void onDestroy() {
        ActivityContext.set(null);
        super.onDestroy();
    }
}

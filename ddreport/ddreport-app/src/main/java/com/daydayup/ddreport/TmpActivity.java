package com.daydayup.ddreport;

/**
 * Created by wuzhen on 8/1/2017.
 */

import android.os.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple XYPlot
 */
public class TmpActivity extends BaseActivity {
    private static final Logger LOG = LoggerFactory.getLogger(TmpActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LOG.info("onCreate");
        super.onCreate(savedInstanceState);
        //not work
//        if (BuildConfig.DEBUG) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
//        }
    }
    @Override
    protected void onDestroy(){
        LOG.info("onDestroy");
        super.onDestroy();
    }
}
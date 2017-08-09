package com.daydayup.ddreport;

/**
 * Created by wuzhen on 8/1/2017.
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import org.ddu.ddr.app.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple XYPlot
 */
public class CorpFindActivity extends AppCompatActivity {
    private static final Logger LOG = LoggerFactory.getLogger(CorpFindActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.corp_find);

    }

}
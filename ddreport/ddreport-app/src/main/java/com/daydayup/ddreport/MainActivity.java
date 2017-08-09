package com.daydayup.ddreport;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import daydayup.csvloader.SseCorpInfoLoadHandler;
import org.ddu.ddr.app.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class MainActivity extends BaseActivity {

    public static class MyDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Build the dialog and set up the button click handlers
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Please wait!");
            return builder.create();
        }
    }

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

                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init context


        //
        setContentView(R.layout.activity_main);
        //toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        //main view
        initContentView();
        //bottom bar
        BottomNavigationView navigation = (BottomNavigationView) this.findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        final AndroidDdrContext ctx = AndroidDdrContext.getInstance();
        if (!ctx.isReady()) {


            // final Semaphore showAndCancel = new Semaphore(0);
            AsyncTask task = new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] objects) {
                    DialogFragment dia = new MyDialogFragment();
                    dia.show(MainActivity.this.getFragmentManager(), "missiles");

                    try {
                        ctx.utilReady();
                    } finally {
                        // showAndCancel.release();
                    }
                    dia.dismissAllowingStateLoss();

                    return null;
                }
            };
            task.execute();


        }
    }

    private void initContentView() {
        {

            Button button = (Button) this.findViewById(R.id.corpCompareChartButton);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Toast.makeText(this, "Search selected", Toast.LENGTH_SHORT)
                        .show();
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
                return true;

            case R.id.action_settings:
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT)
                        .show();
                return true;

            case R.id.action_favorite:
                Toast.makeText(this, "Favorite selected", Toast.LENGTH_SHORT)
                        .show();
                return true;
            case R.id.action_loadcsv:
                Toast.makeText(this, "Favorite selected", Toast.LENGTH_SHORT)
                        .show();

                File storage = Environment.getExternalStorageDirectory();
                String file = storage.getAbsolutePath() + File.separator + ".ddreport" + File.separator + "sse" + File.separator + "sse.corplist.csv";

                ActivityContext.executeAsync(SseCorpInfoLoadHandler.class, file);

                Toast.makeText(this, "Load done.", Toast.LENGTH_SHORT)
                        .show();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

}

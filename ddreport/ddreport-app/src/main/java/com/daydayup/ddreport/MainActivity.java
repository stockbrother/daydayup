package com.daydayup.ddreport;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import daydayup.jdbc.JdbcAccessTemplate;
import daydayup.jdbc.ResultSetProcessor;
import daydayup.openstock.CommandContext;
import daydayup.openstock.database.DataBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem searchViewMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchViewMenuItem.getActionView();
        ImageView v = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_button);
        //v.setImageResource(R.drawable.search_icon); //Changing the image
        String searchFor = "";
        if (!searchFor.isEmpty()) {
            searchView.setIconified(false);
            searchView.setQuery(searchFor, false);
        }

        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                doSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {

                }
                return false;
            }
        });
        return true;
    }

    private void doSearch(final String query) {
        AndroidDdrContext dc = AndroidDdrContext.getInstance();
        CommandContext scc = new CommandContext(dc);

        scc.getDataBaseService().execute(new JdbcAccessTemplate.JdbcOperation<Object>() {

            @Override
            public Object execute(Connection con, JdbcAccessTemplate t) {

                doSearch(query, con, t);
                return null;
            }
        }, false);

    }

    private void doSearch(String text, Connection con, JdbcAccessTemplate t) {
        String sql = "select corpId,corpName from corp_info t where t.corpId like ? or t.corpName like ? limit 15";
        String like = "%" + text + "%";
        t.executeQuery(con, sql, new Object[]{like, like}, new ResultSetProcessor<Object>() {

            @Override
            public Object process(ResultSet rs) throws SQLException {
                while (rs.next()) {

                    String corpId = rs.getString(1);
                    String corpName = rs.getString(2);

                    Toast.makeText(MainActivity.this, "Corp Found:" + corpId + "/" + corpName, Toast.LENGTH_SHORT)
                            .show();
                }
                return null;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT)
                        .show();
                return true;

            case R.id.action_favorite:
                Toast.makeText(this, "Favorite selected", Toast.LENGTH_SHORT)
                        .show();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
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

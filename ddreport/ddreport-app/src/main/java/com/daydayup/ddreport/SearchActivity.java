package com.daydayup.ddreport;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;
import daydayup.jdbc.JdbcAccessTemplate;
import daydayup.jdbc.ResultSetProcessor;
import daydayup.openstock.CommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SearchActivity extends AppCompatActivity {
    private static final Logger LOG = LoggerFactory.getLogger(SearchActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_toolbar, menu);
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

                    Toast.makeText(SearchActivity.this, "Corp Found:" + corpId + "/" + corpName, Toast.LENGTH_SHORT)
                            .show();
                }
                return null;
            }
        });
    }
}

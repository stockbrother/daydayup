package com.daydayup.ddreport;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.*;
import daydayup.AddCorpIdToGroupHandler;
import daydayup.Callback;
import daydayup.jdbc.JdbcAccessTemplate;
import daydayup.jdbc.ResultSetProcessor;
import org.ddu.ddr.app.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity {
    private static final Logger LOG = LoggerFactory.getLogger(SearchActivity.class);
    private static final int COLS = 3;

    public static class CorpGridAdapter extends BaseAdapter {
        private Context context;
        private List<Object[]> rowList = new ArrayList<>();
        private int cols = COLS;

        public CorpGridAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return rowList.size() * cols;
        }

        @Override
        public Object getItem(int i) {
            int r = i / cols;
            Object[] row = rowList.get(r);
            return row[i % cols];
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            TextView text = null;
            if (view == null) {
                text = new TextView(this.context);
            } else {
                text = (TextView) view;
            }
            Object obj = this.getItem(i);
            text.setText(String.valueOf(obj));

            return text;
        }

        public int getRowNum(int position) {
            return position / cols;
        }

        public int getColNum(int position) {
            return position % cols;
        }

        public String getCorpIdIfClickPlus(int position) {
            int colNum = getColNum(position);
            if (colNum != 2) {//not plus
                return null;
            }

            int rowN = getRowNum(position);
            Object[] row = this.rowList.get(rowN);
            String corpId = (String) row[0];
            return corpId;
        }
    }

    CorpGridAdapter gridAdapter;
    GridView grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        LOG.info("onCreate,thread:" + Thread.currentThread().getName());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        //gridView
        grid = (GridView) findViewById(R.id.gridView);
        grid.setNumColumns(COLS);
        this.gridAdapter = new CorpGridAdapter(this);
        grid.setAdapter(this.gridAdapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    final int position, long id) {
                final String corpId = SearchActivity.this.gridAdapter.getCorpIdIfClickPlus(position);
                Toast.makeText(SearchActivity.this, "" + position + ",corpIdIfPlus:" + corpId,
                        Toast.LENGTH_SHORT).show();

                ActivityContext.executeAsync(new AddCorpIdToGroupHandler(), corpId, new Callback<Void>() {
                    @Override
                    public void onResult(Void rst) {
                        Toast.makeText(SearchActivity.this, "Done of adding corp:" + corpId,
                                Toast.LENGTH_SHORT).show();


                    }
                });

            }
        });

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
                search(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {

                }
                search(newText);
                return false;
            }
        });
        return true;
    }

    private void search(final String query) {
        LOG.info("search:" + query);
        ActivityContext.executeAsync(new UiTask<String, Object>() {
            @Override
            public Object execute(String arg) {
                LOG.info("execute,arg:" + arg);
                ActivityContext.get().getDdrContext().getDataBaseService().execute(new JdbcAccessTemplate.JdbcOperation<Object>() {

                    @Override
                    public Object execute(Connection con, JdbcAccessTemplate t) {

                        doSearch(query, con, t);
                        return null;
                    }
                }, false);

                return null;
            }

            @Override
            public void onResult(Object rst) {
                SearchActivity.this.gridAdapter.notifyDataSetChanged();
            }
        }, query);
    }


    private void doSearch(String text, Connection con, JdbcAccessTemplate t) {
        LOG.info("doSearch,text:" + text);
        this.gridAdapter.rowList.clear();

        String sql = "select corpId,corpName,'+' from corp_info t where t.corpId like ? or t.corpName like ? limit 15";
        String like = "%" + text + "%";
        t.executeQuery(con, sql, new Object[]{like, like}, new ResultSetProcessor<Object>() {

            @Override
            public Object process(ResultSet rs) throws SQLException {
                StringBuffer sb = new StringBuffer();
                while (rs.next()) {

                    String corpId = rs.getString(1);
                    String corpName = rs.getString(2);
                    String plus = rs.getString(3);
                    SearchActivity.this.gridAdapter.rowList.add(new Object[]{corpId, corpName, plus});
                    sb.append(corpId).append(corpName);
                }
                //Exception if add following code in a none-ui thread
                //Toast.makeText(SearchActivity.this, "Corps Found:" + sb.toString(), Toast.LENGTH_SHORT)
                //       .show();

                return null;
            }
        });


    }
}

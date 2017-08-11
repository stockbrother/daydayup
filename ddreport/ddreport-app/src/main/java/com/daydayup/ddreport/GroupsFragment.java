package com.daydayup.ddreport;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.ddu.ddr.app.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import daydayup.jdbc.JdbcAccessTemplate;
import daydayup.jdbc.ResultSetProcessor;

/**
 * Created by wu on 8/11/2017.
 */

public class GroupsFragment extends Fragment{
    private static final Logger LOG = LoggerFactory.getLogger(SearchActivity.class);
    public static class GroupsGridAdapter extends BaseAdapter {
        private Context context;
        private List<Object[]> rowList = new ArrayList<>();
        private int cols = COLS;

        public GroupsGridAdapter(Context context) {
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
    private static final int COLS = 3;
    GridView grid;
    GroupsGridAdapter gridAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        ViewGroup vg = (ViewGroup) inflater.inflate(
                R.layout.groups_fragment, container, false);

        grid = (GridView) vg.findViewById(R.id.groupGridView);
        grid.setNumColumns(COLS);
        final Context ctx = this.getActivity();
        this.gridAdapter = new GroupsGridAdapter(ctx);
        grid.setAdapter(this.gridAdapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    final int position, long id) {
                final String corpId = gridAdapter.getCorpIdIfClickPlus(position);
                Toast.makeText(ctx, "" + position + ",corpIdIfPlus:" + corpId,
                        Toast.LENGTH_SHORT).show();

            }
        });
        this.search("001");
        return vg;
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
                gridAdapter.notifyDataSetChanged();
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
                    gridAdapter.rowList.add(new Object[]{corpId, corpName, plus});
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

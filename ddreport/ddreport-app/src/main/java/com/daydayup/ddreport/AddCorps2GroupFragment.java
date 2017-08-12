package com.daydayup.ddreport;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import org.ddu.ddr.app.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import daydayup.AddCorpIdToGroupHandler;
import daydayup.Callback;

/**
 * Created by wu on 8/11/2017.
 */

public class AddCorps2GroupFragment extends BaseSqlQueryGridFragment {

    private static final Logger LOG = LoggerFactory.getLogger(AddCorps2GroupFragment.class);
    private static String GROUP_ID = "groupId";

    public static final AddCorps2GroupFragment newInstance(String groupId) {

        AddCorps2GroupFragment f = new AddCorps2GroupFragment();
        f.groupId = groupId;
        LOG.info("groupId:" + groupId);
        Bundle bdl = new Bundle(1);

        bdl.putString(GROUP_ID, groupId);

        f.setArguments(bdl);

        return f;

    }

    private String groupId;

    @Override
    protected void onCreate(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState, ViewGroup vg, GridView grid) {
        super.onCreate(inflater, container, savedInstanceState, vg, grid);
        if (savedInstanceState != null) {
            groupId = (String) savedInstanceState.get(GROUP_ID);
        }


    }

    @Override
    protected void onCellClick(int rowNum, int colNum, Object[] row) {
        if (colNum != 2) {

           // return;
        }
        final String corpId = (String) row[0];
        ActivityContext.executeAsync(new AddCorpIdToGroupHandler(), corpId, new Callback<Void>() {
            @Override
            public void onResult(Void rst) {
                Toast.makeText(getActivity(), "Done of adding corp:" + corpId,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected int getLayoutViewId() {
        return R.layout.add_corps_2_group_fragment;
    }

    @Override
    protected int getGridViewId() {
        return R.id.gridView;
    }

    @Override
    protected int getCols() {
        return 3;
    }

    @Override
    protected String getSql() {
        String sql = "select corpId,corpName,'+' from corp_info t where t.corpId like ? or t.corpName like ? limit 15";
        return sql;
    }

    @Override
    protected Object[] getSqlArguments() {
        String like = "%" + 001 + "%";
        return new Object[]{like, like};
    }


}

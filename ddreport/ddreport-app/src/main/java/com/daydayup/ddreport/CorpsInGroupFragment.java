package com.daydayup.ddreport;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.ddu.ddr.app.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import daydayup.openstock.database.Tables;

/**
 * Created by wu on 8/11/2017.
 */

public class CorpsInGroupFragment extends BaseSqlQueryGridFragment {

    private static final Logger LOG = LoggerFactory.getLogger(CorpsInGroupFragment.class);
    private static String GROUP_ID = "groupId";
    public static final CorpsInGroupFragment newInstance(String groupId)
    {

        CorpsInGroupFragment f = new CorpsInGroupFragment();

        Bundle bdl = new Bundle(1);

        bdl.putString(GROUP_ID, groupId);

        f.setArguments(bdl);

        return f;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(savedInstanceState != null){
            groupId = (String)savedInstanceState.get(GROUP_ID);
        }
        return super.onCreateView(inflater,container,savedInstanceState);
    }

    private String groupId ;

    @Override
    protected int getLayoutViewId() {
        return R.layout.corps_in_group_fragment;
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
        return "select groupId, corpId, '+' from " + Tables.TN_GROUP_ITEM + " t where groupId = ?";


    }

    @Override
    protected Object[] getSqlArguments(){
        return new Object[]{this.groupId};
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}

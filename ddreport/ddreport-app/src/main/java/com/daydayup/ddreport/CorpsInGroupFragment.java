package com.daydayup.ddreport;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

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

    public static final CorpsInGroupFragment newInstance(String groupId) {

        CorpsInGroupFragment f = new CorpsInGroupFragment();
        f.groupId = groupId;
        LOG.info("groupId:" + groupId);
        Bundle bdl = new Bundle(1);

        bdl.putString(GROUP_ID, groupId);

        f.setArguments(bdl);

        return f;

    }

    @Override
    protected void onCreate(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState, ViewGroup vg, GridView grid) {
        super.onCreate(inflater, container, savedInstanceState, vg, grid);
        if (savedInstanceState != null) {
            groupId = (String) savedInstanceState.get(GROUP_ID);
        }
        Button button ;
        button = (Button) vg.findViewById(R.id.open_plot_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPlot();
            }
        });

        button = (Button) vg.findViewById(R.id.add_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddCorp();
            }
        });

    }
    private void openAddCorp(){
        AddCorps2GroupFragment newFragment = AddCorps2GroupFragment.newInstance(this.groupId);
        FragmentTransaction transaction = this.getFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragments, newFragment);
        transaction.addToBackStack(null);

// Commit the transaction
        transaction.commit();
    }
    private void openPlot(){
        CorpsInGroupPlotFragment newFragment = CorpsInGroupPlotFragment.newInstance(this.groupId);
        FragmentTransaction transaction = this.getFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragments, newFragment);
        transaction.addToBackStack(null);

// Commit the transaction
        transaction.commit();
    }

    @Override
    protected void onCellClick(int rowNum, int colNum, Object[] row) {

    }

    private String groupId;

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
    protected Object[] getSqlArguments() {
        return new Object[]{this.groupId};
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}

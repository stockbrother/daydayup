package com.daydayup.ddreport;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.ddu.ddr.app.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wu on 8/11/2017.
 */

public class HomeFragment extends Fragment {
    private static final Logger LOG = LoggerFactory.getLogger(HomeFragment.class);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        ViewGroup vg = (ViewGroup) inflater.inflate(
                R.layout.home_fragment, container, false);
        {

            Button button = (Button) vg.findViewById(R.id.my_corp_groups_button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                openGroupsFragment();
                }
            });
        }
        {

            Button button = (Button) vg.findViewById(R.id.find_corp_button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
        return vg;
    }

    private void openGroupsFragment(){
        // Create fragment and give it an argument specifying the article it should show
        GroupsFragment newFragment = new GroupsFragment();
        Bundle args = new Bundle();
        //args.putInt(GroupsFragment., position);
        newFragment.setArguments(args);

        FragmentTransaction transaction = this.getFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragments, newFragment);
        transaction.addToBackStack(null);

// Commit the transaction
        transaction.commit();
    }


}

package com.daydayup.ddreport;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.ddu.ddr.app.R;

/**
 * Created by wuzhen on 8/11/2017.
 */

public class ScreenSlidePageFragment  extends Fragment {

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    public ScreenSlidePageFragment(){

    }

    public static final ScreenSlidePageFragment newInstance(String message)
    {

        ScreenSlidePageFragment f = new ScreenSlidePageFragment();

        Bundle bdl = new Bundle(1);

        bdl.putString(EXTRA_MESSAGE, message);

        f.setArguments(bdl);

        return f;

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String message = getArguments().getString(EXTRA_MESSAGE);
        ViewGroup v = (ViewGroup) inflater.inflate(
                R.layout.fragment_screen_slide_page, container, false);
        TextView messageTextView = (TextView)v.findViewById(R.id.textView);

        messageTextView.setText(message);
        return v;
    }

}

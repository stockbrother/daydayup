package com.daydayup.ddreport;

import daydayup.Callback;
import daydayup.Handler;

public abstract class UiTask<T, R> extends Handler<T, R> implements Callback<R>{

    protected boolean isAsnyc = true;

    public boolean isAsnyc(){
        return isAsnyc;
    }

}

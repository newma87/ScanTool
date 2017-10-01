package com.newma.scantool;

import android.app.Application;

import com.googlecode.tesseract.android.TessBaseAPI;

/**
 * Created by newma on 17-9-11.
 */

public class MyApplicatioin extends Application {
    private static TessBaseAPI s_tessApi;
    public TessBaseAPI getTess() {
        return s_tessApi;
    }

    public void setTess(TessBaseAPI api) {
        s_tessApi = api;
    }

    @Override
    public void onTerminate() {
        if (s_tessApi != null) {
            s_tessApi.clear();
            s_tessApi.end();
            s_tessApi = null;
        }
        super.onTerminate();
    }
}

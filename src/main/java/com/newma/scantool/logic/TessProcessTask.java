package com.newma.scantool.logic;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.util.ArrayList;

/**
 * Created by newma on 17-9-11.
 */

public class TessProcessTask extends AsyncTask<Bitmap, Integer, ArrayList<String>> {
    public interface TessProcessCallback {
        void onProgressCallback(int processed, int total);
        void onResultCallback(ArrayList<String> results);
    }

    TessBaseAPI m_tessApi = null;
    TessProcessCallback m_callback = null;

    public TessProcessTask(TessBaseAPI api) {
        m_tessApi = api;
    }

    public void setCallback(TessProcessCallback callback) {
        m_callback = callback;
    }

    @Override
    protected ArrayList<String> doInBackground(Bitmap... bitmaps) {
        ArrayList<String> results = new ArrayList<>();
        int i = 0;
        for (Bitmap bitmap : bitmaps) {
            m_tessApi.setImage(bitmap);
            String res = m_tessApi.getUTF8Text();
            results.add(res);
            publishProgress(++i, bitmaps.length);

            if (isCancelled()) break;
        }
        return results;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (m_callback != null) {
            m_callback.onProgressCallback(values[0], values[1]);
        }
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(ArrayList<String> strings) {
        if (m_callback != null) {
            m_callback.onResultCallback(strings);
        }
        super.onPostExecute(strings);
    }
}

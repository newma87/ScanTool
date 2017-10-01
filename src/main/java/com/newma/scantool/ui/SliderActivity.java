package com.newma.scantool.ui;

import android.graphics.Bitmap;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.newma.scantool.R;
import com.newma.scantool.logic.OrderNumRecognizer;
import com.newma.scantool.logic.QRCodeGenTask;

import java.util.ArrayList;
import java.util.List;

public class SliderActivity extends AppCompatActivity implements QRCodeGenTask.QRCodeGenCallback {

    List<String> m_codes;
    List<String> m_moneys;

    ViewPager m_page;
    CustomPageAdapter m_adapter;
    CustomDialog m_dlg;
    QRCodeGenTask m_task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);

        Bundle bundle = this.getIntent().getExtras();
        String params = bundle.getString(MainActivity.CODE_LIST);

        m_codes = new ArrayList<>();
        m_moneys = new ArrayList<>();
        OrderNumRecognizer.decodeRecognizeResult(params, m_codes, m_moneys);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("订单二维码");

        m_page = (ViewPager)findViewById(R.id.page_slider);
        m_adapter = new CustomPageAdapter(this);
        m_page.setAdapter(m_adapter);

        m_dlg = new CustomDialog(this, "生成二维码中...");
        m_dlg.show();

        m_task = new QRCodeGenTask();
        m_task.setCallback(this);
        m_task.execute(m_codes.toArray(new String[0]));
    }

    @Override
    public void onQRResults(ArrayList<Bitmap> bitmaps) {
        m_dlg.hide();
        m_adapter.setCodesAndBitmap(m_codes.toArray(new String[0]), m_moneys.toArray(new String[0]), bitmaps);
        m_adapter.notifyDataSetChanged();
        m_page.invalidate();
    }

    @Override
    public void onQRProgressCallback(int processed, int total) {
        m_dlg.setMessage(processed + " / " + total);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

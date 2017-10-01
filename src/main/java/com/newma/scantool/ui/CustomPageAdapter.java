package com.newma.scantool.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.newma.scantool.R;

import java.util.ArrayList;

/**
 * Created by newma on 17-9-11.
 */

public class CustomPageAdapter extends PagerAdapter {

    Context m_context;
    String[] m_codes = null;
    String[] m_moneys = null;
    ArrayList<Bitmap> m_bitmaps = null;

    public  CustomPageAdapter(Context context) {
        m_context = context;
    }

    public void setCodesAndBitmap(String[] codes, String[] moneys, ArrayList<Bitmap>bitmaps) {
        m_codes = codes;
        m_bitmaps = bitmaps;
        m_moneys = moneys;
    }

    @Override
    public int getCount() {
        return m_codes != null ? m_codes.length : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (View)object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater flater = (LayoutInflater)m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = flater.inflate(R.layout.view_qrcode_slider, container, false);
        TextView title = (TextView) view.findViewById(R.id.txt_code);
        title.setText(m_codes[position]);
        TextView count = (TextView) view.findViewById(R.id.txt_pageCout);
        count.setText((position + 1) + "/" + m_codes.length);
        ImageView img = (ImageView) view.findViewById(R.id.img_code);
        img.setImageBitmap(m_bitmaps.get(position));
        TextView money = (TextView) view.findViewById(R.id.txt_money);
        money.setText(m_moneys[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }
}

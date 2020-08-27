package com.geekbrains.myweatherv3;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

/* Кастомный LayoutManager для возможности отключения скрола*/
public class CustomGridLayoutManager extends LinearLayoutManager {
    private boolean isScrollEnabled = true;

    public CustomGridLayoutManager(Context context) {
        super(context);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        return isScrollEnabled && super.canScrollVertically();
    }
}
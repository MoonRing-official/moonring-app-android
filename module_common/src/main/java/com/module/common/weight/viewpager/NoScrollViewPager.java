package com.module.common.weight.viewpager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;


public final class NoScrollViewPager extends ViewPager {

    public NoScrollViewPager(Context context) {
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        return false;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        return false;
    }

    @Override
    public boolean executeKeyEvent(@NonNull KeyEvent event) {

        return false;
    }

    @Override
    public void setCurrentItem(int item) {

        super.setCurrentItem(item, Math.abs(getCurrentItem() - item) == 1);
    }
}
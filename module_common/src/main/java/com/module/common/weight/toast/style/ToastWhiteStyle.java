package com.module.common.weight.toast.style;

import android.content.Context;

import com.module.common.weight.toast.style.ToastBlackStyle;


public class ToastWhiteStyle extends ToastBlackStyle {

    public ToastWhiteStyle(Context context) {
        super(context);
    }

    @Override
    public int getBackgroundColor() {
        return 0XFFEAEAEA;
    }

    @Override
    public int getTextColor() {
        return 0XBB000000;
    }
}
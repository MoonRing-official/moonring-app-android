package com.module.common.weight.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;


public final class SmartTextView extends AppCompatTextView {

    public SmartTextView(Context context) {
        this(context, null);
    }

    public SmartTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public SmartTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);

        if (TextUtils.isEmpty(text) && getVisibility() != GONE) {
            setVisibility(GONE);
            return;
        }

        if (getVisibility() != VISIBLE) {
            setVisibility(VISIBLE);
        }
    }
}
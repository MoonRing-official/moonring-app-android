package com.module.common.weight.toast;

import android.app.Application;
import android.view.View;



public final class CustomToast extends NormalToast {

    /** 吐司弹窗显示辅助类 */
    private final ToastHelper mToastHelper;

    /** Toast 的视图 */
    private View mView;
    /** Toast 的重心 */
    private int mGravity;
    /** 水平偏移 */
    private int mXOffset;
    /** 垂直偏移 */
    private int mYOffset;
    /** 水平间距百分比 */
    private float mHorizontalMargin;
    /** 垂直间距百分比 */
    private float mVerticalMargin;

    public CustomToast(Application application) {
        super(application);
        mToastHelper = new ToastHelper(this, application);
    }

    @Override
    public void show() {
        // 替换成 WindowManager 来显示
        mToastHelper.show();
    }

    @Override
    public void cancel() {
        // 取消 WindowManager 的显示
        mToastHelper.cancel();
    }

    @Override
    public void setView(View view) {
        mView = view;
        setMessageView(findMessageView(view));
    }

    @Override
    public View getView() {
        return mView;
    }

    @Override
    public void setGravity(int gravity, int xOffset, int yOffset) {
        mGravity = gravity;
        mXOffset = xOffset;
        mYOffset = yOffset;
    }

    @Override
    public int getGravity() {
        return mGravity;
    }

    @Override
    public int getXOffset() {
        return mXOffset;
    }

    @Override
    public int getYOffset() {
        return mYOffset;
    }

    @Override
    public void setMargin(float horizontalMargin, float verticalMargin) {
        mHorizontalMargin = horizontalMargin;
        mVerticalMargin = verticalMargin;
    }

    @Override
    public float getHorizontalMargin() {
        return mHorizontalMargin;
    }

    @Override
    public float getVerticalMargin() {
        return mVerticalMargin;
    }
}
package com.module.common.weight.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.module.common.R;
import com.module.common.app.AppKt;

import com.module.common.support.log.LogInstance;
import com.module.common.util.time.RxTimer;


import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;


public final class StatusLayout extends FrameLayout {

    /**
     * 主布局
     */
    private ViewGroup mMainLayout;
    /**
     * 提示图标
     */
    private LottieAnimationView mLottieView;
    /**
     * 提示文本
     */
    private TextView mTextView;


    private TextView mTextVersion;

    private ConstraintLayout mClContainer;
    private LottieAnimationView mIvEmpty;


    private RelativeLayout mRlStatusBg;
    public ConstraintLayout mRlStatusMain;
    private ProgressBar    mProgressBar;
    private ImageView    mIvLeft;
    private ImageView    mIvRight;
    private ImageView      mLoadingTop;
    private RelativeLayout mRlProgress;
    private TextView mProgressBarTv;



    public StatusLayout(@NonNull Context context) {
        this(context, null);
    }

    public StatusLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatusLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public StatusLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        setClickable(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    /**
     * 显示
     */
    public void show() {

        if (mMainLayout == null) {
            
            initLayout();
        }

        if (isShow()) {
            return;
        }
        
        mMainLayout.setVisibility(VISIBLE);
    }

    /**
     * 隐藏
     */
    public void hide() {
        if (mMainLayout == null || !isShow()) {
            return;
        }
        
        mMainLayout.setVisibility(INVISIBLE);
    }

    /**
     * 是否显示了
     */
    public boolean isShow() {
        return mMainLayout != null && mMainLayout.getVisibility() == VISIBLE;
    }

    /**
     * 设置提示图标，请在show方法之后调用
     */
    public void setIcon(@DrawableRes int id) {
        setIcon(ContextCompat.getDrawable(getContext(), id));
    }

    public void setIcon(Drawable drawable) {
        if (mLottieView == null) {
            return;
        }
        if (mLottieView.isAnimating()) {
            mLottieView.cancelAnimation();
        }
        mLottieView.setImageDrawable(drawable);
    }

    /**
     * 设置提示动画
     */
    public void setAnimResource(@RawRes int id) {
        if (mLottieView == null) {
            return;
        }

        mLottieView.setAnimation(id);
        if (!mLottieView.isAnimating()) {
            mLottieView.playAnimation();
        }
    }

    /**
     * 设置提示文本，请在show方法之后调用
     */
    public void setHint(@StringRes int id) {
        setHint(getResources().getString(id));
    }

    public void setHint(CharSequence text) {
        if (mTextView == null) {
            return;
        }
        if (text == null) {
            text = "";
        }
        mTextView.setText(text);
    }

    /**
     * 初始化提示的布局
     */
    private void initLayout() {


        mMainLayout = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.widget_status_layout, this, false);

        mLottieView = mMainLayout.findViewById(R.id.iv_status_icon);
        mTextView = mMainLayout.findViewById(R.id.iv_status_text);

        mRlStatusMain = mMainLayout.findViewById(R.id.rl_status_main);
        mRlStatusBg = mMainLayout.findViewById(R.id.rl_status_bg);
        mProgressBar = mMainLayout.findViewById(R.id.prog);


        mRlProgress = mMainLayout.findViewById(R.id.rl_prog);
        mProgressBarTv = mMainLayout.findViewById(R.id.tv_progress);
        mTextVersion = mMainLayout.findViewById(R.id.tv_main_conten);

        mTextVersion.setText("v"+AppUtils.getAppVersionName());




        mClContainer = mMainLayout.findViewById(R.id.cl_container);
        mIvEmpty = mMainLayout.findViewById(R.id.iv_empty);


        
        mClContainer.setVisibility(GONE);
        if (mMainLayout.getBackground() == null) {
            
            TypedArray typedArray = getContext().obtainStyledAttributes(new int[]{android.R.attr.windowBackground});
            mMainLayout.setBackground(typedArray.getDrawable(0));
            typedArray.recycle();
        }

        TextView mainTitle=mMainLayout.findViewById(R.id.tv_main_title);
        mainTitle.setText("");
        addView(mMainLayout);
    }
    AtomicInteger process = new AtomicInteger(0);
    RxTimer rxTimer = new RxTimer();


    public void showEmptyIcon(Drawable drawable) {
        if (mIvEmpty == null) {
            return;
        }
        if (mIvEmpty.isAnimating()) {
            mIvEmpty.cancelAnimation();
        }
        mIvEmpty.setImageDrawable(drawable);
    }

    /**
     * cocos空白状态样式
     */
    public void showCocosStyle() {
        mClContainer.setVisibility(VISIBLE);
        mLottieView.setVisibility(GONE);
    }
    private Timer timer;

    private View mainView;


    private boolean myLoading = false;
    private boolean cocosLoading = false;




    private void endLoding(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mProgressBar.setProgress(100, true);
        }
        mProgressBarTv.setText("100%");
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                if (mainView!=null){
                    LogInstance.INSTANCE.i("zero-----endLoding-----");
                    mainView.setVisibility(View.GONE);


                }
            }
        },1000);
    }

    /**
     * mLottieView 直接显示 暂时loading
     */
    public void showLoading() {
        mClContainer.setVisibility(GONE);
        mLottieView.setVisibility(VISIBLE);
    }


    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        if (isShow()) {
            if (l == null) {
                return;
            }
            mMainLayout.setOnClickListener(l);
        } else {
            super.setOnClickListener(l);
        }
    }

    public View getLayout() {
        return mMainLayout;
    }
}
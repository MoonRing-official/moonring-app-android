package com.module.common.ext

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.opengl.Visibility
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.module.common.R
import com.module.common.weight.viewpager.FragmentPagerAdapter
import com.module.common.weight.viewpager.ScaleTransitionPagerTitleView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.yanzhenjie.recyclerview.SwipeRecyclerView
import com.module.common.app.appContext
import com.module.common.enums.MusicType

import com.module.common.util.SettingUtil
import com.module.common.weight.loadCallBack.EmptyCallback
import com.module.common.weight.loadCallBack.ErrorCallback
import com.module.common.weight.loadCallBack.LoadingCallback
import com.moonring.jetpackmvvm.ext.util.toHtml
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator



fun LoadService<*>.setErrorText(message: String) {
    if (message.isNotEmpty()) {
        this.setCallBack(ErrorCallback::class.java) { _, view ->
            view.findViewById<TextView>(R.id.error_text).text = message
        }
    }
}


fun LoadService<*>.showError(message: String = "") {
    this.setErrorText(message)
    this.showCallback(ErrorCallback::class.java)
}


fun LoadService<*>.showEmpty() {
    this.showCallback(EmptyCallback::class.java)
}

fun Toolbar.initHomeModlueClose(
    binding: ViewDataBinding,
    titleStr: String = "",
    backImg: Int = R.drawable.ic_back,
    onBack: (toolbar: Toolbar) -> Unit,
): Toolbar {
    setBackgroundColor(SettingUtil.getColor(appContext))
    binding.root.findViewById<TextView>(R.id.toolbar_title).apply {
        text = titleStr.toHtml()
        setTextColor(Color.WHITE)
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
    }

    binding.root.findViewById<ImageView>(R.id.toolbar_back).clickNoRepeat(musicType = MusicType.functionclick) { onBack.invoke(this) }
    return this
}

fun LoadService<*>.showLoading() {
    this.showCallback(LoadingCallback::class.java)
}

fun loadServiceInit(view: View, callback: () -> Unit): LoadService<Any> {
    val loadsir = LoadSir.getDefault().register(view) {

        callback.invoke()
    }
    loadsir.showSuccess()
    SettingUtil.setLoadingColor(SettingUtil.getColor(appContext), loadsir)
    SettingUtil.setLoading(loadsir)
    return loadsir
}


fun loadServiceInit(view: View,loadSir: LoadSir, callback: () -> Unit): LoadService<Any> {
    val loadsir = loadSir.register(view) {

        callback.invoke()
    }
    return loadsir
}


fun RecyclerView.init(
    layoutManger: RecyclerView.LayoutManager,
    bindAdapter: RecyclerView.Adapter<*>,
    isScroll: Boolean = true
): RecyclerView {
    layoutManager = layoutManger
    setHasFixedSize(true)
    adapter = bindAdapter
    isNestedScrollingEnabled = isScroll
    return this
}


fun SwipeRecyclerView.init(
    layoutManger: RecyclerView.LayoutManager,
    bindAdapter: RecyclerView.Adapter<*>,
    isScroll: Boolean = true
): SwipeRecyclerView {
    layoutManager = layoutManger
    setHasFixedSize(true)
    adapter = bindAdapter
    isNestedScrollingEnabled = isScroll
    return this
}



fun SwipeRefreshLayout.init(onRefreshListener: () -> Unit) {
    this.run {
        setOnRefreshListener {
            onRefreshListener.invoke()
        }

        setColorSchemeColors(SettingUtil.getColor(appContext))
    }
}


fun Toolbar.init(titleStr: String = ""): Toolbar {
    setBackgroundColor(SettingUtil.getColor(appContext))
    title = titleStr
    return this
}






fun BaseQuickAdapter<*, *>.setAdapterAnimation(mode: Int) {

    if (mode == 0) {
        this.animationEnable = false
    } else {
        this.animationEnable = true
        this.setAnimationWithDefault(BaseQuickAdapter.AnimationType.values()[mode - 1])
    }
}

fun MagicIndicator.bindViewPager2(
    viewPager: ViewPager2,
    mStringList: List<String> = arrayListOf(),
    action: (index: Int) -> Unit = {}) {
    val commonNavigator = CommonNavigator(appContext)
    commonNavigator.adapter = object : CommonNavigatorAdapter() {

        override fun getCount(): Int {
            return  mStringList.size
        }
        override fun getTitleView(context: Context, index: Int): IPagerTitleView {
            return ScaleTransitionPagerTitleView(appContext).apply {

                text = mStringList[index].toHtml()

                textSize = 17f

                normalColor = Color.WHITE

                selectedColor = Color.WHITE

                setOnClickListener {
                    viewPager.currentItem = index
                    action.invoke(index)
                }
            }
        }
        override fun getIndicator(context: Context): IPagerIndicator {
            return LinePagerIndicator(context).apply {
                mode = LinePagerIndicator.MODE_EXACTLY

                lineHeight = UIUtil.dip2px(appContext, 3.0).toFloat()
                lineWidth = UIUtil.dip2px(appContext, 30.0).toFloat()

                roundRadius = UIUtil.dip2px(appContext, 6.0).toFloat()
                startInterpolator = AccelerateInterpolator()
                endInterpolator = DecelerateInterpolator(2.0f)

                setColors(Color.WHITE)
            }
        }
    }
    this.navigator = commonNavigator

    viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            this@bindViewPager2.onPageSelected(position)
            action.invoke(position)
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            this@bindViewPager2.onPageScrolled(position, positionOffset, positionOffsetPixels)
        }

        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            this@bindViewPager2.onPageScrollStateChanged(state)
        }
    })
}

fun ViewPager2.init(
    fragment: Fragment,
    fragments: ArrayList<Fragment>,
    isUserInputEnabled: Boolean = true
): ViewPager2 {

    this.isUserInputEnabled = isUserInputEnabled

    adapter = object : FragmentStateAdapter(fragment) {
        override fun createFragment(position: Int) = fragments[position]
        override fun getItemCount() = fragments.size
    }
    return this
}

fun BottomNavigationViewEx.init(navigationItemSelectedAction: (Int) -> Unit): BottomNavigationViewEx {
    enableAnimation(true)
    enableShiftingMode(false)
    enableItemShiftingMode(true)
    itemIconTintList = SettingUtil.getColorStateList(SettingUtil.getColor(appContext))
    itemTextColor = SettingUtil.getColorStateList(appContext)
    setTextSize(12F)
    setOnNavigationItemSelectedListener {
        navigationItemSelectedAction.invoke(it.itemId)
        true
    }
    return this
}



fun BottomNavigationViewEx.interceptLongClick(vararg ids:Int) {
    val bottomNavigationMenuView: ViewGroup = (this.getChildAt(0) as ViewGroup)
    for (index in ids.indices){
        bottomNavigationMenuView.getChildAt(index).findViewById<View>(ids[index]).setOnLongClickListener {
            true
        }
    }
}


fun hideSoftKeyboard(activity: Activity?) {
    activity?.let { act ->
        val view = act.currentFocus
        view?.let {
            val inputMethodManager =
                act.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }
}

package com.moonring.ring.base

import androidx.databinding.ViewDataBinding
import androidx.viewpager.widget.ViewPager
import com.flyco.tablayout.SegmentTabLayout
import com.flyco.tablayout.listener.OnTabSelectListener
import com.module.common.base.BaseBarFragment
import com.module.common.base.BaseFragment
import com.moonring.jetpackmvvm.base.viewmodel.BaseViewModel

/**
 *    author : Administrator

 *    time   : 2024/10/10/010
 *    desc   :
 */

abstract class BaseChartTabFragment <VM : BaseViewModel, DB : ViewDataBinding> : BaseBarFragment<VM, DB>() {
    private val mTitles_3 = arrayOf("Day", "Week", "Month")
    protected fun setTabLayout(tabLayout: SegmentTabLayout, viewPager: ViewPager) {


       tabLayout.setTabData(mTitles_3)
        tabLayout.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                viewPager.currentItem = position
            }

            override fun onTabReselect(position: Int) {
                // Optionally handle reselection
            }
        })

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {

            }

            override fun onPageSelected(position: Int) {
                tabLayout.currentTab = position
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })
         viewPager.currentItem = 0
    }
}
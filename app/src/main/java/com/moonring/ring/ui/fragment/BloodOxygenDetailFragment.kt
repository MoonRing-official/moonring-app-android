package com.moonring.ring.ui.fragment

import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.module.common.base.BaseBarFragment
import com.module.common.weight.viewpager.FragmentPagerAdapter
import com.moonring.ring.R
import com.moonring.ring.base.BaseChartTabFragment
import com.moonring.ring.bean.room.HeartRateData
import com.moonring.ring.bean.room.OxygenData
import com.moonring.ring.databinding.FragmentBloodOxygenDetailBinding
import com.moonring.ring.enums.DateType
import com.moonring.ring.support.room.DatabaseUtils
import com.moonring.ring.ui.dialogfragment.DialogCheckHeartrateBottomSheet
import com.moonring.ring.ui.dialogfragment.DialogCheckOxgenBottomSheet
import com.moonring.ring.utils.currentTimeInSec
import com.moonring.ring.viewmodule.state.BloodOxygenDetailViewModel
import com.moonring.ring.viewmodule.state.SharedChartDataModel

/**
 *    author : Administrator

 *    time   : 2024/10/12/012
 *    desc   :
 */
class BloodOxygenDetailFragment : BaseChartTabFragment<BloodOxygenDetailViewModel, FragmentBloodOxygenDetailBinding>() {

    private val mClick by lazy {
        ProxyClick()
    }
    val  sharedChartDataModel: SharedChartDataModel by activityViewModels()
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.click = mClick
        mDatabind.viewModel = mViewModel
        mDatabind.shareViewModel = sharedChartDataModel
        sharedChartDataModel.unit.set("%")

        initViews()


    }

    private fun initViews() {

        setupViewPager()

        mDatabind.tvDay.setOnClickListener {
            moveSlideToView(mDatabind.slide,  mDatabind.tvDay)
            updateTextColor(mDatabind.tvDay,  mDatabind.tvWeek, mDatabind.tvMonth)
            mDatabind.viewPager.currentItem = 0

            sharedChartDataModel.flag = DateType.Day.name
            dayFragment.setsharedData()
            dayFragment.defaultTip()
        }
        mDatabind.tvWeek.setOnClickListener {
            moveSlideToView( mDatabind.slide,  mDatabind.tvWeek)
            updateTextColor(mDatabind.tvWeek,  mDatabind.tvDay, mDatabind.tvMonth)
            mDatabind.viewPager.currentItem = 1
            sharedChartDataModel.flag = DateType.Week.name

            weekFragment.setsharedData()
            weekFragment.defaultTip()
        }
        mDatabind.tvMonth.setOnClickListener {
            moveSlideToView( mDatabind.slide,  mDatabind.tvMonth)
            updateTextColor(mDatabind.tvMonth,  mDatabind.tvWeek, mDatabind.tvDay)
            mDatabind.viewPager.currentItem = 2
            sharedChartDataModel.flag = DateType.Month.name

            monthFragment.setsharedData()
            monthFragment.defaultTip()
        }

        moveSlideToView(mDatabind.slide,  mDatabind.tvDay)
        updateTextColor(mDatabind.tvDay,  mDatabind.tvWeek, mDatabind.tvMonth)
        mDatabind.viewPager.currentItem = 0
        sharedChartDataModel.flag = DateType.Day.name

        dayFragment.setsharedData()

    }


    private val dayFragment by lazy {
        BloodOxygenChartFragment.newInstance(DateType.Day.name)
    }

    private val weekFragment by lazy {
        BloodOxygenChartFragment.newInstance(DateType.Week.name)
    }

    private val monthFragment by lazy {
        BloodOxygenChartFragment.newInstance(DateType.Month.name)
    }
    private fun setupViewPager() {
        val titles = arrayOf("Day", "Week", "Month")

        val adapter = FragmentPagerAdapter<Fragment>(this)
        adapter.addFragment(dayFragment,DateType.Day.name)
        adapter.addFragment(weekFragment,DateType.Week.name)
        adapter.addFragment(monthFragment,DateType.Month.name)

        mDatabind.viewPager.adapter = adapter


    }


    override fun toolbarBackVisity(): Boolean {
        return true
    }

    override fun createObserver() {



    }

    fun moveSlideToView(slide: View, targetView: View) {
        val targetX = targetView.left.toFloat()


        val animator = ObjectAnimator.ofFloat(slide, "x", targetX)
        animator.duration = 300
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()

    }


    fun updateTextColor(selectedView: TextView, vararg otherViews: TextView) {

        selectedView.setTextColor(Color.BLACK)


        otherViews.forEach { it.setTextColor(Color.WHITE) }
    }



    override fun getToolbarTitle(): String {
        return  ""
    }

    inner class ProxyClick {
        fun refreshData() {

            mViewModel.loadBloodOxygenData()
        }
        fun measureNow(){
            val dialogFragment = DialogCheckOxgenBottomSheet{ heartRateStats->


                dayFragment.updateLasyOxgen(heartRateStats)

            }
            dialogFragment.show(childFragmentManager, "oxgen")
        }
    }
}

package com.moonring.ring.ui.fragment

import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.module.common.base.BaseBarFragment
import com.module.common.bean.MovementGoalBean
import com.module.common.network.apiService
import com.module.common.weight.viewpager.FragmentPagerAdapter
import com.moonring.ring.R
import com.moonring.ring.base.BaseChartTabFragment
import com.moonring.ring.databinding.FragmentStepDetailBinding
import com.moonring.ring.enums.DateType
import com.moonring.ring.homeAppViewModel
import com.moonring.ring.ui.dialogfragment.DailyGoalBottomSheet
import com.moonring.ring.ui.dialogfragment.GenderSelectionBottomSheet
import com.moonring.ring.viewmodule.state.GoalApiModel
import com.moonring.ring.viewmodule.state.SharedChartDataModel
import com.moonring.ring.viewmodule.state.StepDetailViewModel
import kotlinx.coroutines.launch

/**
 *    author : Administrator

 *    time   : 2024/10/12/012
 *    desc   :
 */
class StepDetailFragment : BaseChartTabFragment<StepDetailViewModel, FragmentStepDetailBinding>() {

    private val mClick by lazy {
        ProxyClick()
    }
    val  sharedChartDataModel: SharedChartDataModel by activityViewModels()
    val  goalApiModel: GoalApiModel by viewModels()
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.click = mClick
        mDatabind.viewModel = mViewModel
        mDatabind.shareViewModel = sharedChartDataModel
        sharedChartDataModel.unit.set("steps")

        initViews()

       val goal =  homeAppViewModel.movementGoalBean.value?.movement_goal?:0
        if (goal>0){
            sharedChartDataModel.goal.set("${goal}")
        }


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
        StepBarChartFragment.newInstance(DateType.Day.name)
    }

    private val weekFragment by lazy {
        StepBarChartFragment.newInstance(DateType.Week.name)
    }

    private val monthFragment by lazy {
        StepBarChartFragment.newInstance(DateType.Month.name)
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

            mViewModel.loadStepData()
        }

        fun setGo(){
            val dialogFragment = DailyGoalBottomSheet( ){numberString ->

                val numberWithoutComma = numberString.replace(",", "")
                val numberAsInt = numberWithoutComma.toInt()
                sharedChartDataModel.goal.set("${numberAsInt}")
                goalApiModel.postMovementGoal(MovementGoalBean(numberAsInt.toLong()))

            }
            dialogFragment.show(childFragmentManager, "DailyGoalBottomSheet")
        }
    }
}

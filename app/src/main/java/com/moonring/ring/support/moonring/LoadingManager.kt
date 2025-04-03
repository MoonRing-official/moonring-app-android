package com.moonring.ring.support.moonring

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.LinearInterpolator

class LoadingManager {


    private lateinit var animator: ObjectAnimator

    private var initialRotation: Float = 0f
    var roateView:View?=null
     fun configureRotationAnimator(view:View) {
        animator = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f).apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = LinearInterpolator()
        }
    }

    fun configureRotationVerseAnimator(view: View) {

        animator = ObjectAnimator.ofFloat(view, "rotation", 360f, 0f).apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = LinearInterpolator()
        }
    }

    fun startRotation() {

        if (!animator.isRunning) {
            animator.start()
        }
    }

    fun stopRotation() {
        if (animator.isRunning) {
            animator.cancel()

            animator.target?.let {
                (it as View).rotation = initialRotation
            }

            roateView?.rotation = 0f
        }

    }

}
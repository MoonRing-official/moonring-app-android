package com.moonring.ring.weight.loadCallBack

import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import com.kingja.loadsir.callback.Callback
import com.moonring.ring.R


class LoadingMoonringCallback : Callback() {
    private lateinit var animator: ObjectAnimator
    override fun onCreateView(): Int {
        return R.layout.layout_moonring_loading
    }

    override fun onReloadEvent(context: Context?, view: View?): Boolean {
        return true
    }
    override fun onViewCreate(context: Context?, view: View?) {
        var img = view?.findViewById<ImageView>(R.id.loadingImage)
        img?.let{
            configureRotationAnimator(it)
            startRotation()
        }
    }

    private fun configureRotationAnimator(img:ImageView) {
        animator = ObjectAnimator.ofFloat(img, "rotation", 0f, 360f).apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = LinearInterpolator()
        }
    }


    private fun startRotation() {
        animator.start()
    }

    private fun stopRotation() {
        animator.cancel()
    }
}
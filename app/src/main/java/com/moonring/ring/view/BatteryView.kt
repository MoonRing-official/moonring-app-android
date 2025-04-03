package com.moonring.ring.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.hjq.shape.layout.ShapeConstraintLayout
import com.hjq.shape.layout.ShapeRelativeLayout
import com.hjq.shape.view.ShapeView
import com.moonring.ring.R
import com.moonring.ring.databinding.ViewBatteryBinding

class BatteryView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: ViewBatteryBinding

    init {

        val inflater = LayoutInflater.from(context)
        binding = ViewBatteryBinding.inflate(inflater, this, true)


        context.theme.obtainStyledAttributes(attrs, R.styleable.BatteryView, 0, 0).apply {
            try {

                val slRecWidth = getDimension(R.styleable.BatteryView_slRecWidth, 40f)
                val slRecHeight = getDimension(R.styleable.BatteryView_slRecHeight, 25f)


                binding.slRec.layoutParams = binding.slRec.layoutParams.apply {
                    width = slRecWidth.toInt()
                    height = slRecHeight.toInt()
                }


                val scaleFactor = getFloat(R.styleable.BatteryView_scaleFactor, 1.0f)
                this@BatteryView.scaleX = scaleFactor
                this@BatteryView.scaleY = scaleFactor
            } finally {
                recycle()
            }
        }
    }


    fun setProgress(progress: Int) {
        println("progress:${progress}")
        binding.progressBar.progress = progress.coerceIn(0, 100)
    }


    fun setStatus(isCharge:Boolean){
        if (isCharge){
            binding.ivCharge.visibility = VISIBLE
            binding.progressBar.visibility = INVISIBLE
        }else{
            binding.ivCharge.visibility = INVISIBLE
            binding.progressBar.visibility = VISIBLE
        }
    }
}
package com.moonring.ring.view

/**
 *    author : Administrator

 *    time   : 2024/3/12/012
 *    desc   :
 */


import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.moonring.ring.R
import com.moonring.ring.databinding.SettingItemViewBinding


class SettingItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var binding: SettingItemViewBinding

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = SettingItemViewBinding.inflate(inflater, this, false)

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.SettingItemView, 0, 0)
            val iconDrawable = typedArray.getDrawable(R.styleable.SettingItemView_settingIcon)
            val titleText = typedArray.getString(R.styleable.SettingItemView_settingTitle)

            binding.icon.setImageDrawable(iconDrawable)
            binding.title.text = titleText

            typedArray.recycle()
        }
        addView(binding.root)
    }
}

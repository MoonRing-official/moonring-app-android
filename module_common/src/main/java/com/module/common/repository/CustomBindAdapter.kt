package com.module.common.repository

import android.os.SystemClock
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout.INVISIBLE
import androidx.constraintlayout.widget.ConstraintLayout.VISIBLE
import androidx.databinding.BindingAdapter

import com.module.common.enums.MusicType

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.kyleduo.switchbutton.SwitchButton
import com.moonring.jetpackmvvm.ext.view.textString


object CustomBindAdapter {

    @JvmStatic
    @BindingAdapter(value = ["kswChecked"])
    fun setSwitchChecked(view: SwitchButton, checked: Boolean) {
        if (view.isChecked != checked) {
            view.isChecked = checked
        }
    }
    @BindingAdapter(value = ["checkChange"])
    @JvmStatic
    fun checkChange(checkbox: CheckBox, listener: CompoundButton.OnCheckedChangeListener) {
        checkbox.setOnCheckedChangeListener(listener)
    }

    @BindingAdapter(value = ["showPwd"])
    @JvmStatic
    fun showPwd(view: EditText, boolean: Boolean) {
        if (boolean) {
            view.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            view.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        view.setSelection(view.textString().length)
    }

    @BindingAdapter(value = ["imageUrl"])
    @JvmStatic
    fun imageUrl(view: ImageView, url: String) {
        Glide.with(view.context.applicationContext)
            .load(url)
            .transition(DrawableTransitionOptions.withCrossFade(500))
            .into(view)
    }

    @BindingAdapter(value = ["circleImageUrl"])
    @JvmStatic
    fun circleImageUrl(view: ImageView, url: String) {
        Glide.with(view.context.applicationContext)
            .load(url)
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .transition(DrawableTransitionOptions.withCrossFade(500))
            .into(view)
    }



    @BindingAdapter(value = ["afterTextChanged"])
    @JvmStatic
    fun EditText.afterTextChanged(action: (String) -> Unit) {
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                action(s.toString())
            }
        })
    }



    @BindingAdapter(value = ["noRepeatClick","musicType"], requireAll=false)
    @JvmStatic
    fun setOnClick(view: View,

                   clickListener: View.OnClickListener,
                   type:MusicType?=null,
    ) {
        val mHits = LongArray(2)
        view.setOnClickListener { v ->
            System.arraycopy(mHits, 1, mHits, 0, mHits.size - 1)
            mHits[mHits.size - 1] = SystemClock.uptimeMillis()
            if (mHits[0] < SystemClock.uptimeMillis() - 500) {
                clickListener.onClick(v)


            }
        }
    }













}
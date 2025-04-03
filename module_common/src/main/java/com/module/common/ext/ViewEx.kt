package com.module.common.ext

import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ImageSpan
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.module.common.enums.MusicType
import com.module.common.weight.toast.ToastSingleton.view
import com.module.common.weight.view.CenterImageSpan
import com.moonring.jetpackmvvm.support.click.RecordClickUtils


/**
 *    author : Administrator

 *    time   : 2022/9/7/007
 *    desc   :
 */


fun TextView.afterImageSpan(
    content: String,
    @DrawableRes id: Int,
    width: Int,
    height: Int,imageClick:()->Unit = {}
) {
    if(content.isNullOrBlank()){
        return
    }
    val space = " "
    val text = "${content}${space}"
    val ssb = SpannableStringBuilder(text)
    val drawable = ContextCompat.getDrawable(context, id) ?: return


    drawable.setBounds(0, 0, width, height)
    val imageSpan = CenterImageSpan(drawable)
    val cs = object : ClickableSpan() {
        override fun onClick(widget: View) {


            imageClick.invoke()
        }

    }

    ssb.setSpan(imageSpan, ssb.length - 1, ssb.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

    ssb.setSpan(cs, ssb.length - 1, ssb.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);


    setText(ssb, TextView.BufferType.SPANNABLE)

    highlightColor = resources.getColor(android.R.color.transparent)
    movementMethod = LinkMovementMethod.getInstance();
}

fun View.ableClick(){
    isEnabled = false
    setBackgroundColor(Color.parseColor("#86909c"))
}

fun View.unableClick(){
    isEnabled = true
    setBackgroundColor(Color.parseColor("#f53f3f"))
}


var lastClickTime = 0L
fun View.clickNoRepeat(interval: Long = 500,
                      musicType: MusicType = MusicType.functionclick,
                       action: (view: View) -> Unit) {
    setOnClickListener {
        val currentTime = System.currentTimeMillis()
        if (lastClickTime != 0L && (currentTime - lastClickTime < interval)) {
            return@setOnClickListener
        }
        lastClickTime = currentTime
        action(it)
        if (musicType != MusicType.none){

        }

        RecordClickUtils.notifyClick()
    }
}

fun RecyclerView.disableAnimations(){
    val animator = itemAnimator as? SimpleItemAnimator
    if (animator != null) {
        animator.supportsChangeAnimations = false
    }
}

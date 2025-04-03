package com.module.common.weight.loadCallBack


import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.hjq.shape.view.ShapeButton
import com.module.common.R
import com.kingja.loadsir.callback.Callback
import com.module.common.ext.clickNoRepeat


class EmptyCallback (val hashMap:Map<String,Any> = emptyMap<String,Any>()): Callback() {

    private var emptyeCallback:IEmptyCallback?=null
    var btnSure:ShapeButton?=null
    override fun onCreateView(): Int {
        return R.layout.layout_empty
    }

    override fun onViewCreate(context: Context?, view: View?) {
        super.onViewCreate(context, view)
        var tvTitle = view?.findViewById<TextView>(R.id.tv_title)
        var tvDesc = view?.findViewById<TextView>(R.id.tv_desc)
        var img = view?.findViewById<ImageView>(R.id.iv_img)
        btnSure = view?.findViewById<ShapeButton>(R.id.btn_sure)
        val title = hashMap.getOrElse("title"){
            ""
        }
        tvTitle?.text = title as? String ?:""
        val desc = hashMap.getOrElse("desc"){
            ""
        }
        tvDesc?.text = desc as? String ?:""
        val imgUrl = hashMap.getOrElse("img"){
            0
        }
        img?.setImageResource(imgUrl as? Int?:0)
        val buttonText = hashMap.getOrElse("button"){
            ""
        }
        val strBtn = buttonText as? String ?:""
        if (strBtn.isNullOrEmpty()){
            btnSure?.visibility =  View.INVISIBLE
        }else{
            btnSure?.text =strBtn
        }


    }


    fun setIEmptyCallback(callback1:IEmptyCallback?){
        emptyeCallback = callback1
    }


    interface IEmptyCallback{
        fun sureCallBack()
    }
}
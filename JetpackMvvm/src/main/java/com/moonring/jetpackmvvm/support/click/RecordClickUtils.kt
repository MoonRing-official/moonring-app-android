package com.moonring.jetpackmvvm.support.click

/**
 *    author : Administrator

 *    time   : 2022/9/14/014
 *    desc   :
 */

object RecordClickUtils {
    var mIRecordClick: IRecordClick?=null

    fun addIRecordClick(recordClick: IRecordClick){
        mIRecordClick = recordClick
    }

    fun notifyClick(){
        mIRecordClick?.notifyClick()
    }
}
package com.moonring.jetpackmvvm.support.logout

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Process.killProcess
import android.os.Process.myPid
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ActivityUtils
import com.module.common.util.CacheUtil

import com.moonring.jetpackmvvm.ext.nav
import com.moonring.jetpackmvvm.ext.navigateAction

/**
 *    author : Administrator

 *    time   : 2022/8/11/011
 *    desc   :
 */





fun restartApp(mContext: Context){

    clearAllInfo()

    mContext.packageManager.getLaunchIntentForPackage(mContext.packageName)?.apply {

        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

        mContext.startActivity(this)
    }


    System.exit(0)

}




fun clearAllInfo(){


    CacheUtil.clearAll()


}


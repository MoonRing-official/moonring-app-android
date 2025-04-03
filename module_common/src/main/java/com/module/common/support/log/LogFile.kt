package com.module.common.support.log

import android.os.Build
import android.os.Environment
import android.util.Log
import com.module.common.BuildConfig
import com.module.common.enums.PackagingConfig
import com.module.common.support.config.AppConfig
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 *    author : Administrator

 *    time   : 2022/8/11/011
 *    desc   :打印日志 同一管理
 */


object LogInstance {
    private val NEW_LINE = System.getProperty("line.separator")
    private const val DEFAULT_TAG = "YLVS"
    //是否打印Logcat
    var mLogcatAppender = BuildConfig.DEBUG
    //是否打印日志到本地
    var mLogcatToLocal = true

    var hasPermisson = false
    var mLogFile:File?=null
    init {

    }
    fun i(message: String?) {
        i(DEFAULT_TAG,message)
    }
    fun i(TAG: String = DEFAULT_TAG, message: String?) {
        appendLog("$TAG : $message")
        if (mLogcatAppender) {
            Log.i(TAG, message?:"")
        }
    }

    fun d(TAG: String = DEFAULT_TAG, message: String?) {
        appendLog("$TAG : $message")
        if (mLogcatAppender) {
            Log.d(TAG, message?:"")
        }
    }
    fun d(message: String?) {
        d(DEFAULT_TAG,message)
    }

    fun e(TAG: String = DEFAULT_TAG, message: String?) {
        appendLog("$TAG : $message")
        if (mLogcatAppender) {
            Log.e(TAG, message?:"")
        }
    }
    fun e(message: String?) {
        e(DEFAULT_TAG,message)
    }
    fun v(TAG: String = DEFAULT_TAG, message: String?) {
        appendLog("$TAG : $message")
        if (mLogcatAppender) {
            Log.v(TAG, message?:"")
        }
    }
    fun v(message: String?) {
        v(DEFAULT_TAG,message)
    }
    fun w(TAG: String = DEFAULT_TAG, message: String?) {
        appendLog("$TAG : $message")
        if (mLogcatAppender) {
            Log.w(TAG, message?:"")
        }
    }
    fun w(message: String?) {
        w(DEFAULT_TAG,message?:"")
    }
    @Synchronized
    private fun appendLog(text: String?) {
        if (!mLogcatToLocal){
            return
        }
        if (!hasPermisson){
            return
        }
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
        mLogFile?.let {
            try {
                val fileOut = FileWriter(mLogFile, true)
                fileOut.append(sdf.format(Date()) + " : " + text + NEW_LINE)
                fileOut.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    }

    private fun logDeviceInfo() {
        appendLog("Model : " + Build.MODEL)
        appendLog("Brand : " + Build.BRAND)
        appendLog("Product : " + Build.PRODUCT)
        appendLog("Device : " + Build.DEVICE)
        appendLog("Codename : " + Build.VERSION.CODENAME)
        appendLog("Release : " + Build.VERSION.RELEASE)
    }

    fun initLogfile(){

        if (mLogcatToLocal){


            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val sb = StringBuilder()
            sb.append(DEFAULT_TAG)
            sb.append("/")
            sb.append(sdf.format(Date()))
            val appDir = File(Environment.getExternalStorageDirectory(), sb.toString())
            if (!appDir.exists()) {
                appDir.mkdirs()
            }


            val fileName = "logs.txt"
            mLogFile = File(appDir, fileName)
            mLogFile?.apply {
                if (!exists()) {
                    try {
                       createNewFile()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                hasPermisson = true
                logDeviceInfo()
            }


        }


    }

}
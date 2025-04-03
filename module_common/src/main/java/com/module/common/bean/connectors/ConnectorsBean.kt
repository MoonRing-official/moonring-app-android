package com.module.common.bean.connectors

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *    author : Administrator

 *    time   : 2022/7/7/007
 *    desc   :
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class ConnectorsBean (var weight:Int, var addr:String = "") : Parcelable
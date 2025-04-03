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
data class ConnectorsResponse (var token: String = "",
                               var timeout: Int = 0,
                               var code: Int = -1,
                               var data:List<ConnectorsBean>
) : Parcelable
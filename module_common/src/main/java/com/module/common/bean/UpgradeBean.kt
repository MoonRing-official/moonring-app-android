package com.module.common.bean
import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@SuppressLint("ParcelCreator")
@Parcelize
data class UpgradeBean(var download_url: String = "",
                       var version: String = "",
                       var is_force_update: Boolean = false,
                       var update_log: String =  "",
                       var firmware_type: String =  "",
                   ) : Parcelable

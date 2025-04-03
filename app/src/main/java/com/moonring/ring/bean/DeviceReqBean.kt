package com.moonring.ring.bean

/**
 *    author : Administrator

 *    time   : 2024/3/14/014
 *    desc   :
 */
data class DeviceReqBean(
    val data: DeviceReqData
)

data class DeviceReqData(
    val uuid: String="",
    val mac: String,
    val client: String = "android"
)

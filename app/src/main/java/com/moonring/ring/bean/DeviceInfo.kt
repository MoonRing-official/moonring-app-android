package com.moonring.ring.bean


data class DeviceInfo(
    val version: Int = -1,
    val macAddress: String = "",
    val vendorCode: String = "",
    val productCode: String = "",
    val result: Int = 0
)

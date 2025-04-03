package com.moonring.ring.bean

data class ScanResult(
    val deviceName: String,
    val deviceMacAddress: String,
    val rssi: Int,
    val ver: String,
    val cid: String,
    val did: String,
    val bindFlag: String,
    val bindState: String
)
package com.moonring.ring.bean


data class SensorData(
    val result: Int = 0 ,
    val timestamp: Long = 0,
    val heartrate: Int = 0,
    val sleepStatus: Int = 0
) {

}

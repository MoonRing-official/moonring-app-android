package com.moonring.ring.bean

data class SensorStatus(
    val type: Int,  // 1: 心率, 2: 血压血氧
    val state: Int  // 1: 打开, 0: 关闭
) {

}

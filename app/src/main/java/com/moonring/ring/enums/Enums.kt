package com.moonring.ring.enums

import com.moonring.ring.utils.awakeSleepColor
import com.moonring.ring.utils.deepSleepColor
import com.moonring.ring.utils.lightSleepColor

/**
 *    author : Administrator

 *    time   : 2024/3/13/013
 *    desc   :
 */

enum class DateType{
    Week,Day,Month
}

enum class GetDataByDay(val value: Int){
    Step_Sleep(1),HeartBeat(2),
}
enum class  OnGetDataByDay(val value: Int){
    Step(1),Sleep(2),HeartBeat(3)
}

enum class ConnectionState(val value: Int, val description: String) {
    NotConnected(0, "未连接"), // Not connected
    Connecting(1, "连接中"), // Connecting
    Connected(2, "已连接") // Connected
}







enum class SleepLevelType(val yAxisIndex: Int, val yAxisIndexColor: Any,val titleTip:String) {
    Awake(2, deepSleepColor,"Awake Sleep"),  // Example color and index
    LightSleep(1, lightSleepColor,"Light Sleep"),
    DeepSleep(0, awakeSleepColor,"Deep Sleep");

    // Helper function to convert integer value to SleepLevelType
    companion object {
        fun fromValue(value: Int): SleepLevelType {
            return when (value) {
                99 -> DeepSleep
                40 -> LightSleep
                else -> Awake
            }
        }
    }
}


enum class HealthDataType(val displayName: String) {
    calorie("卡路里"),
    heart_rate("心率"),
    movement("步数"),
    blood_oxygen("血氧"),
    sleep("睡眠")
}
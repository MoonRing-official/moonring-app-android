package com.moonring.ring.bean.room


data class HealthData(
    val metric: String = "calorie",
    val value: Int = 0,
    val value2: Int = 0,
    val timestamp: Long = 0L
)

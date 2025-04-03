package com.moonring.ring.support.moonring

import com.module.common.bean.UserProfileMoonRing


fun getDefaultProfile(): UserProfileMoonRing {
   return UserProfileMoonRing()
}

/// 开关  0:关 1:开
var `switch` = 1

/// 开始时间 时
var start_hour:Int = 0

/// 开始时间 分
var start_min:Int = 0

/// 结束时间 时
var end_hour:Int = 23

/// 结束时间 分
var end_min:Int = 59

/// 重复间隔 分钟
var interval:Int = 15

/// 每次 测量 时间 分钟
var duration:Int = 2


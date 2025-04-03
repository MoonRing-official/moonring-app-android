package com.moonring.ring.utils

import android.text.TextUtils
import com.connect.common.model.Account
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.moonring.ring.bean.OxygenSensorData
import com.particle.base.model.UserInfo
import com.tencent.mmkv.MMKV

/**
 *    author : Administrator

 *    time   : 2024/10/12/012
 *    desc   :
 */
object StorageUtil {
    private val mmkv = MMKV.defaultMMKV()
    private val gson = Gson()


    fun storeOxygenList(key: String, data: List<OxygenSensorData>) {
        val json = gson.toJson(data)
        mmkv.encode(key, json)
    }


    fun getOxygenList(key: String): List<OxygenSensorData> {
        val json = mmkv.decodeString(key, "")
        return if (json.isNotEmpty()) {
            gson.fromJson(json, object : TypeToken<List<OxygenSensorData>>() {}.type)
        } else {
            emptyList()
        }
    }


    fun storeOxygen(key: String, data: OxygenSensorData) {
        val list = getOxygenList(key).toMutableList()
        list.add(data)
        storeOxygenList(key, list)
    }


    /**
     * 获取保存的账户信息
     */
    fun getUserAccount(): UserInfo? {
        val kv = MMKV.mmkvWithID("app")
        val userStr = kv.decodeString("user")
        return if (TextUtils.isEmpty(userStr)) {
            null
        } else {
            Gson().fromJson(userStr, UserInfo::class.java)
        }
    }

    /**
     * 设置账户信息
     */
    fun setUserAccount(userResponse: UserInfo?) {
        val kv = MMKV.mmkvWithID("app")
        if (userResponse == null) {
            kv.encode("user", "")
        } else {
            kv.encode("user", Gson().toJson(userResponse))

        }
    }
}

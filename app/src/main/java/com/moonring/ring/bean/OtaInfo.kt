package com.moonring.ring.bean

import org.json.JSONObject

/**
 *    author : Administrator

 *    time   : 2024/8/30/030
 *    desc   :
 */
data class OtaInfo(val isUpdate: Boolean, val version: String, val path: String){
    val versionSuffix: String
        get() {
            try {

                val jsonObject = JSONObject(version)
                val versionCode = jsonObject.getString("versionCode")

                val regex = "V(\\d+)$".toRegex()
                val matchResult = regex.find(versionCode)
                return matchResult?.groupValues?.get(1) ?: "--"
            }catch (e:Exception){
                e.printStackTrace()
            }
            return "--"

        }
}

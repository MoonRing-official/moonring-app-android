package com.module.common.util

import android.text.TextUtils
import com.google.gson.Gson
import com.module.common.bean.UserProfileMoonRing
import com.tencent.mmkv.MMKV

object CacheUtil {

    /**
     * 获取保存的账户信息
     */
    fun getUser(): UserProfileMoonRing? {
        val kv = MMKV.mmkvWithID("app")
        val userStr = kv.decodeString("user")
        return if (TextUtils.isEmpty(userStr)) {
            null
        } else {
            Gson().fromJson(userStr, UserProfileMoonRing::class.java)
        }
    }

    /**
     * 设置账户信息
     */
    fun setUser(userResponse: UserProfileMoonRing?) {
        val kv = MMKV.mmkvWithID("app")
        if (userResponse == null) {
            kv.encode("user", "")
        } else {
            kv.encode("user", Gson().toJson(userResponse))

        }
    }



    /**
     * 获取保存的账户信息
     */
    fun getToken(): String {
        val kv = MMKV.mmkvWithID("app")
        return  kv.decodeString("token")?:""
    }

    /**
     * 设置账户信息
     */
    fun setToken(token: String) {
        val kv = MMKV.mmkvWithID("app")
        kv.encode("token",token)
    }



    /**
     * 设置用户个人信息专用
     * @param key 键名
     * @param value 值
     */
    fun setUserProfile(value: String) {
//        val kv = MMKV.mmkvWithID("UserProfile")
//        kv.encode("profile", value)
    }


    /**
     * 获取用户个人信息专用
     * @param key 键名 使用mac 地址为
     * @return 返回对应键名的值，如果没有则返回空字符串
     */
    fun getLastSynTime(key: String): String {
        val kv = MMKV.mmkvWithID("lastSynTime")
        return kv.decodeString(key, "0")
    }

    /**
     * 设置用户个人信息专用
     * @param key 键名
     * @param value 值
     */
    fun setLastSynTime(key: String, value: String) {
        val kv = MMKV.mmkvWithID("lastSynTime")
        kv.encode(key, value)
    }


    /**
     * 保存退出登录标志
     */
    fun getCookie(): String {
        val kv = MMKV.mmkvWithID("app")
        return  kv.decodeString("Cookie","")
    }

    /**
     * 设置退出登录标志
     */
    fun setCookie(cookie: String) {
        val kv = MMKV.mmkvWithID("app")
        kv.encode("Cookie",cookie)
    }


    /**
     * 存储SP
     */
    fun setCommonJson(key:String, json: String) {
//        LogInstance.i("====setCommonJson=======${key}========${json}")
        val kv = MMKV.mmkvWithID("app")
        kv.encode(key,json)
    }


    /**
     * 获取SP
     */
    fun getCommonJson(key:String): String {
        val kv = MMKV.mmkvWithID("app")
        return  kv.decodeString(key,"")
    }



    /**
     * 清楚所有
     */
    fun clearAll(){
        val kv = MMKV.mmkvWithID("app")
        val appInt = MMKV.mmkvWithID("appInt")
        val rolesData = MMKV.mmkvWithID("RolesData")
        kv.clearAll()
        appInt.clearAll()
        rolesData.clearAll()
    }


    /**
     * 获取用户是否同意用户协议
     */
    fun getUserAgreement(): Boolean {
        val kv = MMKV.mmkvWithID("UserAgreement")
        return  kv.decodeBool("UserAgreement")?:false
    }

    /**
     *设置用户是否同意用户协议
     */
    fun setUserAgreement(boolean: Boolean) {
        val kv = MMKV.mmkvWithID("UserAgreement")
        kv.encode("UserAgreement",boolean)
    }






    /**
     * 保存Music
     */
    fun getMusic(): Boolean {
        val kv = MMKV.mmkvWithID("music")
        return  kv.decodeBool("music",true)
    }




    /**
     * 保存Meffect
     */
    fun getEffect(): Boolean {
        val kv = MMKV.mmkvWithID("effect")
        return  kv.decodeBool("effect",true)
    }


    /**
     * 保存拒绝升级信息
     */
    fun setUpgradleCancel(str: String){
        val kv = MMKV.mmkvWithID("upgradle")
        kv.encode("upcancel",str)
    }
    /**
     * 获取拒绝升级信息
     */
    fun getUpgradleCancel():String{
        val kv = MMKV.mmkvWithID("upgradle")
        return kv.decodeString("upcancel", "")
    }




    /**
     *获取设置的语言,默认英语
     */
    fun getSettingLanguage():String{
        val kv = MMKV.mmkvWithID("language")
        return  kv.decodeString("lang","en")
    }





}




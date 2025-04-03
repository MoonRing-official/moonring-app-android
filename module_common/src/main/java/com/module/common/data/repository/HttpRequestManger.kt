package com.module.common.data.repository

import com.module.common.bean.BoundRingResponse
import com.module.common.bean.DeleteBeanResponse
import com.module.common.bean.UpgradeBean
import com.module.common.bean.UpgradeRingResponse
import com.module.common.bean.connectors.ConnectorsResponse
import com.module.common.bean.login.LoginReqBean
import com.module.common.bean.login.LoginResponse
import com.module.common.bean.login.PasswordReqBean
import com.module.common.bean.login.PasswordResponse
import com.module.common.bean.login.RingInfo
import com.module.common.bean.login.SendCaptchaReqBean
import com.module.common.bean.login.UserResponse
import com.module.common.data.moudle.bean.ApiResponse
import com.module.common.network.apiService
import com.module.common.support.log.LogInstance
import com.module.common.util.GsonUtils
import com.moonring.jetpackmvvm.network.AppException
import com.moonring.jetpackmvvm.network.parseEmptyBody
import java.lang.Exception



val HttpRequestCoroutine: HttpRequestManger by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
    HttpRequestManger()
}

class HttpRequestManger {



    suspend fun password(bean: PasswordReqBean): PasswordResponse {
        val response = apiService.password(bean)

        if (response.code==0) {
            return response
        } else {

            throw Exception()
        }
    }







    suspend fun getVersion(): UpgradeBean {
        val connectorsData = apiService.getVersion()

        return connectorsData
    }
    suspend fun getRingVersion(): UpgradeRingResponse {
        val response = apiService.getRingVersion()
        if (response.code == 0) {
            return response
        } else {

            throw Exception()
        }

    }


    suspend fun boundRing(ringInfo: RingInfo): ApiResponse<*> {
        val response = apiService.boundRing(ringInfo)
        if (response!=null) {
            return response
        } else {

            throw Exception("Failed to fetch user info with code: ${response.code}")
        }
    }

    suspend fun getBoundRing(): BoundRingResponse {
        val response = apiService.getBoundRing()
        if (response!=null) {
            return response
        } else {

            throw Exception("Failed to fetch user info with code: ${response.code}")
        }
    }




    suspend fun accountDeletion(): DeleteBeanResponse {
        val response = apiService.accountDeletion()
        val errorLog= response.errorBody()?.string()
        val code = parseEmptyBody(response)

        LogInstance.i("accountDeletion==========${errorLog}")

        if (code == 0) {
            val response2 = DeleteBeanResponse(code)
            return response2
        } else {
            var email = GsonUtils.json2VO(errorLog, DeleteBeanResponse::class.java)

            throw AppException(email.code, email.msg,errorLog)


        }

    }




}
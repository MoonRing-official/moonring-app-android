package com.module.common.network

import com.module.common.bean.*

import com.module.common.bean.login.LoginMoonringReqBean
import com.module.common.bean.login.PasswordReqBean
import com.module.common.bean.login.PasswordResponse
import com.module.common.bean.login.RingInfo
import com.module.common.bean.login.SendCaptchaReqBean
import com.module.common.bean.login.UserResponse
import com.module.common.data.moudle.bean.ApiResponse
import com.module.common.support.config.AppConfig
import retrofit2.Response
import retrofit2.http.*

@JvmSuppressWildcards
interface ApiService {

    companion object {
        val SERVER_URL: String = AppConfig.getHostUrl()

    }


    @POST("")
    suspend fun login(@Body bean: LoginMoonringReqBean): ApiResponse<*>



    @GET("")
    suspend fun getUserInfo(): UserResponse



    @PUT("")
    suspend fun updateUserProfile(
        @Body personal: UserProfileMoonRing
    ): ApiResponse<*>





    @GET("")
    suspend fun getBoundRing(): BoundRingResponse



    @POST("")
    suspend fun boundRing(@Body bean: RingInfo): ApiResponse<*>





    @DELETE("")
    suspend fun deleteBoundRing(): Response<Unit>


    @GET("")
    suspend fun getVersion(): UpgradeBean



    @GET("")
    suspend fun getRingVersion(): UpgradeRingResponse





    @GET("")
    suspend fun getMovementGoal(): MovementGoalBean



    @POST("")
    suspend fun postMovementGoal(@Body bean: MovementGoalBean): ApiResponse<*>



    @DELETE("")
    suspend fun accountDeletion():  Response<Unit>



    @POST("")
    suspend fun postHealth(@Body params: Map<String, Any>): ApiResponse<*>







    @POST("")
    suspend fun password(
        @Body bean: PasswordReqBean
//        @Field("iv") iv: String,
//        @Field("email") email: String
    ): PasswordResponse

    @POST("")
    suspend fun sendCaptcha(@Body bean: SendCaptchaReqBean = SendCaptchaReqBean()): ApiResponse<*>


    @POST("")
    suspend fun sendCaptchaBind(@Body bean: SendCaptchaReqBean = SendCaptchaReqBean()): ApiResponse<*>














}
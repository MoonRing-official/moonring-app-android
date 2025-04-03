package com.module.common.network

import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.google.gson.GsonBuilder
import com.module.common.app.appContext
import com.moonring.jetpackmvvm.network.BaseNetworkApi
import com.moonring.jetpackmvvm.network.interceptor.logging.LogInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit




val apiService: ApiService by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
    NetworkApi.INSTANCE.getApi(ApiService::class.java, ApiService.SERVER_URL)
}




class NetworkApi : BaseNetworkApi() {

    companion object {
        val INSTANCE: NetworkApi by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            NetworkApi()
        }
    }


    override fun setHttpClientBuilder(builder: OkHttpClient.Builder): OkHttpClient.Builder {
        builder.apply {

            cache(Cache(File(appContext.cacheDir, "cxk_cache"), 10 * 1024 * 1024))

            cookieJar(cookieJar)

            addInterceptor(MyHeadInterceptor())

            addInterceptor(CacheInterceptor())
            addInterceptor(TokenOutInterceptor())
            addInterceptor(CookieInterceptor())

            addInterceptor(LogInterceptor())

            connectTimeout(10, TimeUnit.SECONDS)
            readTimeout(5, TimeUnit.SECONDS)
            writeTimeout(5, TimeUnit.SECONDS)
        }
        return builder
    }


    override fun setRetrofitBuilder(builder: Retrofit.Builder): Retrofit.Builder {
        return builder.apply {
            addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        }
    }

    val cookieJar: PersistentCookieJar by lazy {
        PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(appContext))
    }

}




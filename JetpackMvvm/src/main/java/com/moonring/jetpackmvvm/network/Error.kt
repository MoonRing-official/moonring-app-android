package com.moonring.jetpackmvvm.network

enum class Error(private val code: Int, private val err: String) {

    /**
     * 未知错误
     */
    UNKNOWN(1000, "Request failed, please try again later"),
    /**
     * 解析错误
     */
    PARSE_ERROR(1001, "Parse error, please try again later"),
    /**
     * 网络错误
     */
    NETWORK_ERROR(1002, "Network connection error, please try again later"),

    /**
     * 证书出错
     */
    SSL_ERROR(1004, "Certificate error, please try again later"),

    /**
     * 连接超时
     */
    TIMEOUT_ERROR(1006, "Network connection timeout, please try again later");

    fun getValue(): String {
        return err
    }

    fun getKey(): Int {
        return code
    }

}
package com.moonring.ring.utils

/**
 *    author : Administrator

 *    time   : 2024/3/14/014
 *    desc   :
 */


import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode


fun add(a: String, b: String): String {
    val _a = BigDecimal(a)
    val _b = BigDecimal(b)
    return _a.add(_b).toPlainString()
}


fun subtract(a: String, b: String): String {
    val _a = BigDecimal(a)
    val _b = BigDecimal(b)
    return _a.subtract(_b).toPlainString()
}


fun multiply(a: String, b: String): String {
    val _a = BigDecimal(a)
    val _b = BigDecimal(b)
    return _a.multiply(_b).toPlainString()
}


fun divide(a: String, b: String, n: Int, mode: RoundingMode = RoundingMode.HALF_UP): String {
    val _a = BigDecimal(a)
    val _b = BigDecimal(b)
    return _a.divide(_b, MathContext(n, mode)).toPlainString()
}

fun divide(a: BigDecimal, b: BigDecimal, n: Int, mode: RoundingMode = RoundingMode.HALF_UP): String {

    return a.divide(b, MathContext(n, mode)).toPlainString()
}


fun getPrecision(num: BigDecimal): Int {
    val str = num.toPlainString()
    val decimalIndex = str.indexOf(".")
    return if (decimalIndex == -1) 0 else str.length - decimalIndex - 1
}

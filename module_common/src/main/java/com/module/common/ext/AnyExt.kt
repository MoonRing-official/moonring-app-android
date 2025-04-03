package com.module.common.ext


import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigDecimal
import java.math.BigInteger

/**
 *    author : Administrator

 *    time   : 2022/6/8/008
 *    desc   :
 */


fun JSONObject.toMap(): Map<String, *> = keys().asSequence().associateWith {
    when (val value = this[it])
    {
        is JSONArray ->
        {
            val map = (0 until value.length()).associate { Pair(it.toString(), value[it]) }
            JSONObject(map).toMap().values.toList()
        }
        is JSONObject -> value.toMap()
        JSONObject.NULL -> null
        else            -> value
    }
}

fun String.toJson(args: String):String{
    val gson = Gson()
    val jsonStr = gson.toJson(args)
    return  jsonStr
}

fun JSONObject.getValue(key: String): Any? {
    if (has(key)) {
        return get(key)
    }
    return null
}


fun Any?.asString():String{
    return this as? String ?:""
}

fun Double?.convertDoubleToString(): String {
    try {
        val bd = BigDecimal(this.toString())
        return bd.stripTrailingZeros().toPlainString()
    }catch (e:Exception){
       e.printStackTrace()
    }
    return this.toString()
}

fun <T> List<T>.toArrayList(): ArrayList<T>{
    return ArrayList(this)
}

fun String.toBigIntSafe(): BigInteger? {
    try {
        return this.toBigInteger()
    }catch (e:Exception){
        e.printStackTrace()
    }
    return null
}

fun BigDecimal.toNoZeroPlain():String{
    return this.stripTrailingZeros().toPlainString()
}
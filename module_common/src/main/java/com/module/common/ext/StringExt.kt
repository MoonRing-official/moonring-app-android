package com.module.common.ext

/**
 *    author : Administrator

 *    time   : 2022/12/13/013
 *    desc   :
 */




fun String.toThousandSeparator(): String {
    return try {
        val number = this.toLong()
        String.format("%,d", number)
    } catch (e: NumberFormatException) {
        this
    }
}

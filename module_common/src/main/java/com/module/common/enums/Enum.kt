package com.module.common.enums

import com.module.common.R



enum class MusicType(val rawId:Int,val type:Int){
    none(0,1),
    functionclick(0,1),



}

enum class PackagingConfig(val tpye: Int) {
    DEV(1), PROD(2), PRODG(3), ONLINE(4),PRODA(5),PRODB(6),PRODC(7),PRODD(8),
}






enum class Gender(val value: Int,val displayName:String) {
    male(1,"Male"),female(0,"Female"),non_binary(2,"Prefer Not to Tell")
}

enum class HeightUnit(val value: Int) {
    cm(1),ft(2),unknown(0)
}

enum class WeightUnit(val value: Int) {
    kg(1),lb(2),unknown(0)
}

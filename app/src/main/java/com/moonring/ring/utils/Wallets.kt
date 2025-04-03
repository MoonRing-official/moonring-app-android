package com.moonring.ring.utils




fun String.toWalletSimaple():String{
    if (this == ""){
        return ""
    }
    return "${this.subSequence(0,9)}...${this.subSequence(this.length - 6, this.length)}"
}

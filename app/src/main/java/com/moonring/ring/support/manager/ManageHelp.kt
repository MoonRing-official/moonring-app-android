package com.moonring.ring.support.manager

import com.moonring.ring.homeAppViewModel


fun toForeground(){
    homeAppViewModel.toForeground.value = true
}

fun toBackground(){
    homeAppViewModel.toForeground.value = false
}
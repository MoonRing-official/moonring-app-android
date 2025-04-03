package com.module.common.bean.login

import com.module.common.bean.UserProfileMoonRing
import com.module.common.data.moudle.bean.ApiResponse

/**
 *    author : Administrator

 *    time   : 2024/3/13/013
 *    desc   :
 */






data class WalletInfo(
    val address: String
)

data class RingInfo(
    val mac_address: String,
    val last_synced: Long
)


class UserResponse : ApiResponse<UserProfileMoonRing>() {

}

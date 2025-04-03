package com.module.common.app.event

import com.module.common.app.appContext

import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.module.common.bean.UserProfileMoonRing
import com.moonring.jetpackmvvm.base.viewmodel.BaseViewModel
import com.moonring.jetpackmvvm.callback.livedata.event.EventLiveData
import com.module.common.util.CacheUtil
import com.module.common.util.SettingUtil


class AppViewModel : BaseViewModel() {




    var userInfo = UnPeekLiveData.Builder<UserProfileMoonRing>().setAllowNullValue(true).create()


    var appColor = EventLiveData<Int>()




    init {

        userInfo.value = CacheUtil.getUser()

        appColor.value = SettingUtil.getColor(appContext)


    }


}
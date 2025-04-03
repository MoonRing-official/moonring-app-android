package com.module.common.ext

import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.blankj.utilcode.util.ToastUtils
import com.module.common.R
import com.module.common.app.isLogin
import com.module.common.util.CacheUtil
import com.module.common.util.SettingUtil
import com.moonring.jetpackmvvm.ext.navigateAction
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException





fun getProcessName(pid: Int): String? {
    var reader: BufferedReader? = null
    try {
        reader = BufferedReader(FileReader("/proc/$pid/cmdline"))
        var processName = reader.readLine()
        if (!TextUtils.isEmpty(processName)) {
            processName = processName.trim { it <= ' ' }
        }
        return processName
    } catch (throwable: Throwable) {
        throwable.printStackTrace()
    } finally {
        try {
            reader?.close()
        } catch (exception: IOException) {
            exception.printStackTrace()
        }

    }
    return null
}



fun Fragment.joinPhoneWebview(url: String){
    try {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }catch (e: Exception) {
        e.printStackTrace()
    }

}


fun NavController.jumpByLogin(action: (NavController) -> Unit) {
    if (isLogin()) {
        action(this)
    } else {

    }
}


fun NavController.jumpByLogin(
    actionLogin: (NavController) -> Unit,
    action: (NavController) -> Unit
) {
    if (isLogin()) {
        action(this)
    } else {
        actionLogin(this)
    }
}


fun List<*>?.isNull(): Boolean {
    return this?.isEmpty() ?: true
}

fun List<*>?.isNotNull(): Boolean {
    return this != null && this.isNotEmpty()
}




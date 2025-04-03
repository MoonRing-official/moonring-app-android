package com.module.common.ext

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.module.common.weight.dialog.LoadingDialog
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import com.moonring.jetpackmvvm.enum.LoadingEunm




private var loadingDialog: LoadingDialog? = null


fun AppCompatActivity.showLoadingExt(message: String = "loading",loadingEunm: LoadingEunm = LoadingEunm.moonring) {
    if (!this.isFinishing) {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog(this)




        }

        loadingDialog?.showByLoadingEunm(loadingEunm)
    }
}


fun Fragment.showLoadingExt(message: String = "loading",
                            loadingEunm: LoadingEunm = LoadingEunm.moonring, autoClose:Boolean = false) {
    activity?.let {
        if (!it.isFinishing) {
            if (loadingDialog == null) {
                loadingDialog = LoadingDialog(it)




            }

            loadingDialog?.showByLoadingEunm(loadingEunm)
        }
    }
}


fun Fragment.dismissLoadingCallback(listener: ()->Unit){
    activity?.let {
        loadingDialog?.setOnDismissListener{
            listener.invoke()
            loadingDialog = null
        }
        loadingDialog?.dismiss()

    }
}

fun Activity.dismissLoadingCallback(listener:  ()->Unit){
    loadingDialog?.setOnDismissListener{
        listener.invoke()
        loadingDialog = null
    }
    loadingDialog?.dismiss()
}



fun Activity?.dismissLoadingExt() {

    loadingDialog?.dismiss()
    loadingDialog = null
}


fun Fragment?.dismissLoadingExt() {

    loadingDialog?.dismiss()
    loadingDialog = null
}


fun Activity?.delayDismissLoadingExt() {
    delaydismissLoading()
}


fun Fragment?.delayDismissLoadingExt() {

    delaydismissLoading()
}
fun delaydismissLoading(){
    runBlocking {



        val delayTime = async {  delay(5000) }
        delayTime.await()
        loadingDialog?.dismiss()
        loadingDialog = null

    }
}







package com.module.common.base.action;

import com.module.common.weight.toast.ToastSingleton;
import com.module.common.weight.toast.ToastVOType;

import androidx.annotation.StringRes;


public interface ToastAction {

    default void toast(CharSequence text) {
        ToastSingleton.INSTANCE.show(text);
    }

    default void toast(@StringRes int id) {
        ToastSingleton.INSTANCE.show(id);
    }




    default void toastSuccess(CharSequence text) {
        ToastSingleton.INSTANCE.show(ToastVOType.success,text,2000);
    }
    default void toastError(CharSequence text) {
        ToastSingleton.INSTANCE.show(ToastVOType.error,text,2000);
    }
    default void toastWarning(CharSequence text) {
        ToastSingleton.INSTANCE.show(ToastVOType.warning,text,2000);
    }

    default void toastWarning(@StringRes int id) {
        ToastSingleton.INSTANCE.show(ToastVOType.warning,id,2000);
    }
    default void toastError(@StringRes int id) {
        ToastSingleton.INSTANCE.show(ToastVOType.error,id,2000);
    }
    default void toastSuccess(@StringRes int id) {
        ToastSingleton.INSTANCE.show(ToastVOType.success,id,2000);
    }

    default void toast(Object object) {
        ToastSingleton.INSTANCE.show(object);
    }
}
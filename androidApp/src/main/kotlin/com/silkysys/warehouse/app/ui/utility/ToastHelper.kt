package com.silkysys.warehouse.wms.app.ui.utility

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

object ToastHelper {

    // Show toast from Context
    fun show(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }

    // Show toast from Fragment
    fun show(fragment: Fragment, message: String, duration: Int = Toast.LENGTH_SHORT) {
        show(fragment.requireContext(), message, duration)
    }

    // Show toast with string resource
    fun show(context: Context, @StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, resId, duration).show()
    }

    // Extension function for Context
    fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        ToastHelper.show(this, message, duration)
    }

    // Extension function for Fragment
    fun Fragment.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        ToastHelper.show(this, message, duration)
    }
}

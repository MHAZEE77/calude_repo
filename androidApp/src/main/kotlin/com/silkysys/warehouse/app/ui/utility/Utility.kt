package com.silkysys.warehouse.app.ui.utility

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.silkysys.warehouse.app.autentcation.login.LoginActivity
import com.silkysys.warehouse.app.ui.compose_ui.dialog_boxes.ProgressDialogContent


object Utility {

    fun logoutAccount(context: Context) {
        val savePref = SavePref(context)
        savePref.clearSession()
        val intent = Intent(context, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)
    }

    fun getProgressDialog(activity: ComponentActivity): AlertDialog {

        val progressView = ComposeView(activity).apply {
            setViewTreeLifecycleOwner(activity)
            setViewTreeSavedStateRegistryOwner(activity)
            setContent { ProgressDialogContent() }
        }

        return AlertDialog.Builder(activity)
            .setView(progressView)
            .setCancelable(false)
            .create()
    }
}

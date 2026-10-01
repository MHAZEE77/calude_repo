package com.silkysys.warehouse.app.error_handle

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import com.silkysys.warehouse.R
import java.io.IOException

object ErrorHandler {

    fun handleException(exception: Throwable, context: Context) {
        when (exception) {

            is InterceptorHandledException -> return

            is IOException -> showErrorDialog(
                context, -1, context.getString(R.string.network_error)
            )

            else -> {
                showErrorDialog(
                    context, -1, context.getString(R.string.something_went_wrong)
                )
            }
        }
    }

    private fun showErrorDialog(context: Context, code: Int, message: String) {
        val activity = context as? Activity ?: return
        if (activity.isFinishing || activity.isDestroyed) return

        activity.runOnUiThread {
            AlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.Ooops))
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(activity.getString(R.string.ok)) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }
}
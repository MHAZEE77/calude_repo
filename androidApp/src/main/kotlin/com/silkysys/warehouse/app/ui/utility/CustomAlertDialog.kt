package com.silkysys.warehouse.wms.app.ui.utility

import android.app.AlertDialog
import android.content.Context
import android.view.View
import androidx.annotation.StringRes

// Extension functions for Context
fun Context.alert(init: CustomAlertDialog.() -> Unit): CustomAlertDialog {
    return CustomAlertDialog(this).apply(init)
}

fun Context.alert(message: String, init: CustomAlertDialog.() -> Unit): CustomAlertDialog {
    return CustomAlertDialog(this)
        .message(message)
        .apply(init)
}

fun Context.alert(@StringRes messageRes: Int, init: CustomAlertDialog.() -> Unit): CustomAlertDialog {
    return CustomAlertDialog(this)
        .message(messageRes)
        .apply(init)
}

class CustomAlertDialog(private val context: Context) {
    var title: String = ""
    var message: String = ""
    var isCancelable: Boolean = true
    private var positiveButtonText: String = context.getString(android.R.string.ok)
    private var negativeButtonText: String? = null
    private var positiveAction: (() -> Unit)? = null
    private var negativeAction: (() -> Unit)? = null
    var customView: View? = null

    // Title setters
    fun title(title: String): CustomAlertDialog {
        this.title = title
        return this
    }

    fun title(@StringRes titleRes: Int): CustomAlertDialog {
        this.title = context.getString(titleRes)
        return this
    }

    // Message setters
    fun message(message: String): CustomAlertDialog {
        this.message = message
        return this
    }

    fun message(@StringRes messageRes: Int): CustomAlertDialog {
        this.message = context.getString(messageRes)
        return this
    }

    // Cancelable configuration
    fun isCancelable(cancelable: Boolean): CustomAlertDialog {
        this.isCancelable = cancelable
        return this
    }

    // Button configurations
    fun positiveButton(text: String = context.getString(android.R.string.ok), action: () -> Unit = {}): CustomAlertDialog {
        this.positiveButtonText = text
        this.positiveAction = action
        return this
    }

    fun negativeButton(text: String = context.getString(android.R.string.cancel), action: () -> Unit = {}): CustomAlertDialog {
        this.negativeButtonText = text
        this.negativeAction = action
        return this
    }

    fun yesButton(action: () -> Unit = {}): CustomAlertDialog {
        return positiveButton(context.getString(android.R.string.yes), action)
    }

    fun noButton(action: () -> Unit = {}): CustomAlertDialog {
        return negativeButton(context.getString(android.R.string.no), action)
    }

    // Custom view
    fun setCustomView(view: View): CustomAlertDialog {
        this.customView = view
        return this
    }

    fun show() {
        val builder = AlertDialog.Builder(context)
            .setCancelable(isCancelable)
            .setPositiveButton(positiveButtonText) { _, _ ->
                positiveAction?.invoke()
            }

        if (title.isNotEmpty()) {
            builder.setTitle(title)
        }

        if (message.isNotEmpty()) {
            builder.setMessage(message)
        }

        customView?.let { view ->
            builder.setView(view)
        }

        negativeButtonText?.let { text ->
            builder.setNegativeButton(text) { _, _ ->
                negativeAction?.invoke()
            }
        }

        builder.show()
    }
}

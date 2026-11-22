package com.silkysys.warehouse.app.ui.utility.extension

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.location.Location
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.silkysys.warehouse.R
import com.silkysys.warehouse.app.ui.activity.BaseActivity
import com.silkysys.warehouse.app.ui.compose_ui.dialog_boxes.ErrorAlertDialog
import com.silkysys.warehouse.app.ui.compose_ui.dialog_boxes.SuccessAlertDialog
import java.text.SimpleDateFormat
import java.util.Date

fun Context.showErrorAlert(
    message: String,
    buttonName: String = getString(android.R.string.ok).capitalize(),
    callback: (() -> Unit)? = null
) {
    showStatusAlert(
        isError = true,
        title = getString(R.string.error).capitalize(),
        message = message,
        buttonName = buttonName,
        callback = callback,
    )
}
fun Context.showToast(message: String){
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.showSuccessAlert(message: String, callback: (() -> Unit)? = null) {
    showStatusAlert(
        isError = false,
        title = getString(R.string.success).capitalize(),
        message = message,
        buttonName = getString(android.R.string.ok).capitalize(),
        callback = callback,
    )
}

private fun Context.showStatusAlert(
    isError: Boolean,
    title: String,
    message: String,
    buttonName: String,
    callback: (() -> Unit)?,
) {
    val activity = findComponentActivity()
    if (activity == null) {
        // No activity to host Compose in: plain platform dialog without the icon.
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(buttonName) { _, _ -> callback?.invoke() }
            .show()
        return
    }
    if (activity.isFinishing || activity.isDestroyed) return

    val host = ComposeView(activity)
    val onConfirm = {
        host.post { (host.parent as? ViewGroup)?.removeView(host) }
        callback?.invoke()
        Unit
    }
    host.setContent {
        if (isError) {
            ErrorAlertDialog(title = title, message = message, confirmText = buttonName, onConfirm = onConfirm)
        } else {
            SuccessAlertDialog(title = title, message = message, confirmText = buttonName, onConfirm = onConfirm)
        }
    }
    activity.addContentView(
        host,
        ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT),
    )
}

private tailrec fun Context.findComponentActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.findComponentActivity()
    else -> null
}

fun BaseActivity.vibratePhone() {
    val vibrator = activeContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= 26) {
        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        vibrator.vibrate(200)
    }
}

fun Context.playSoundOf(name: String) {
    val resID = resources.getIdentifier(name, "raw", packageName)
    val mediaPlayer = MediaPlayer.create(this, resID)
    mediaPlayer.start()
}

fun BaseActivity.playCorrectSound() {
    this.playSoundOf("correct")
    this.vibratePhone()
}

fun BaseActivity.playWrongSound() {
    this.playSoundOf("wrong")
    this.vibratePhone()
}

fun Location.toLatLng(): LatLng {
    return LatLng(this.latitude, this.longitude)
}

fun <T> MutableLiveData<T>.notifyObserver() {
    this.value = this.value
}

@SuppressLint("SimpleDateFormat")
fun String.parseToDate(): Date? {
    val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
    return df.parse(this)
}

@SuppressLint("SimpleDateFormat")
fun Date.formatToWeekDaysAndMonth(): String {
    val df = SimpleDateFormat("yyyy-MM-dd HH:mm aa")
    return df.format(this)
}

@SuppressLint("SimpleDateFormat")
fun Date.formatToYear(): String {
    val df = SimpleDateFormat("yyyy")
    return df.format(this)
}


@SuppressLint("SimpleDateFormat")
fun Date.formatForApi(): String {
    val df = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
    return df.format(this)
}

fun Context.customAlertDialog(title: String?, message: String) {
    AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        .show()
}
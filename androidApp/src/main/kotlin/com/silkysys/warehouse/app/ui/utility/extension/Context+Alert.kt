package com.silkysys.warehouse.app.ui.utility.extension

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.location.Location
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.silkysys.warehouse.R
import com.silkysys.warehouse.app.ui.activity.BaseActivity
import com.silkysys.warehouse.wms.app.ui.utility.alert
import java.text.SimpleDateFormat
import java.util.Date

fun Context.showErrorAlert(
    message: String,
    buttonName: String = getString(android.R.string.ok).capitalize(),
    callback: (() -> Unit)? = null
) {
    val view = LayoutInflater.from(this).inflate(R.layout.alert_fali_layout, LinearLayout(this))
    val textView = view.findViewById<TextView>(R.id.textView)
    textView.text = message
    alert {
        this.title = getString(R.string.error).capitalize()
//        this.message = message
        this.customView = view
        this.isCancelable = false
        this.positiveButton(buttonName) { callback?.invoke() }
    }.show()
}
fun Context.showToast(message: String){
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.showSuccessAlert(message: String, callback: (() -> Unit)? = null) {
    val view = LayoutInflater.from(this).inflate(R.layout.alert_success_layout, LinearLayout(this))
    val textView = view.findViewById<TextView>(R.id.textView)
    textView.text = message
    alert {
        this.title = getString(R.string.success).capitalize()
//        this.message = message
        this.customView = view
        this.isCancelable = false
        this.positiveButton(getString(android.R.string.ok).capitalize()) { callback?.invoke() }
    }.show()
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



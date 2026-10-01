package com.silkysys.warehouse.app.ui.utility.extension

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.silkysys.warehouse.app.ui.activity.BaseActivity

fun Activity.hideKeyboard() {
    if (this is BaseActivity)
        hideKeyboard(currentFocus ?: View(this))
}

fun BaseActivity.hideKeyboard(view: View) {
    val inputMethodManager = activeContext.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

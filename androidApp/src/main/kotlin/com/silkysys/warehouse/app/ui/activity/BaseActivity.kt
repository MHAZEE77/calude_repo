package com.silkysys.warehouse.app.ui.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.silkysys.warehouse.R
import com.silkysys.warehouse.app.error_handle.AuthEvent
import com.silkysys.warehouse.app.autentcation.login.LoginActivity
import com.silkysys.warehouse.app.ui.utility.LocaleManager
import com.silkysys.warehouse.app.ui.utility.SavePref
import com.silkysys.warehouse.app.ui.utility.Utility
import kotlinx.coroutines.launch

open class BaseActivity : AppCompatActivity() {

    private var activeBaseContext: Context? = null
    public val activeContext: Context
        get() = activeBaseContext ?: this

    private var errorDialog: AlertDialog? = null

    override fun attachBaseContext(base: Context) {
        activeBaseContext = base
        super.attachBaseContext(LocaleManager.setLocale(base))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                AuthEvent.httpError.collect(::showErrorDialog)
            }
        }
    }

    protected fun requireAuthenticatedSession(): Boolean {
        if (SavePref(this).hasActiveSession()) return true
        startActivity(
            Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("session_expired", true)
            },
        )
        finish()
        return false
    }

    private fun showErrorDialog(error: AuthEvent.AuthError) {
        if (isFinishing || isDestroyed || errorDialog?.isShowing == true) return

        errorDialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.something_went_wrong))
            .setMessage(error.message)
            .setCancelable(false)
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
                errorDialog = null
                if (error.code == 401) {
                    Utility.logoutAccount(this)
                    finishAffinity()
                }
            }
            .show()
    }
}

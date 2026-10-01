package com.silkysys.warehouse.app.autentcation.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.installations.FirebaseInstallations
import com.silkysys.warehouse.R
import com.silkysys.warehouse.app.autentcation.login.LoginActivity
import com.silkysys.warehouse.app.autentcation.main.activity.MainActivity
import com.silkysys.warehouse.app.ui.activity.BaseActivity
import com.silkysys.warehouse.app.ui.utility.SavePref

class LauncherActivity : BaseActivity() {
    private lateinit var savePref: SavePref
    private lateinit var appUpdateManager: AppUpdateManager
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savePref = SavePref(this)
        setContentView(R.layout.activity_launcher)
        appUpdateManager = AppUpdateManagerFactory.create(this)

        FirebaseInstallations.getInstance().getToken(false)
            .addOnSuccessListener { result -> savePref.deviceToken = result.token }

        handler.postDelayed(::checkUpdate, SPLASH_DELAY_MS)
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    private fun checkUpdate() {
        appUpdateManager.appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                    appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)
                ) {
                    @Suppress("DEPRECATION")
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, IMMEDIATE, this, UPDATE_REQUEST_CODE)
                } else {
                    routeToNextScreen()
                }
            }
            .addOnFailureListener { routeToNextScreen() }
    }

    override fun onResume() {
        super.onResume()
        if (!::appUpdateManager.isInitialized) return
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                @Suppress("DEPRECATION")
                appUpdateManager.startUpdateFlowForResult(appUpdateInfo, IMMEDIATE, this, UPDATE_REQUEST_CODE)
            }
        }
    }

    @Deprecated("Deprecated by Android; retained for Play in-app update compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UPDATE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) routeToNextScreen() else checkUpdate()
        }
    }

    private fun routeToNextScreen() {
        val destination = if (savePref.hasActiveSession()) MainActivity::class.java else LoginActivity::class.java
        startActivity(
            Intent(this, destination).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            },
        )
        finish()
    }

    companion object {
        private const val SPLASH_DELAY_MS = 1_200L
        private const val UPDATE_REQUEST_CODE = 1
    }
}

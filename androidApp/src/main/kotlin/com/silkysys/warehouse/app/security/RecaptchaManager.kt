package com.silkysys.warehouse.app.security

import android.app.Application
import android.content.Context
import com.google.android.recaptcha.Recaptcha
import com.google.android.recaptcha.RecaptchaAction
import com.google.android.recaptcha.RecaptchaClient
import com.silkysys.warehouse.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class RecaptchaManager(context: Context) {
    private val application = context.applicationContext as Application
    private var recaptchaClient: RecaptchaClient? = null
    private val recaptchaScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        if (BuildConfig.SITE_KEY.isNotBlank()) initializeRecaptchaClient()
    }

    private fun initializeRecaptchaClient() {
        recaptchaScope.launch {
            runCatching { Recaptcha.fetchClient(application, BuildConfig.SITE_KEY) }
                .onSuccess { recaptchaClient = it }
        }
    }

    suspend fun executeRecaptcha(action: RecaptchaAction): Result<String> {
        if (BuildConfig.SITE_KEY.isBlank()) {
            return Result.failure(IllegalStateException("reCAPTCHA is not configured"))
        }
        val client = recaptchaClient
            ?: return Result.failure(IllegalStateException("reCAPTCHA is not ready"))
        return runCatching { client.execute(action, timeout = 10_000L).getOrThrow() }
    }
}
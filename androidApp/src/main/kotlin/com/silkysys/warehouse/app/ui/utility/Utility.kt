package com.silkysys.warehouse.app.ui.utility

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.view.LayoutInflater
import android.widget.ProgressBar
import com.silkysys.warehouse.R
import com.silkysys.warehouse.app.autentcation.login.LoginActivity
import com.silkysys.warehouse.app.autentcation.main.activity.MainActivity
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import kotlin.random.Random


object Utility {

    fun changeLanguageDialog(context: Context) {
        // getting current language
        // savePref.language
        val language = SavePref(context).language

        // Late initialize an alert dialog object
        lateinit var dialog: AlertDialog

        // Initialize a new instance of alert dialog builder object
        val builder = AlertDialog.Builder(context)

        // Set a title for alert dialog
        builder.setTitle(context.getString(R.string.change_language))

//        var selectIndex = if (language == LocaleManager.englishFlag) 0 else 1
        var selectIndex = LocaleManager.getLocaleIndex(language) ?: 0

        builder.setSingleChoiceItems(LocaleManager.arrayLanguage, selectIndex) { _, i ->
            selectIndex = i
        }

        // Set the positive/yes button click listener
        builder.setPositiveButton(context.getString(R.string.change)) { _, _ ->

            val selectedLanguage = LocaleManager.arrayFlag[selectIndex]

            if (language != selectedLanguage) {
                //requestUpdateLocalization(context, selectedLanguage)
                LocaleManager.setNewLocale(context, selectedLanguage)
                context.startActivity(
                    Intent(context, MainActivity::class.java).apply {
                        addFlags(FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK or FLAG_ACTIVITY_SINGLE_TOP)
                    }
                )
            }
        }

        // Set the negative/no button click listener
        builder.setNegativeButton(context.getString(android.R.string.cancel)) { _, _ -> }

        // Initialize the AlertDialog using builder object
        dialog = builder.create()

        // Finally, display the alert dialog
        dialog.show()
    }


    fun logoutAccount(context: Context) {
        val savePref = SavePref(context)
        savePref.clearSession()
        val intent = Intent(context, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)
    }


    @Suppress("DEPRECATION")
    fun getProgressDialog(activity: Activity): AlertDialog {

        return AlertDialog.Builder(activity)
            .setView(LayoutInflater.from(activity).inflate(R.layout.dialog_progress, null))
            .setCancelable(false)
            .create()

    }

    fun getProgressBar(activity: Activity): ProgressBar {
        return activity.findViewById(R.id.tv_PD_Weight)
    }


    fun generateIdempotencyKey(): String {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val datetime = buildString {
            append(now.year.toString().padStart(4, '0'))
            append(now.monthNumber.toString().padStart(2, '0'))
            append(now.dayOfMonth.toString().padStart(2, '0'))
            append(now.hour.toString().padStart(2, '0'))
            append(now.minute.toString().padStart(2, '0'))
            append(now.second.toString().padStart(2, '0'))
        }
        val random = Random.nextInt(100000, 999999)
        return "$datetime-$random"
    }


    suspend inline fun <reified T> HttpResponse.parseBodyOrDefault(): T {
        val statusCode = status.value
        return try {
            val json = Json { ignoreUnknownKeys = true }
            json.decodeFromString<T>(bodyAsText())
                .also {
                    if (it is WrappedResponse<*>) {
                        it.code = statusCode
                    }
                }
        } catch (e: Exception) {
            WrappedResponse<Any>(
                code = statusCode,
                message = "Unable to process the server response.",
                status = false,
                error = "invalid_server_response"
            ) as T
        }
    }

}

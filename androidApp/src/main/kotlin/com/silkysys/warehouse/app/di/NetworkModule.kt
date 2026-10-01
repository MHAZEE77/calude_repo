package com.silkysys.warehouse.app.di

import android.annotation.SuppressLint
import android.content.Context
import com.silkysys.warehouse.BuildConfig
import com.silkysys.warehouse.R
import com.silkysys.warehouse.app.error_handle.AuthEvent
import com.silkysys.warehouse.app.error_handle.InterceptorHandledException
import com.silkysys.warehouse.app.ui.utility.SavePref
import com.silkysys.warehouse.app.ui.utility.Utility.generateIdempotencyKey
import com.silkysys.warehouse.sharedcode.data.remote.ApiService
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.accept
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

val networkModule = module {
    single { provideHttpClient(get(), androidContext()) }
    single { ApiService(get()) }
}

@SuppressLint("StringFormatInvalid")
fun provideHttpClient(savePref: SavePref, context: Context): HttpClient = HttpClient(OkHttp) {
    expectSuccess = false

    engine {
        config {
            connectTimeout(30, TimeUnit.SECONDS)
            readTimeout(60, TimeUnit.SECONDS)
            writeTimeout(60, TimeUnit.SECONDS)
            retryOnConnectionFailure(true)
        }
    }

    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                encodeDefaults = false
                explicitNulls = false
            },
        )
    }

//    install(Logging) {
//        logger = object : Logger {
//            override fun log(message: String) {
//                Log.d("KtorClient", message)
//            }
//        }
//        level = LogLevel.ALL
//    }

    defaultRequest {
        url(BuildConfig.BASE_URL)
        contentType(ContentType.Application.Json)
        accept(ContentType.Application.Json)
        header("lang", savePref.language)
        header("X-DEVICE", "app")
        savePref.bearerAuthorization.takeIf { it.isNotBlank() }?.let { header("Authorization", it) }
        savePref.refreshToken.takeIf { it.isNotBlank() }?.let { header("X-Refresh-Token", it) }
        savePref.clientId.takeIf { it.isNotBlank() }?.let { header("X-Client-Id", it) }
        header("Idempotency-Key", generateIdempotencyKey())
    }

    @Serializable
    data class ApiErrorResponse(
        val message: String? = null
    )

    HttpResponseValidator {
        validateResponse { response ->
            if (response.status.isSuccess() || response.status.value == 400) return@validateResponse

            val code = response.status.value
            val bodyText = response.bodyAsText()

            val serverMessage = runCatching {
                Json {
                    ignoreUnknownKeys = true
                }.decodeFromString<ApiErrorResponse>(bodyText).message
            }.getOrNull()

            val errorMessage = when (code) {
                401 -> context.getString(R.string.your_session_has_expired_please_log_in_again)
                403 -> context.getString(R.string.access_denied_you_don_t_have_permission)
                404 -> context.getString(R.string.resource_not_found)
                422 -> {
                    serverMessage
                        ?: context.getString(R.string.the_recaptcha_verification_failed)
                }
                500 -> context.getString(R.string.server_error_please_try_again)
                503 -> context.getString(R.string.service_unavailable_please_try_again_later)
                else -> context.getString(R.string.unexpected_error, code)
            }
            AuthEvent.postError(AuthEvent.AuthError(code, errorMessage))
            throw InterceptorHandledException(code, errorMessage)
        }

        handleResponseExceptionWithRequest { exception, _ ->
            throw exception
        }
    }
}

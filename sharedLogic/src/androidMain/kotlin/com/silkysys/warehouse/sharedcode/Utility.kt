package com.silkysys.warehouse.sharedcode

import android.util.Log
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer


val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

object Utility {

    suspend inline fun <reified T> HttpResponse.parseBodyOrDefault(): T {
        val statusCode = status.value
        val bodyText = bodyAsText()
        return try {
            json.decodeFromString<T>(serializer(), bodyText)
                .also {
                    if (it is WrappedResponse<*>) {
                        it.code = statusCode
                    }
                }
        } catch (e: Exception) {
            //Log.e("**ERROR:**", "\"**ERROR:** - \" ${e.message}")
            @Suppress("UNCHECKED_CAST")
            WrappedResponse<Any>(
                code = statusCode,
                message = "Unable to process the server response.",
                status = false,
                error = "invalid_server_response"
            ) as T
        }
    }
}

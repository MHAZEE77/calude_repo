package com.silkysys.warehouse.app.network

import com.silkysys.warehouse.app.error_handle.InterceptorHandledException
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.plugin
import io.ktor.client.request.header
import io.ktor.http.HttpMethod
import io.ktor.http.content.OutgoingContent
import io.ktor.http.encodedPath
import java.util.UUID

private const val IDEMPOTENCY_HEADER = "Idempotency-Key"

private val MUTATING_METHODS = setOf(HttpMethod.Post, HttpMethod.Put, HttpMethod.Patch, HttpMethod.Delete)

// Auth endpoints are not stock mutations and must never be replayed from a stored key.
private val EXCLUDED_PATH_MARKERS = listOf("/login", "/logout", "/auth/")

fun HttpClient.installOperationIdempotency(store: PendingOperationStore) {
    plugin(HttpSend).intercept { request ->
        val bodyBytes = (request.body as? OutgoingContent)?.let(::bodyBytesOrNull)
        val path = request.url.encodedPath.lowercase()

        // Durable (reused-on-retry) keys only for stock/fulfillment mutations with a fingerprintable body.
        val durable = request.method in MUTATING_METHODS &&
                EXCLUDED_PATH_MARKERS.none { path.contains(it) } &&
                bodyBytes != null

        if (!durable) {
            // Every other call (GET, login/logout/auth, streaming bodies) still carries a key,
            // a fresh one per call, exactly like before.
            request.header(IDEMPOTENCY_HEADER, UUID.randomUUID().toString())
            return@intercept execute(request)
        }

        val fingerprint = PendingOperationStore.fingerprint(
            method = request.method.value,
            url = request.url.buildString(),
            body = bodyBytes!!,
        )
        request.header(IDEMPOTENCY_HEADER, store.keyFor(fingerprint))

        try {
            val call = execute(request)
            if (isTerminal(call.response.status.value)) store.resolve(fingerprint)
            call
        } catch (e: InterceptorHandledException) {
            // HttpResponseValidator throws for non-2xx (except 400) before we see the response.
            if (isTerminal(e.code)) store.resolve(fingerprint)
            throw e
        }
        // Any other exception (timeout, connection loss, cancellation): outcome unknown -> keep the ID.
    }
}

private fun bodyBytesOrNull(content: OutgoingContent): ByteArray? = when (content) {
    is OutgoingContent.NoContent -> ByteArray(0)
    is OutgoingContent.ByteArrayContent -> content.bytes()
    else -> null
}

private fun isTerminal(status: Int): Boolean = when (status) {
    in 200..299 -> true
    401, 408, 409, 425, 429 -> false
    in 400..499 -> true
    else -> false // 5xx and anything unexpected: outcome unknown, keep the ID
}
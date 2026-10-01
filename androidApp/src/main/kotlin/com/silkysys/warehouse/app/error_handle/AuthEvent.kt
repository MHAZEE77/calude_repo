package com.silkysys.warehouse.app.error_handle

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object AuthEvent {
    private val _httpError = MutableSharedFlow<AuthError>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val httpError = _httpError.asSharedFlow()

    fun postError(error: AuthError) {
        _httpError.tryEmit(error)
    }

    data class AuthError(val code: Int, val message: String)
}
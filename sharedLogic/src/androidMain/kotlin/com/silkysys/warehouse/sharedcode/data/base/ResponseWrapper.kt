package com.silkysys.warehouse.sharedcode.data.base

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WrappedListResponse<T>(
    var code: Int = 0,
    @SerialName("message") var message: String = "",
    @SerialName("state") var status: Boolean = false,
    @SerialName("errors") var errors: String? = null,
    @SerialName("data") var data: List<T>? = null
)

@Serializable
data class WrappedResponse<T>(
    var code: Int = 0,
    @SerialName("message") var message: String = "",
    @SerialName("state") var status: Boolean = false,
    @SerialName("error") var error: String? = null,
    @SerialName("data") var data: T? = null
)

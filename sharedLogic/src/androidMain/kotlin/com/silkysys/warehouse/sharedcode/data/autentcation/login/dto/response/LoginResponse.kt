package com.silkysys.warehouse.sharedcode.data.autentcation.login.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(

    @SerialName("id")
    val id: Int,

    @SerialName("token_type")
    val tokenType: String?,

    @SerialName("name")
    val name: String?,

    @SerialName("email")
    val email: String?,

    @SerialName("image")
    val image: String?,

    @SerialName("is_active")
    val isActive: Boolean,

    @SerialName("role")
    val role: String,

    @SerialName("permissions")
    val permissions: List<String>,

    @SerialName("access_token")
    val accessToken: String,

    @SerialName("refresh_token")
    val refreshToken: String?,

    @SerialName("client_id")
    val clientId: String?,

    @SerialName("tenant_id")
    val tenantId: Int,

    @SerialName("locale")
    val locale: String?,

    @SerialName("default_warehouse_id")
    val defaultWarehouseId: String?
)


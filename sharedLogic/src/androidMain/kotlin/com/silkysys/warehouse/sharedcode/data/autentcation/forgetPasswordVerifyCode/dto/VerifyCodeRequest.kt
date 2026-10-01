package com.silkysys.warehouse.sharedcode.data.autentcation.forgetPasswordVerifyCode.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class VerifyCodeRequest(
    @SerialName("email")
    val email: String,
    @SerialName("code")
    val code: String
)

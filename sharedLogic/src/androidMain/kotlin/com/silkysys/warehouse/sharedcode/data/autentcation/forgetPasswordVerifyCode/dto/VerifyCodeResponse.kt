package com.silkysys.warehouse.sharedcode.data.autentcation.forgetPasswordVerifyCode.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerifyCodeResponse(
    @SerialName("reset_token")
    val resetToken: String? = null
)

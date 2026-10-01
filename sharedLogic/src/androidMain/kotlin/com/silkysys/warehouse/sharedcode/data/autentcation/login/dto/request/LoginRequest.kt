package com.silkysys.warehouse.sharedcode.data.autentcation.login.dto.request


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Created by: Mahmoud Ibrahim on 3/4/2023
 * Email: mahmoudibrahiem57@gmail.com
 **/
@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
    val device_type: String,
    val device_token: String,
    @SerialName("recaptcha_action")
    val recaptcha_action: String,
    @SerialName("g-recaptcha-response")
    val gRecaptchaResponse: String?
)

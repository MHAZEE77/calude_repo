package com.silkysys.warehouse.sharedcode.data.autentcation.updateForgetPassword.dto

data class UpdateForgetPasswordRequest(
    val email: String,
    val new_password: String,
    val confirm_new_password: String,
    val reset_token: String
)

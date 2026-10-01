package com.silkysys.warehouse.sharedcode.domain.autentcation.forgetPasswordVerifyCode.entity

data class VerifyCodeEntity(
    val message: String,
    val resetToken: String
)
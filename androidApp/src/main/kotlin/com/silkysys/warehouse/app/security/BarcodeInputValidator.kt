package com.silkysys.warehouse.app.security

object BarcodeInputValidator {
    const val MAX_LENGTH = 128

    fun normalize(rawValue: String?): String? {
        val value = rawValue?.trim().orEmpty()
        if (value.isBlank() || value.length > MAX_LENGTH || value.any(Char::isISOControl)) return null
        return value
    }
}

package com.silkysys.warehouse.app.autentcation.login

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ValidateLoginTest {
    @Test fun blankEmailIsInvalid() = assertTrue(ValidateLogin.validateEmail(""))
    @Test fun nonBlankEmailPassesRequiredCheck() = assertFalse(ValidateLogin.validateEmail("operator@example.com"))
    @Test fun blankPasswordIsInvalid() = assertTrue(ValidateLogin.validatePassword(""))
    @Test fun nonBlankPasswordPassesRequiredCheck() = assertFalse(ValidateLogin.validatePassword("not-stored"))
}

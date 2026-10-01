package com.silkysys.warehouse.app.security

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BarcodeInputValidatorTest {
    @Test fun trimsValidBarcode() = assertEquals("ABC-123", BarcodeInputValidator.normalize("  ABC-123  "))
    @Test fun rejectsBlankBarcode() = assertNull(BarcodeInputValidator.normalize("   "))
    @Test fun rejectsControlCharacters() = assertNull(BarcodeInputValidator.normalize("ABC\n123"))
    @Test fun rejectsOversizedBarcode() = assertNull(BarcodeInputValidator.normalize("A".repeat(BarcodeInputValidator.MAX_LENGTH + 1)))
}

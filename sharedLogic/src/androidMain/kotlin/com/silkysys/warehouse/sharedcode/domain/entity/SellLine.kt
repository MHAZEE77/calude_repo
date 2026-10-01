package com.silkysys.warehouse.sharedcode.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class SellLine(
    var id: Int,
    var transaction_id: Int,
    var quantity: Int,
    var quantity_returned: String,
    var unit_price_before_discount: String,
    var unit_price: String,
    var item_tax: String,
    var sell_line_note: String,
    var line_discount_type: String,
    var line_discount_amount: String,
    var unit_price_inc_tax: String,
    var product: Product? = null
): Parcelable

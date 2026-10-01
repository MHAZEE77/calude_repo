package com.silkysys.warehouse.sharedcode.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable


@Serializable
@Parcelize
data class Transaction(
    var id: Int,
    var type: String?,
    var sub_type: String?,
    var status: String?,
    var payment_status: String?,
    var invoice_no: String?,
    var tax_amount: String?,
    var discount_type: String?,
    var discount_amount: String?,
    var shipping_details: String?,
    var shipping_address: String?,
    var shipping_status: String?,
    var delivered_to: String?,
    var shipping_charges: String?,
    var final_total: String?,
    var created_at: String?,
    var contact: Contact? = null,
    var sell_lines: List<SellLine>
): Parcelable

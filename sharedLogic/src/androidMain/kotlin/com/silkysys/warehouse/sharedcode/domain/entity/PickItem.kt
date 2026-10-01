package com.silkysys.warehouse.sharedcode.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
class PickItem(
    var id: Int,
    var item_name: String,
    var item_description: String?,
    var item_image: String?,
    var picked_qty: Int,
    var quantity: Int,
//    var reference_id: Int,
    var size: String?,
    var SKU: String?,
//    var sort: Int?,
    var status: String
): Parcelable {
    val remain: Int
        get() = quantity - picked_qty

    object Status {
        const val pending = "pending"
        const val partialPicked = "partial_picked"
        const val picked = "picked"
    }
}

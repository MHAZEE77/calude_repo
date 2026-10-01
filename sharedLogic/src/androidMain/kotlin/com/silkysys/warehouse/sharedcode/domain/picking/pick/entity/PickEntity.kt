package com.silkysys.warehouse.sharedcode.domain.picking.pick.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PickEntity(
    val fulfillments: Fulfillments
): Parcelable {
    @Parcelize
    data class Fulfillments(
        val waiting: List<Waiting>,
        val started: List<Started>
    ) : Parcelable {
        @Parcelize
        data class Started(
            val created_at: String,
            val id: Int,
            val name: String,
            val status: String,
            val total_items: Int,
            val total_items_picked_quantity: Int,
            val total_items_quantity: Int,
        ) : Parcelable

        @Parcelize
        data class Waiting(
            val created_at: String,
            val id: Int,
            val name: String,
            val status: String,
            val total_items: Int,
            val total_items_picked_quantity: Int,
            val total_items_quantity: Int,
        ) : Parcelable
    }
}

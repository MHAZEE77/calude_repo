package com.silkysys.warehouse.sharedcode.domain.picking.startPick.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StartPickEntity(
    val fulfillment: Fulfillment
):Parcelable {
    @Parcelize
    data class Fulfillment(
        val items: ArrayList<Item>,
    ): Parcelable {

        @Parcelize
        data class Item(
            val description: String?,
            val id: Int,
            val name: String,
            val picked_quantity: Int,
            val quantity: Int,
            val image: String,
            val status: String,
            val barcode: String?
         ):Parcelable
    }
}

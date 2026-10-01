package com.silkysys.warehouse.sharedcode.domain.putAwayModule.putAway.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class PutAwayScanEntity(
    val item: Item
):Parcelable {
    @Parcelize
    data class Item(
        val item_img: String,
        val barcode: String,
        val company_id: Int,
        val created_at: String,
        val default_warehouse: Int,
        val id: Int,
        val item_id: Int,
        val item_name: String,
        val item_quantity: String,
        val opening_stock: Int,
        val opening_stock_value: Int,
        val reorder_level: Int,
        val returnable: Int,
        val sku: String,
        val category: Category?,
        val bin_locations: MutableList<BinLocations>?
    ): Parcelable {
        @Parcelize
        data class Category(
            val name: String
        ):Parcelable
        @Parcelize
        data class BinLocations (
            val id: Int,
            val company_id: Int,
            val warehouse_id: Int,
            val code:String
            ): Parcelable
    }
}

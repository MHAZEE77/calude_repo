package com.silkysys.warehouse.sharedcode.data.putAwayModule.putAway.dto.putAwayResponse

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PutAwayScanResponse(
    @SerialName("item")
    val item: Item
) {
    @Serializable
    data class Item(
        @SerialName("barcode")
        val barcode: String,
        @SerialName("item_img")
        val item_img: String,
        @SerialName("bin_locations")
        val binLocations: List<BinLocations>,
        @SerialName("binlocation_ids")
        val binlocationIds: List<String>,
        @SerialName("company_id")
        val companyId: Int,
        @SerialName("created_at")
        val createdAt: String,
        @SerialName("created_by")
        val createdBy: String,
        @SerialName("created_from")
        val createdFrom: String,
        @SerialName("default_warehouse")
        val defaultWarehouse: Int,
        @SerialName("deleted_at")
        val deletedAt: String,
        @SerialName("id")
        val id: Int,
        @SerialName("item_id")
        val itemId: Int,
        @SerialName("item_name")
        val itemName: String,
        @SerialName("item_quantity")
        val itemQuantity: String,
        @SerialName("opening_stock")
        val openingStock: Int,
        @SerialName("opening_stock_value")
        val openingStockValue: Int,
        @SerialName("reorder_level")
        val reorderLevel: Int,
        @SerialName("returnable")
        val returnable: Int,
        @SerialName("sku")
        val sku: String,
        @SerialName("unit")
        val unit: String,
        @SerialName("updated_at")
        val updatedAt: String,
        @SerialName("vendor_id")
        val vendorId: String,
        @SerialName("warehouse_id")
        val warehouseId: Int,
        @SerialName("item_category")
        val item_category: Category
    ){
        @Serializable
        data class Category(
            @SerialName("name")
            val name: String
        )
        @Serializable
        data class BinLocations (
            @SerialName("id")
            val id: Int,
            @SerialName("company_id")
            val company_id: Int,
            @SerialName("warehouse_id")
            val warehouse_id: Int,
            @SerialName("code")
            val code:String
        )
    }
}

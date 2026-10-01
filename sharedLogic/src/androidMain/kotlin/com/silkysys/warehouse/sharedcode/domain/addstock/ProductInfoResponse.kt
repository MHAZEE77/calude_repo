package com.silkysys.warehouse.sharedcode.domain.addstock

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductInfoResponse(
    @SerialName("id")
    val id: Int = 0,
    @SerialName("type")
    val type: String = "",
    @SerialName("name")
    val name: String = "",
    @SerialName("description")
    val description: String? = null,
    @SerialName("sku")
    val sku: String? = null,
    @SerialName("purchase_price")
    val purchase_price: String? = null,
    @SerialName("sale_price")
    val sale_price: String? = null,
    @SerialName("currency")
    val currency: String = "",
    @SerialName("enabled")
    val enabled: Boolean = false,
    @SerialName("category")
    val category: Category? = null,
    @SerialName("vendor")
    val vendor: Vendor? = null,
    @SerialName("image")
    val image_url: String? = null,
    @SerialName("warehouses")
    val warehouses: List<Warehouse> = emptyList()
) {

    @Serializable
    data class Category(
        @SerialName("id")
        val id: Int = 0,
        @SerialName("name")
        val name: String = ""
    )

    @Serializable
    data class Vendor(
        @SerialName("id")
        val id: Int = 0,
        @SerialName("name")
        val name: String = ""
    )

    @Serializable
    data class Warehouse(
        @SerialName("id")
        val id: Int = 0,
        @SerialName("name")
        val name: String = "",
        @SerialName("opening_stock")
        val opening_stock: Int = 0,
        @SerialName("reserved_stock")
        val reserved_stock: Int = 0,
        @SerialName("bin_locations")
        val bin_locations: List<BinLocation> = emptyList()
    ) {
        @Serializable
        data class BinLocation(
            @SerialName("id")
            val id: Int = 0,
            @SerialName("code")
            val code: String = "",
            @SerialName("opening_stock")
            val opening_stock: Int = 0,
            @SerialName("reserved_stock")
            val reserved_stock: Int = 0
        )
    }
}

package com.silkysys.warehouse.sharedcode.domain.addstock

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddStockRequest(
    @SerialName("warehouse_id")
    val warehouseId: String? ,
    @SerialName("bin_location_id")
    val binLocationId: String?,
    @SerialName("barcode")
    val barcode: String? ,
    @SerialName("quantity")
    val quantity: String?
)

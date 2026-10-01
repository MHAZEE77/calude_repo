package com.silkysys.warehouse.sharedcode.data.picking.pick.dto.pickWaitingAndStartedResponse


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Waiting(
    @SerialName("assigned")
    val assigned: Boolean,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("document")
    val document: Document?,
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("permissions")
    val permissions: Permissions?,
    @SerialName("status")
    val status: String,
    @SerialName("total_items")
    val totalItems: Int,
    @SerialName("total_items_picked_quantity")
    val totalItemsPickedQuantity: Int,
    @SerialName("total_items_quantity")
    val totalItemsQuantity: Int,
    @SerialName("warehouse")
    val warehouse: Warehouse?
)

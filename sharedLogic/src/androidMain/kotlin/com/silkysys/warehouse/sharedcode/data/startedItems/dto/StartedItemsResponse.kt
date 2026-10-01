package com.silkysys.warehouse.sharedcode.data.startedItems.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Created by: Mahmoud Ibrahim on 3/25/2023
 * Email: mahmoudibrahiem57@gmail.com
 **/
@Serializable
class StartedItemsResponse(
@SerialName("fulfillment")
val fulfillment: Fulfillment
) {
    @Serializable
    data class Fulfillment(
        @SerialName("assigned_user_id")
        val assignedUserId: Int,
        @SerialName("company_id")
        val companyId: Int,
        @SerialName("created_at")
        val createdAt: String,
        @SerialName("created_by")
        val createdBy: Int,
        @SerialName("deleted_at")
        val deletedAt: String,
        @SerialName("delivery_type")
        val deliveryType: String,
        @SerialName("document_id")
        val documentId: Int,
        @SerialName("id")
        val id: Int,
        @SerialName("items")
        val items: List<Item>,
        @SerialName("name")
        val name: String,
        @SerialName("permissions")
        val permissions: Permissions,
        @SerialName("prepare_date")
        val prepareDate: String,
        @SerialName("status")
        val status: String,
        @SerialName("updated_at")
        val updatedAt: String,
        @SerialName("warehouse_id")
        val warehouseId: Int
    ) {
        @Serializable
        data class Item(
            @SerialName("barcode")
            val barcode: String,
            @SerialName("created_at")
            val createdAt: String,
            @SerialName("deleted_at")
            val deletedAt: String,
            @SerialName("description")
            val description: String,
            @SerialName("document_fulfillment_id")
            val documentFulfillmentId: Int,
            @SerialName("document_item_id")
            val documentItemId: Int,
            @SerialName("id")
            val id: Int,
            @SerialName("issue_note")
            val issueNote: String,
            @SerialName("location_name")
            val locationName: String,
            @SerialName("name")
            val name: String,
            @SerialName("picked_quantity")
            val pickedQuantity: Int,
            @SerialName("quantity")
            val quantity: Int,
            @SerialName("status")
            val status: String,
            @SerialName("updated_at")
            val updatedAt: String,
            @SerialName("image")
            val image: String
        )

        @Serializable
        data class Permissions(
            @SerialName("can_be_packed")
            val canBePacked: Boolean,
            @SerialName("can_be_picked")
            val canBePicked: Boolean,
            @SerialName("can_be_ready_to_pack")
            val canBeReadyToPack: Boolean,
            @SerialName("can_be_ready_to_pick")
            val canBeReadyToPick: Boolean,
            @SerialName("can_be_ready_to_ship")
            val canBeReadyToShip: Boolean,
            @SerialName("can_be_shipped")
            val canBeShipped: Boolean
        )
    }
}

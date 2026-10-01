package com.silkysys.warehouse.sharedcode.domain.picking.startedItems.entity

/**
 * Created by: Mahmoud Ibrahim on 3/25/2023
 * Email: mahmoudibrahiem57@gmail.com
 **/
class StartedItemsEntity(
val fulfillment: Fulfillment
) {
    data class Fulfillment(
        val created_at: String,
        val id: Int,
        val items: List<Item>,
        val name: String,
        val status: String,
    ) {
        data class Item(
            val created_at: String,
            val id: Int,
            val name: String,
            val picked_quantity: Int,
            val quantity: Int,
            val status: String,
        )

        data class Permissions(
            val can_be_packed: Boolean,
            val can_be_picked: Boolean,
            val can_be_ready_to_pack: Boolean,
            val can_be_ready_to_pick: Boolean,
            val can_be_ready_to_ship: Boolean,
            val can_be_shipped: Boolean
        )
    }
}

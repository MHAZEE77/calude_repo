package com.silkysys.warehouse.sharedcode.data.packing.packItems.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 *
 * Created by: Mahmoud Ibrahim on 5/8/2023
 * Email: mahmoudibrahiem57@gmail.com
 *
 **/
@Serializable
data class PackItemsResponse(
    @SerialName("items")
    val packItems: ArrayList<PackItems>
){
    @Serializable
    data class PackItems(
        @SerialName("id")
        val id: Int,
        @SerialName("name")
        val name: String,
        @SerialName("quantity_that_can_be_packed")
        val allowedQuantity: Int
    )
}

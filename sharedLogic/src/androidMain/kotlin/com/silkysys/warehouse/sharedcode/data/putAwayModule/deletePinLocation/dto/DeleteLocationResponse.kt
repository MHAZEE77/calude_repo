package com.silkysys.warehouse.sharedcode.data.putAwayModule.deletePinLocation.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 *
 * Created by: Mahmoud Ibrahim on 4/18/2023
 * Email: mahmoudibrahiem57@gmail.com
 *
 **/
@Serializable
data class DeleteLocationResponse (
    @SerialName("item")
    val item: Item
) {
    @Serializable
    data class Item(
        @SerialName("bin_locations")
        val binLocation: MutableList<BinLocations>
    ) {
        @Serializable
        data class BinLocations(
            @SerialName("id")
            val id: Int,
            @SerialName("company_id")
            val company_id: Int,
            @SerialName("warehouse_id")
            val warehouse_id: Int,
            @SerialName("code")
            val code: String
        )
    }
}

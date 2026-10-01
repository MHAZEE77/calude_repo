package com.silkysys.warehouse.sharedcode.data.packing.addPack.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 *
 * Created by: Mahmoud Ibrahim on 5/11/2023
 * Email: mahmoudibrahiem57@gmail.com
 *
 **/
@Serializable
data class AddPackRequest(
    @SerialName("height")
    val height: Int,
    @SerialName("width")
    val width: Int,
    @SerialName("length")
    val length: Int,
    @SerialName("weight")
    val weight: Int,
    @SerialName("name")
    val name: String,
    @SerialName("uom")
    val uom: String,
    @SerialName("is_fragile")
    val isFragile: Boolean,
    @SerialName("is_packed")
    val isPacked: Boolean,
    @SerialName("items")
    val items: ArrayList<Items>) {

    @Serializable
    data class Items(
        @SerialName("id")
        val id: Int,
        @SerialName("packed_quantity")
        val packedQuantity: Int
    )
}

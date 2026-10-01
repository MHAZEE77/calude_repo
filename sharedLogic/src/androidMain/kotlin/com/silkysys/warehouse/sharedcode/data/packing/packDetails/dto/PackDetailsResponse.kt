package com.silkysys.warehouse.sharedcode.data.packing.packDetails.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 *
 * Created by: Mahmoud Ibrahim on 5/4/2023
 * Email: mahmoudibrahiem57@gmail.com
 *
 **/
@Serializable
data class PackDetailsResponse(
    @SerialName("allow-new-pack")
    val canPack: Boolean,
    @SerialName("pack-list")
    val packListItems: ArrayList<PackList>) {

    @Serializable
    data class PackList(
        @SerialName("id")
        val id: Int,
        @SerialName("height")
        val height: Int,
        @SerialName("width")
        val width: Int,
        @SerialName("length")
        val length: Int,
        @SerialName("weight")
        val weight: Int,
        @SerialName("is_fragile")
        val isFragile: Int,
        @SerialName("quantity")
        val quantity: Int,
        @SerialName("name")
        val name: String,
        @SerialName("uom")
        val uom: String
    )
}

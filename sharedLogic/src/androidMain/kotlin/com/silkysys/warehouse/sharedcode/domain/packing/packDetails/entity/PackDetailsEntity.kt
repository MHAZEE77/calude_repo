package com.silkysys.warehouse.sharedcode.domain.packing.packDetails.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 *
 * Created by: Mahmoud Ibrahim on 5/4/2023
 * Email: mahmoudibrahiem57@gmail.com
 *
 **/
@Parcelize
data class PackDetailsEntity(
    val packListItems: ArrayList<PackList>,
    val canPack: Boolean) :Parcelable {
    @Parcelize
    data class PackList(
        val id: Int,
        val height: Int,
        val width: Int,
        val length: Int,
        val weight: Int,
        val isFragile: Int,
        val quantity: Int,
        val name: String,
        val uom: String
    ): Parcelable
}

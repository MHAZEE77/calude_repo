package com.silkysys.warehouse.sharedcode.domain.packing.packItems.entity

/**
 *
 * Created by: Mahmoud Ibrahim on 5/8/2023
 * Email: mahmoudibrahiem57@gmail.com
 *
 **/
data class PackItemsEntity(
    val packItems: ArrayList<PackItems>)
{
        data class PackItems(
            val id: Int,
            val name: String,
            val allowedQuantity: Int
        )
}


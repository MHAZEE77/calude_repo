package com.silkysys.warehouse.data.picking.startPickUpdateDialogQuantity.dto

/**
 * Created by: Mahmoud Ibrahim on 3/27/2023
 * Email: mahmoudibrahiem57@gmail.com
 **/
data class UpdateQuantityRequest(
    val id: Int,
    val quantity: Int,
    val picked_quantity: Int,
    val issue: Boolean,
    val issue_note: String
)

package com.silkysys.warehouse.sharedcode.data.picking.updatePickStatus.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Created by: Mahmoud Ibrahim on 3/22/2023
 * Email: mahmoudibrahiem57@gmail.com
 **/
@Serializable
data class UpdatePickStatusResponse(
    @SerialName("message")
    val message: String
)

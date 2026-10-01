package com.silkysys.warehouse.sharedcode.data.putAwayModule.putAwayProductDetails.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Created by: Mahmoud Ibrahim on 3/21/2023
 * Email: mahmoudibrahiem57@gmail.com
 **/
@Serializable
data class PutAwayProductDetailsRequest (
    @SerialName("item_barcode")
    val itemBarcode: String,
    @SerialName("bin_location_barcode")
    val binLocationBarcode: String
    )

package com.silkysys.warehouse.sharedcode.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class SearchProduct(
//    var id: Int,
    var name: String? = null,
    var name_ar: String? = null,
    var variation: String? = null,
    var product_id: Int,
    var variation_id: Int,
    var quantity: Int = 1,
    var image_url: String? = null,
    var sell_price_inc_tax: String,
    var default_sell_price: String
): Parcelable

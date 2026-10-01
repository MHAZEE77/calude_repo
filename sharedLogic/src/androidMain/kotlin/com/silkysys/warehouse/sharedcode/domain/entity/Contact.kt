package com.silkysys.warehouse.sharedcode.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Contact(
    var id: Int,
    var name: String,
    var email: String?,
    var mobile: String?,
    var supplier_business_name: String?,
    var address_line_1: String?,
    var address_line_2: String?
): Parcelable

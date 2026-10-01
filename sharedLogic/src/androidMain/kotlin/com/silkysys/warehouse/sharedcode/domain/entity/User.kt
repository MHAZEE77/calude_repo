package com.silkysys.warehouse.sharedcode.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class User(
    var id: Int,
    var username: String?,
    var first_name: String?,
    var last_name: String?,
    var contact_no: String?,
    var email: String?
): Parcelable

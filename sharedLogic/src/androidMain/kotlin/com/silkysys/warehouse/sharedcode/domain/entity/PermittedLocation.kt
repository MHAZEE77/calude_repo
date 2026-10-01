package com.silkysys.warehouse.sharedcode.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class PermittedLocation(
    var id: Int,
    var location_id: String?,
    var name: String?,
    var landmark: String?,
    var country: String?,
    var state: String?,
    var city: String?
): Parcelable

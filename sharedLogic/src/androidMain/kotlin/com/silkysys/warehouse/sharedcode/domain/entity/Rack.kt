package com.silkysys.warehouse.sharedcode.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Rack(
    var id: Int,
    var rack: String?,
    var row: String?,
    var position: String?,
    var location: String?
): Parcelable

package com.silkysys.warehouse.sharedcode.domain.autentcation.user.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class UserEntity(
    var companies: List<Company>,
    var email: String?,
    var id: Int?,
    var image_url: String?,
    var name: String?,
    var phone: String?,
        ): Parcelable{

    @Serializable
    @Parcelize
    data class Company(
        var id: Int?,
        var location: String?
    ): Parcelable
        }

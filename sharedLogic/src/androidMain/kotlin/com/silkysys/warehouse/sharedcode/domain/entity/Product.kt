package com.silkysys.warehouse.sharedcode.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Product(
    var id: Int,
    var name: String?,
    var name_ar: String?,
    var sku: String?,
    var weight: String?,
    var image_url: String?,
    var product_description: String? = null,
    var brand: Brand? = null,
    var category: Category? = null,
    var sub_category: SubCategory? = null
): Parcelable

@Serializable
@Parcelize
data class Brand(
    var id: Int,
    var name: String?
): Parcelable

@Serializable
@Parcelize
data class Category(
    var id: Int,
    var name: String?
): Parcelable

@Serializable
@Parcelize
data class SubCategory(
    var id: Int,
    var name: String?
): Parcelable


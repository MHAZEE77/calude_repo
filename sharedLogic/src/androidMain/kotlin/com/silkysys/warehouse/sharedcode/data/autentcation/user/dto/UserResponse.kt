package com.silkysys.warehouse.sharedcode.data.autentcation.user.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    @SerialName("user")
    var user: User? = null
) {
    @Serializable
    data class User(
        @SerialName("companies")
        var companies: List<Company?>? = null,
        @SerialName("company_ids")
        var companyIds: List<Int?>? = null,
        @SerialName("created_at")
        var createdAt: String? = null,
        @SerialName("created_by")
        var createdBy: String? = null,
        @SerialName("created_from")
        var createdFrom: String? = null,
        @SerialName("deleted_at")
        var deletedAt: String? = null,
        @SerialName("email")
        var email: String? = null,
        @SerialName("email_verified_at")
        var emailVerifiedAt: String? = null,
        @SerialName("enabled")
        var enabled: Boolean? = null,
        @SerialName("id")
        var id: Int? = null,
        @SerialName("image_url")
        var imageUrl: String? = null,
        @SerialName("landing_page")
        var landingPage: String? = null,
        @SerialName("last_logged_in_at")
        var lastLoggedInAt: String? = null,
        @SerialName("locale")
        var locale: String? = null,
        @SerialName("media")
        var media: List<String?>? = null,
        @SerialName("name")
        var name: String? = null,
        @SerialName("otp_code")
        var otpCode: String? = null,
        @SerialName("phone")
        var phone: String? = null,
        @SerialName("updated_at")
        var updatedAt: String? = null
    ) {
        @Serializable
        data class Company(
            @SerialName("created_at")
            var createdAt: String? = null,
            @SerialName("created_by")
            var createdBy: String? = null,
            @SerialName("created_from")
            var createdFrom: String? = null,
            @SerialName("deleted_at")
            var deletedAt: String? = null,
            @SerialName("domain")
            var domain: String? = null,
            @SerialName("enabled")
            var enabled: Boolean? = null,
            @SerialName("id")
            var id: Int? = null,
            @SerialName("image_url")
            var imageUrl: String? = null,
            @SerialName("location")
            var location: String? = null,
            @SerialName("media")
            var media: List<String?>? = null,
            @SerialName("pivot")
            var pivot: Pivot? = null,
            @SerialName("updated_at")
            var updatedAt: String? = null
        ) {
            @Serializable
            data class Pivot(
                @SerialName("company_id")
                var companyId: Int? = null,
                @SerialName("user_id")
                var userId: Int? = null
            )
        }
    }
}

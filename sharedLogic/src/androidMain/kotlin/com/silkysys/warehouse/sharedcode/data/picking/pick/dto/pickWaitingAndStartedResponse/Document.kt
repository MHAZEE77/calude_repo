package com.silkysys.warehouse.sharedcode.data.picking.pick.dto.pickWaitingAndStartedResponse

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Document(
    @SerialName("document_number")
    val documentNumber: String,
    @SerialName("id")
    val id: Int,
    @SerialName("type")
    val type: String
)

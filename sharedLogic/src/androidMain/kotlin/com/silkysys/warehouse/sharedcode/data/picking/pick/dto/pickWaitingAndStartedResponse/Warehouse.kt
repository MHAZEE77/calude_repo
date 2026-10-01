package com.silkysys.warehouse.sharedcode.data.picking.pick.dto.pickWaitingAndStartedResponse

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Warehouse(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String
)

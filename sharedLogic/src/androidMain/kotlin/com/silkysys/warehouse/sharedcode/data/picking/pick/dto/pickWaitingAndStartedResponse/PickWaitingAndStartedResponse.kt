package com.silkysys.warehouse.sharedcode.data.picking.pick.dto.pickWaitingAndStartedResponse

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PickWaitingAndStartedResponse(
    @SerialName("fulfillments")
    val fulfillments: Fulfillments
)

package com.silkysys.warehouse.sharedcode.data.picking.pick.dto.pickWaitingAndStartedResponse

import com.silkysys.warehouse.data.picking.pick.dto.pickWaitingAndStartedResponse.Started
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Fulfillments(
    @SerialName("started")
    val started: List<Started>,
    @SerialName("waiting")
    val waiting: List<Waiting>
)

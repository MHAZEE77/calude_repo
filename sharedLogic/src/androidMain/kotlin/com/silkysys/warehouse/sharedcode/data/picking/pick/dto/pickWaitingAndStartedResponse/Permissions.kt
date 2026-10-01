package com.silkysys.warehouse.sharedcode.data.picking.pick.dto.pickWaitingAndStartedResponse

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Permissions(
    @SerialName("can_be_packed")
    val canBePacked: Boolean,
    @SerialName("can_be_picked")
    val canBePicked: Boolean,
    @SerialName("can_be_ready_to_pack")
    val canBeReadyToPack: Boolean,
    @SerialName("can_be_ready_to_pick")
    val canBeReadyToPick: Boolean,
    @SerialName("can_be_ready_to_ship")
    val canBeReadyToShip: Boolean,
    @SerialName("can_be_shipped")
    val canBeShipped: Boolean
)

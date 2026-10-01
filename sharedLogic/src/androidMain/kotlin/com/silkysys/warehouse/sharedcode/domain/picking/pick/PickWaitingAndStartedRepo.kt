package com.silkysys.warehouse.sharedcode.domain.picking.pick

import com.silkysys.warehouse.sharedcode.data.picking.pick.dto.pickWaitingAndStartedResponse.PickWaitingAndStartedResponse
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.domain.picking.pick.entity.PickEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by: Mahmoud Ibrahim on 3/16/2023
 * Email: mahmoudibrahiem57@gmail.com
 **/
interface PickWaitingAndStartedRepo {
    suspend fun getPickWaitingAndStarted(companyId: Int):
            Flow<BaseResult<PickEntity, WrappedResponse<PickWaitingAndStartedResponse>>>
}

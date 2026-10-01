package com.silkysys.warehouse.sharedcode.domain.picking.pick.useCase

import com.silkysys.warehouse.sharedcode.data.picking.pick.dto.pickWaitingAndStartedResponse.PickWaitingAndStartedResponse
import com.silkysys.warehouse.sharedcode.domain.picking.pick.PickWaitingAndStartedRepo
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.domain.picking.pick.entity.PickEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by: Mahmoud Ibrahim on 3/16/2023
 * Email: mahmoudibrahiem57@gmail.com
 **/
class PickWaitingAndStartedUseCase  (
    private val pickWaitingAndStartedRepo: PickWaitingAndStartedRepo
) {
    suspend fun getPickWaitingAndStarted(companyId: Int):
            Flow<BaseResult<PickEntity, WrappedResponse<PickWaitingAndStartedResponse>>>{
        return pickWaitingAndStartedRepo.getPickWaitingAndStarted(companyId)
    }
}

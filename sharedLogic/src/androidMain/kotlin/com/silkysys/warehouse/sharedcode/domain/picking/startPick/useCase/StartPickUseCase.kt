package com.silkysys.warehouse.sharedcode.domain.picking.startPick.useCase

import com.silkysys.warehouse.sharedcode.data.picking.startPick.dto.StartPickResponse
import com.silkysys.warehouse.sharedcode.domain.picking.startPick.StartPickRepo
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.domain.picking.startPick.entity.StartPickEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by: Mahmoud Ibrahim on 3/23/2023
 * Email: mahmoudibrahiem57@gmail.com
 **/
class StartPickUseCase  (private val startPickRepo: StartPickRepo){

    suspend fun startPick(companyId: Int, fulfillmentId: Int): Flow<BaseResult<StartPickEntity, WrappedResponse<StartPickResponse>>>{
        return startPickRepo.startPick(companyId,fulfillmentId)
    }

}

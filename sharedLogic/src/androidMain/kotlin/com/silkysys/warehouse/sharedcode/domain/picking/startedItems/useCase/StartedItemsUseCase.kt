package com.silkysys.warehouse.sharedcode.domain.picking.startedItems.useCase

import com.silkysys.warehouse.sharedcode.data.startedItems.dto.StartedItemsResponse
import com.silkysys.warehouse.sharedcode.domain.picking.startPick.entity.StartPickEntity
import com.silkysys.warehouse.sharedcode.domain.picking.startedItems.StartedItemsRepo
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import kotlinx.coroutines.flow.Flow

/**
 * Created by: Mahmoud Ibrahim on 3/25/2023
 * Email: mahmoudibrahiem57@gmail.com
 **/
class StartedItemsUseCase  (private val startedItemsRepo: StartedItemsRepo){

    suspend fun getStartedList(companyId: Int, fulfillmentId: Int):
            Flow<BaseResult<StartPickEntity, WrappedResponse<StartedItemsResponse>>>{
        return startedItemsRepo.getStartedList(companyId,fulfillmentId)

    }

}

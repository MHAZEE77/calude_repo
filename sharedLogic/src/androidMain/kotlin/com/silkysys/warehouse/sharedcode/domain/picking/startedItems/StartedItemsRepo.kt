package com.silkysys.warehouse.sharedcode.domain.picking.startedItems

import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.data.startedItems.dto.StartedItemsResponse
import com.silkysys.warehouse.sharedcode.domain.picking.startPick.entity.StartPickEntity
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import kotlinx.coroutines.flow.Flow

/**
 * Created by: Mahmoud Ibrahim on 3/25/2023
 * Email: mahmoudibrahiem57@gmail.com
 **/
interface StartedItemsRepo {
    suspend fun getStartedList(companyId: Int, fulfillmentId: Int):
            Flow<BaseResult<StartPickEntity, WrappedResponse<StartedItemsResponse>>>
}

package com.silkysys.warehouse.sharedcode.domain.packing.packItems

import com.silkysys.warehouse.sharedcode.data.packing.packItems.dto.PackItemsResponse
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.domain.packing.packItems.entity.PackItemsEntity
import kotlinx.coroutines.flow.Flow

/**
 *
 * Created by: Mahmoud Ibrahim on 5/8/2023
 * Email: mahmoudibrahiem57@gmail.com
 *
 **/
interface PackItemsRepo {
    suspend fun getPackItems(companyId: Int, fulfillmentId: Int):
            Flow<BaseResult<PackItemsEntity, WrappedResponse<PackItemsResponse>>>

}

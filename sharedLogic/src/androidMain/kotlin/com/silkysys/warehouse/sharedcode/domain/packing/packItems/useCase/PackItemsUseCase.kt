package com.silkysys.warehouse.sharedcode.domain.packing.packItems.useCase

import com.silkysys.warehouse.sharedcode.data.packing.packItems.dto.PackItemsResponse
import com.silkysys.warehouse.sharedcode.domain.packing.packItems.PackItemsRepo
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
class PackItemsUseCase  (private val packItemsRepo: PackItemsRepo){
    suspend fun getPackItems(companyId: Int, fulfillmentId: Int):
            Flow<BaseResult<PackItemsEntity, WrappedResponse<PackItemsResponse>>>{
        return packItemsRepo.getPackItems(companyId,fulfillmentId)
    }
}

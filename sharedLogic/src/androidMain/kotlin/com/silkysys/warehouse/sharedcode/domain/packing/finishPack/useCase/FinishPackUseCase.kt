package com.silkysys.warehouse.sharedcode.domain.packing.finishPack.useCase

import com.silkysys.warehouse.data.packing.finishPack.dto.FinishPackResponse
import com.silkysys.warehouse.sharedcode.domain.packing.finishPack.FinishPackRepo
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.domain.packing.finishPack.entity.FinishPackEntity
import kotlinx.coroutines.flow.Flow

/**
 *
 * Created by: Mahmoud Ibrahim on 5/8/2023
 * Email: mahmoudibrahiem57@gmail.com
 *
 **/
class FinishPackUseCase  (private val finishPackRepo: FinishPackRepo){
    suspend fun finishPack(companyId: Int, fulfillmentId: Int):
            Flow<BaseResult<FinishPackEntity, WrappedResponse<FinishPackResponse>>> {
        return finishPackRepo.finishPack(companyId,fulfillmentId)
    }
}

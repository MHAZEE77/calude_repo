package com.silkysys.warehouse.sharedcode.domain.putAwayModule.putAway.useCase

import com.silkysys.warehouse.sharedcode.data.putAwayModule.putAway.dto.putAwayResponse.PutAwayScanResponse
import com.silkysys.warehouse.sharedcode.domain.putAwayModule.putAway.PutAwayRepo
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.domain.putAwayModule.putAway.entity.PutAwayScanEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by: Mahmoud Ibrahim on 3/19/2023
 * Email: mahmoudibrahiem57@gmail.com
 **/
class PutAwayUseCase  (private val putAwayRepo: PutAwayRepo) {
    suspend fun putAwayScan(itemBarcode: String):
            Flow<BaseResult<PutAwayScanEntity, WrappedResponse<PutAwayScanResponse>>>{
        return putAwayRepo.putAwayScan(itemBarcode)
    }
}

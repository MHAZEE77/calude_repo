package com.silkysys.warehouse.sharedcode.domain.addstock

import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import kotlinx.coroutines.flow.Flow


class AddStockUseCase (private val addStockRepo: AddStockRepo) {
    suspend fun addStock(addStockRequest: AddStockRequest):
            Flow<BaseResult<WrappedResponse<Unit>, WrappedResponse<Unit>>>{
        return addStockRepo.addStock(addStockRequest)
    }
}

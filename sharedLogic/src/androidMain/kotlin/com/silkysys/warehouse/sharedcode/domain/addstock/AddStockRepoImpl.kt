package com.silkysys.warehouse.sharedcode.domain.addstock

import android.annotation.SuppressLint
import com.silkysys.warehouse.sharedcode.data.remote.ApiService
import com.silkysys.warehouse.sharedcode.Utility.parseBodyOrDefault
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import  com.silkysys.warehouse.sharedcode.data.base.BaseResult

class AddStockRepoImpl (private val apiService: ApiService) : AddStockRepo {

    @SuppressLint("SuspiciousIndentation")
    override suspend fun addStock(
        addStockRequest: AddStockRequest
    ): Flow<BaseResult<WrappedResponse<Unit>, WrappedResponse<Unit>>> {
        return flow {

            val response = apiService.addStock(addStockRequest)
            val parsed = response.parseBodyOrDefault<WrappedResponse<Unit>>()
            if (response.status.isSuccess()) {
                emit(BaseResult.Success(parsed))
            } else {
                emit(BaseResult.Error(parsed))
            }
        }
    }
}

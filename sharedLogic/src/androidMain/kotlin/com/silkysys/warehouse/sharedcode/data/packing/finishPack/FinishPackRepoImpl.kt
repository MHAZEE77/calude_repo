package com.silkysys.warehouse.sharedcode.data.packing.finishPack

import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.data.packing.finishPack.dto.FinishPackResponse
import com.silkysys.warehouse.sharedcode.data.remote.ApiService
import com.silkysys.warehouse.sharedcode.domain.packing.finishPack.FinishPackRepo
import com.silkysys.warehouse.sharedcode.domain.packing.finishPack.entity.FinishPackEntity
import com.silkysys.warehouse.sharedcode.Utility.parseBodyOrDefault
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 *
 * Created by: Mahmoud Ibrahim on 5/8/2023
 * Email: mahmoudibrahiem57@gmail.com
 *
 **/
class FinishPackRepoImpl  (private val apiService: ApiService) : FinishPackRepo {
    override suspend fun finishPack(
        companyId: Int,
        fulfillmentId: Int
    ): Flow<BaseResult<FinishPackEntity, WrappedResponse<FinishPackResponse>>> {
        return flow {
            val response = apiService.finishPack(fulfillmentId)
            response.parseBodyOrDefault<WrappedResponse<FinishPackResponse>>().let { parsed ->
                if (response.status.isSuccess()) {
                    emit(BaseResult.Success(FinishPackEntity(parsed.message)))
                } else {
                    emit(BaseResult.Error(parsed))
                }
            }
        }
    }
}

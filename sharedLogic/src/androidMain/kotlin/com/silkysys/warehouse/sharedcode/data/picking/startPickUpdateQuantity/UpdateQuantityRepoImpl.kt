package com.silkysys.warehouse.sharedcode.data.picking.startPickUpdateQuantity

import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.data.picking.startPickUpdateQuantity.dto.UpdateQuantityResponse
import com.silkysys.warehouse.sharedcode.data.remote.ApiService
import com.silkysys.warehouse.sharedcode.Utility.parseBodyOrDefault
import com.silkysys.warehouse.sharedcode.domain.picking.startPickUpdateQuantity.UpdateQuantityRepo
import com.silkysys.warehouse.sharedcode.domain.picking.startPickUpdateQuantity.entity.UpdateQuantityEntity
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Created by: Mahmoud Ibrahim on 3/27/2023
 * Email: mahmoudibrahiem57@gmail.com
 **/
class UpdateQuantityRepoImpl  (private val apiService: ApiService) :
    UpdateQuantityRepo {
    override suspend fun updateQuantity(
        companyId: Int,
        fulfilmentId: Int,
        itemCode: String
    ): Flow<BaseResult<UpdateQuantityEntity, WrappedResponse<UpdateQuantityResponse>>> {
        return flow {
            val response = apiService.updateQuantity(fulfilmentId, itemCode)

            response.parseBodyOrDefault<WrappedResponse<UpdateQuantityResponse>>()
                .let { parsed ->
                    if (response.status.isSuccess()) {
                        emit(BaseResult.Success(UpdateQuantityEntity(parsed.message)))
                    } else {
                        emit(BaseResult.Error(parsed))
                    }
                }
        }
    }
}

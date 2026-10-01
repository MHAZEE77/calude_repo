package com.silkysys.warehouse.sharedcode.data.picking.startPickUpdateDialogQuantity

import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.data.picking.startPickUpdateDialogQuantity.dto.UpdateDialogQuantityResponse
import com.silkysys.warehouse.data.picking.startPickUpdateDialogQuantity.dto.UpdateQuantityRequest
import com.silkysys.warehouse.sharedcode.data.remote.ApiService
import com.silkysys.warehouse.sharedcode.Utility.parseBodyOrDefault
import com.silkysys.warehouse.sharedcode.domain.picking.startPickUpdateDialogQuantity.UpdateDialogQuantityRepo
import com.silkysys.warehouse.sharedcode.domain.picking.startPickUpdateDialogQuantity.entity.UpdateDialogQuantityEntity
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


/**
 *
 * Created by: Mahmoud Ibrahim on 4/16/2023
 * Email: mahmoudibrahiem57@gmail.com
 *
 **/
class UpdateDialogQuantityRepoImpl  (private val apiService: ApiService) :
    UpdateDialogQuantityRepo {
    override suspend fun updateDialogQuantity(
        companyId: Int,
        fulfilmentId: Int,
        updateQuantityRequest: UpdateQuantityRequest
    ): Flow<BaseResult<UpdateDialogQuantityEntity, WrappedResponse<UpdateDialogQuantityResponse>>> {
        return flow {
            val response = apiService.updateQuantityFromDialog(fulfilmentId, updateQuantityRequest)
            response.parseBodyOrDefault<WrappedResponse<UpdateDialogQuantityResponse>>()
                .let { parsed ->
                    if (response.status.isSuccess()) {
                        emit(BaseResult.Success(UpdateDialogQuantityEntity(parsed.message)))
                    } else {
                        emit(BaseResult.Error(parsed))
                    }
                }
        }
    }
}

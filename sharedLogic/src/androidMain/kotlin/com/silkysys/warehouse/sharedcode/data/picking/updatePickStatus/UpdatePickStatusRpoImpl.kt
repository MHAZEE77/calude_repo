package com.silkysys.warehouse.sharedcode.data.picking.updatePickStatus

import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.data.picking.updatePickStatus.dto.UpdatePickStatusRequest
import com.silkysys.warehouse.sharedcode.data.picking.updatePickStatus.dto.UpdatePickStatusResponse
import com.silkysys.warehouse.sharedcode.data.remote.ApiService
import com.silkysys.warehouse.sharedcode.Utility.parseBodyOrDefault
import com.silkysys.warehouse.sharedcode.domain.picking.updatePickStatus.UpdatePickStatusRepo
import com.silkysys.warehouse.sharedcode.domain.picking.updatePickStatus.entity.UpdatePickStatusEntity
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Created by: Mahmoud Ibrahim on 3/22/2023
 * Email: mahmoudibrahiem57@gmail.com
 **/
class UpdatePickStatusRpoImpl  (private val apiService: ApiService) :
    UpdatePickStatusRepo {
    override suspend fun updatePickStatus(
        companyId: Int,
        updatePickStatusRequest: UpdatePickStatusRequest
    ): Flow<BaseResult<UpdatePickStatusEntity, WrappedResponse<UpdatePickStatusResponse>>> {
        return flow {
            val response = apiService.updatePickStatus(updatePickStatusRequest)
            response.parseBodyOrDefault<WrappedResponse<UpdatePickStatusResponse>>().let { parsed ->
                emit(
                    if (response.status.isSuccess()) {
                        BaseResult.Success(UpdatePickStatusEntity(parsed.message))
                    } else {
                        BaseResult.Error(parsed)
                    }
                )
            }
        }
    }
}

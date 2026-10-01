package com.silkysys.warehouse.sharedcode.data.packing.addPack

import com.silkysys.warehouse.sharedcode.data.remote.ApiService
import com.silkysys.warehouse.sharedcode.domain.packing.addPackItem.AddPackRepo
import com.silkysys.warehouse.sharedcode.domain.packing.addPackItem.entity.AddPackEntity
import com.silkysys.warehouse.sharedcode.Utility.parseBodyOrDefault
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.data.packing.addPack.dto.AddPackRequest
import com.silkysys.warehouse.sharedcode.data.packing.addPack.dto.AddPackResponse
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
/**
 *
 * Created by: Mahmoud Ibrahim on 5/11/2023
 * Email: mahmoudibrahiem57@gmail.com
 *
 **/
class AddPackRepoImpl  (private val apiService: ApiService) : AddPackRepo {
    override suspend fun addPackItem(
        companyId: Int,
        fulfillmentId: Int,
        addPackRequest: AddPackRequest
    ):
            Flow<BaseResult<AddPackEntity, WrappedResponse<AddPackResponse>>> {
        return flow {
            val response = apiService.addPackItem(fulfillmentId, addPackRequest)
            response.parseBodyOrDefault<WrappedResponse<AddPackResponse>>().let { parsed ->
                if (response.status.isSuccess()) {
                    emit(BaseResult.Success(AddPackEntity(parsed.message)))
                } else {
                    emit(BaseResult.Error(parsed))
                }
            }
        }
    }
}

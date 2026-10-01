package com.silkysys.warehouse.sharedcode.data.putAwayModule.deletePinLocation

import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.data.putAwayModule.deletePinLocation.dto.DeleteLocationRequest
import com.silkysys.warehouse.sharedcode.data.putAwayModule.deletePinLocation.dto.DeleteLocationResponse
import com.silkysys.warehouse.sharedcode.data.remote.ApiService
import com.silkysys.warehouse.sharedcode.Utility.parseBodyOrDefault
import com.silkysys.warehouse.sharedcode.domain.putAwayModule.deletePinLocation.DeleteLocationRepo
import com.silkysys.warehouse.sharedcode.domain.putAwayModule.deletePinLocation.entity.DeleteLocationEntity
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 *
 * Created by: Mahmoud Ibrahim on 4/18/2023
 * Email: mahmoudibrahiem57@gmail.com
 *
 **/
class DeleteLocationRepoImpl  (private val apiService: ApiService) :
    DeleteLocationRepo {
    override suspend fun deleteLocation(
        companyId: Int,
        deleteLocationRequest: DeleteLocationRequest
    ): Flow<BaseResult<DeleteLocationEntity, WrappedResponse<DeleteLocationResponse>>> {
        return flow {
            val response = apiService.deleteLocation(companyId.toString(), deleteLocationRequest)

            response.parseBodyOrDefault<WrappedResponse<DeleteLocationResponse>>().let { parsed ->
                val binLocations = parsed.data?.item?.binLocation?.map {
                    DeleteLocationEntity.BinLocations(
                        it.id, it.company_id,
                        it.warehouse_id, it.code
                    )
                } ?: emptyList()
                emit(
                    if (response.status.isSuccess()) {
                        BaseResult.Success(DeleteLocationEntity(ArrayList(binLocations)))
                    } else {
                        BaseResult.Error(parsed)
                    }
                )
            }
        }
    }
}

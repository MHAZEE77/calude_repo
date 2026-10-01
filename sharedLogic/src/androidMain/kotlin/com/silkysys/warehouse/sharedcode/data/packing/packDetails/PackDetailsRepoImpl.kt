package com.silkysys.warehouse.sharedcode.data.packing.packDetails

import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.data.packing.packDetails.dto.PackDetailsResponse
import com.silkysys.warehouse.sharedcode.data.remote.ApiService
import com.silkysys.warehouse.sharedcode.Utility.parseBodyOrDefault
import com.silkysys.warehouse.sharedcode.domain.packing.packDetails.PackDetailsRepo
import com.silkysys.warehouse.sharedcode.domain.packing.packDetails.entity.PackDetailsEntity
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 *
 * Created by: Mahmoud Ibrahim on 5/4/2023
 * Email: mahmoudibrahiem57@gmail.com
 *
 **/
class PackDetailsRepoImpl (private val apiService: ApiService) :
    PackDetailsRepo {
    override suspend fun getPackDetails(
        companyId: Int,
        fulfillmentId: Int
    ): Flow<BaseResult<PackDetailsEntity, WrappedResponse<PackDetailsResponse>>> {
        return flow {
            val response = apiService.getPackDetails(fulfillmentId)
            response.parseBodyOrDefault<WrappedResponse<PackDetailsResponse>>().let { parsed ->
                val data = parsed.data
                if (response.status.isSuccess() && data != null) {
                    val detailsList = data.packListItems.map {
                        PackDetailsEntity.PackList(
                            it.id, it.height, it.width, it.length, it.weight,
                            it.isFragile, it.quantity, it.name, it.uom
                        )
                    }?.toCollection(ArrayList()) ?: ArrayList()
                    emit(
                        BaseResult.Success(
                            PackDetailsEntity(
                                detailsList, data.canPack
                            )
                        )
                    )
                } else {
                    emit(BaseResult.Error(parsed))
                }
            }
        }
    }
}

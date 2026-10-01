package com.silkysys.warehouse.sharedcode.data.packing.packItems

import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.data.packing.packItems.dto.PackItemsResponse
import com.silkysys.warehouse.sharedcode.data.remote.ApiService
import com.silkysys.warehouse.sharedcode.Utility.parseBodyOrDefault
import com.silkysys.warehouse.sharedcode.domain.packing.packItems.PackItemsRepo
import com.silkysys.warehouse.sharedcode.domain.packing.packItems.entity.PackItemsEntity
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 *
 * Created by: Mahmoud Ibrahim on 5/8/2023
 * Email: mahmoudibrahiem57@gmail.com
 *
 **/
class PackItemsRepoImpl  (private val apiService: ApiService) : PackItemsRepo {
    override suspend fun getPackItems(
        companyId: Int,
        fulfillmentId: Int
    ): Flow<BaseResult<PackItemsEntity, WrappedResponse<PackItemsResponse>>> {
        return flow {
            val response = apiService.getPackItems(fulfillmentId)
            val parsed =
                response.parseBodyOrDefault<WrappedResponse<PackItemsResponse>>()
            if (response.status.isSuccess()) {
                val packItems = parsed.data?.packItems
                    ?.map {
                        PackItemsEntity.PackItems(
                            id = it.id,
                            name = it.name,
                            allowedQuantity = it.allowedQuantity
                        )
                    }
                    ?: emptyList()

                emit(BaseResult.Success(PackItemsEntity(ArrayList(packItems))))
            } else {
                emit(BaseResult.Error(parsed))
            }
        }
    }
}

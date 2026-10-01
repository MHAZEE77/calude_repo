package com.silkysys.warehouse.sharedcode.data.packing.packList

import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.data.packing.packList.dto.PackResponse
import com.silkysys.warehouse.sharedcode.data.remote.ApiService
import com.silkysys.warehouse.sharedcode.Utility.parseBodyOrDefault
import com.silkysys.warehouse.sharedcode.domain.packing.packList.PackListRepo
import com.silkysys.warehouse.sharedcode.domain.packing.packList.entity.PackListEntity
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 *
 * Created by: Mahmoud Ibrahim on 5/1/2023
 * Email: mahmoudibrahiem57@gmail.com
 *
 **/
class PackListRepoImpl  (private val apiService: ApiService) : PackListRepo {
    override suspend fun getPackList(
        companyId: Int,
        status: String
    ): Flow<BaseResult<PackListEntity, WrappedResponse<PackResponse>>> {
        return flow {
            val response = apiService.getPackList(status)

            val parsed =
                response.parseBodyOrDefault<WrappedResponse<PackResponse>>()

            if (response.status.isSuccess()) {
                val fulfillments = parsed.data?.fulfillments
                    ?.map { fulfillment ->
                        PackListEntity.Fulfillments(
                            id = fulfillment.id,
                            name = fulfillment.name,
                            status = fulfillment.status,
                            created_at = fulfillment.created_at,
                            document = PackListEntity.Fulfillments.Document(fulfillment.document.document_number),
                            warehouse = PackListEntity.Fulfillments.Warehouse(fulfillment.warehouse.name),
                        )
                    } ?: emptyList()

                emit(BaseResult.Success(PackListEntity(ArrayList(fulfillments))))
            } else {
                emit(BaseResult.Error(parsed))
            }
        }
    }
}

package com.silkysys.warehouse.sharedcode.data.startedItems

import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.data.remote.ApiService
import com.silkysys.warehouse.sharedcode.Utility.parseBodyOrDefault
import com.silkysys.warehouse.sharedcode.data.startedItems.dto.StartedItemsResponse
import com.silkysys.warehouse.sharedcode.domain.picking.startPick.entity.StartPickEntity
import com.silkysys.warehouse.sharedcode.domain.picking.startedItems.StartedItemsRepo
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Created by: Mahmoud Ibrahim on 3/25/2023
 * Email: mahmoudibrahiem57@gmail.com
 **/
class StartedItemsRepoImpl  (private val apiService: ApiService) :
    StartedItemsRepo {
    override suspend fun getStartedList(
        companyId: Int, fulfillmentId: Int
    ):
            Flow<BaseResult<StartPickEntity, WrappedResponse<StartedItemsResponse>>> {
        return flow {
            val response = apiService.getStartedList(fulfillmentId)

            response.parseBodyOrDefault<WrappedResponse<StartedItemsResponse>>().let { parsed ->
                val startedList = parsed.data?.fulfillment?.items?.map { item ->
                    StartPickEntity.Fulfillment.Item(
                        item.description,
                        item.id,
                        item.name,
                        item.pickedQuantity,
                        item.quantity,
                        item.image,
                        item.status,
                        item.barcode
                    )
                } ?: emptyList()
                emit(
                    if (response.status.isSuccess()) {
                        BaseResult.Success(
                            StartPickEntity(
                                StartPickEntity.Fulfillment(
                                    ArrayList(
                                        startedList
                                    )
                                )
                            )
                        )
                    } else {
                        BaseResult.Error(parsed)
                    }
                )
            }
        }
    }
}

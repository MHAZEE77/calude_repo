package com.silkysys.warehouse.sharedcode.data.picking.startPick

import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.data.picking.startPick.dto.StartPickResponse
import com.silkysys.warehouse.sharedcode.data.remote.ApiService
import com.silkysys.warehouse.sharedcode.Utility.parseBodyOrDefault
import com.silkysys.warehouse.sharedcode.domain.picking.startPick.StartPickRepo
import com.silkysys.warehouse.sharedcode.domain.picking.startPick.entity.StartPickEntity
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Created by: Mahmoud Ibrahim on 3/23/2023
 * Email: mahmoudibrahiem57@gmail.com
 **/
class StartPickRepoImpl  (private val apiService: ApiService) : StartPickRepo {
    override suspend fun startPick(
        companyId: Int,
        fulfillmentId: Int
    ): Flow<BaseResult<StartPickEntity, WrappedResponse<StartPickResponse>>> {
        return flow {
            val response = apiService.startPick(fulfillmentId)

            response.parseBodyOrDefault<WrappedResponse<StartPickResponse>>().let { parsed ->

                val items = parsed.data?.fulfillment?.items?.map {
                    StartPickEntity.Fulfillment.Item(
                        it.description, it.id,
                        it.name, it.pickedQuantity, it.quantity, it.image, it.status, it.barcode
                    )
                } ?: emptyList()

                if (response.status.isSuccess()) {
                    emit(
                        BaseResult.Success(
                            StartPickEntity(
                                StartPickEntity.Fulfillment(
                                    ArrayList(
                                        items
                                    )
                                )
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

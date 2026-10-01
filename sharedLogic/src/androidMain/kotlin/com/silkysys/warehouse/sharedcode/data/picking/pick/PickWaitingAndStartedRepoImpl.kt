package com.silkysys.warehouse.sharedcode.data.picking.pick

import android.annotation.SuppressLint
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.data.picking.pick.dto.pickWaitingAndStartedResponse.PickWaitingAndStartedResponse
import com.silkysys.warehouse.sharedcode.data.remote.ApiService
import com.silkysys.warehouse.sharedcode.Utility.parseBodyOrDefault
import com.silkysys.warehouse.sharedcode.domain.picking.pick.PickWaitingAndStartedRepo
import com.silkysys.warehouse.sharedcode.domain.picking.pick.entity.PickEntity
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Created by: Mahmoud Ibrahim on 3/16/2023
 * Email: mahmoudibrahiem57@gmail.com
 **/
class PickWaitingAndStartedRepoImpl(private val apiService: ApiService) :
    PickWaitingAndStartedRepo {
    @SuppressLint("SuspiciousIndentation")
    override suspend fun getPickWaitingAndStarted(companyId: Int): Flow<BaseResult<PickEntity, WrappedResponse<PickWaitingAndStartedResponse>>> {
        return flow {
            val response = apiService.getPickWaitingAndStarted()

            response.parseBodyOrDefault<WrappedResponse<PickWaitingAndStartedResponse>>()
                .let { parsed ->

                    emit(
                        if (response.status.isSuccess()) {
                            val waitingList =
                                parsed.data?.fulfillments?.waiting?.map { waitingItem ->
                                    PickEntity.Fulfillments.Waiting(
                                        waitingItem.createdAt,
                                        waitingItem.id,
                                        waitingItem.name,
                                        waitingItem.status,
                                        waitingItem.totalItems,
                                        waitingItem.totalItemsPickedQuantity,
                                        waitingItem.totalItemsQuantity,
                                    )
                                } ?: emptyList()

                            val startedList =
                                parsed.data?.fulfillments?.started?.map { startedItem ->
                                    PickEntity.Fulfillments.Started(
                                        startedItem.createdAt,
                                        startedItem.id,
                                        startedItem.name,
                                        startedItem.status,
                                        startedItem.totalItems,
                                        startedItem.totalItemsPickedQuantity,
                                        startedItem.totalItemsQuantity,
                                    )
                                } ?: emptyList()

                            BaseResult.Success(
                                PickEntity(
                                    PickEntity.Fulfillments(waitingList, startedList)
                                )

                            )
                        } else {
                            BaseResult.Error(parsed)
                        })
                }
        }
    }
}

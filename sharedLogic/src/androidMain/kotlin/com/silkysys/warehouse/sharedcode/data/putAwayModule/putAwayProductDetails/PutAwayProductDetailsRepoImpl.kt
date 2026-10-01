package com.silkysys.warehouse.sharedcode.data.putAwayModule.putAwayProductDetails

import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.data.putAwayModule.putAwayProductDetails.dto.PutAwayProductDetailsRequest
import com.silkysys.warehouse.sharedcode.data.putAwayModule.putAwayProductDetails.dto.PutAwayProductDetailsResponse
import com.silkysys.warehouse.sharedcode.data.remote.ApiService
import com.silkysys.warehouse.sharedcode.Utility.parseBodyOrDefault
import com.silkysys.warehouse.sharedcode.domain.putAwayModule.putAway.entity.PutAwayScanEntity
import com.silkysys.warehouse.sharedcode.domain.putAwayModule.putAwayProductDetails.PutAwayProductDetailsRepo
import com.silkysys.warehouse.sharedcode.domain.putAwayModule.putAwayProductDetails.entity.PutAwayProductDetailsEntity
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Created by: Mahmoud Ibrahim on 3/21/2023
 * Email: mahmoudibrahiem57@gmail.com
 **/

class PutAwayProductDetailsRepoImpl  (
    private val apiService: ApiService
) : PutAwayProductDetailsRepo {

    override suspend fun assignProductToBinLocation(
        companyId: Int,
        putAwayProductDetailsRequest: PutAwayProductDetailsRequest
    ): Flow<BaseResult<PutAwayProductDetailsEntity, WrappedResponse<PutAwayProductDetailsResponse>>> {
        return flow {
            val response = apiService.assignProductToBinLocation(putAwayProductDetailsRequest)

            response.parseBodyOrDefault<WrappedResponse<PutAwayProductDetailsResponse>>()
                .let { parsed ->
                    val binLocation = parsed.data?.item?.binLocations?.map { item ->
                        PutAwayScanEntity.Item.BinLocations(
                            item.id, item.company_id, item.warehouse_id, item.code
                        )
                    } ?: emptyList()
                    if (response.status.isSuccess()) {
                        emit(BaseResult.Success(PutAwayProductDetailsEntity(ArrayList(binLocation))))
                    } else {
                        emit(BaseResult.Error(parsed))
                    }
                }
        }
    }
}

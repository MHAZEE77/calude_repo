package com.silkysys.warehouse.sharedcode.domain.packing.packDetails.useCase

import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.data.packing.packDetails.dto.PackDetailsResponse
import com.silkysys.warehouse.sharedcode.domain.packing.packDetails.PackDetailsRepo
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.domain.packing.packDetails.entity.PackDetailsEntity
import kotlinx.coroutines.flow.Flow

/**
 *
 * Created by: Mahmoud Ibrahim on 5/4/2023
 * Email: mahmoudibrahiem57@gmail.com
 *
 **/
class PackDetailsUseCase (private val packDetailsRepo: PackDetailsRepo) {
    suspend fun getPAckDetails(companyId: Int , fulfillmentId: Int):
            Flow<BaseResult<PackDetailsEntity, WrappedResponse<PackDetailsResponse>>>{
        return packDetailsRepo.getPackDetails(companyId,fulfillmentId)
    }
}

package com.silkysys.warehouse.sharedcode.domain.packing.addPackItem.useCase

import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.data.packing.addPack.dto.AddPackRequest
import com.silkysys.warehouse.sharedcode.data.packing.addPack.dto.AddPackResponse
import com.silkysys.warehouse.sharedcode.domain.packing.addPackItem.AddPackRepo
import com.silkysys.warehouse.sharedcode.domain.packing.addPackItem.entity.AddPackEntity
import kotlinx.coroutines.flow.Flow

/**
 *
 * Created by: Mahmoud Ibrahim on 5/11/2023
 * Email: mahmoudibrahiem57@gmail.com
 *
 **/
class AddPackUseCase  (private val addPackRepo: AddPackRepo){
    suspend fun addPackItem(companyId: Int, fulfillmentId: Int,addPackRequest: AddPackRequest) :
    Flow<BaseResult<AddPackEntity, WrappedResponse<AddPackResponse>>>{
        return addPackRepo.addPackItem(companyId,fulfillmentId,addPackRequest)
    }
}

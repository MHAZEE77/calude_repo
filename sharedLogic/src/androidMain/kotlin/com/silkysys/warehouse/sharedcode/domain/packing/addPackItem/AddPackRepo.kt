package com.silkysys.warehouse.sharedcode.domain.packing.addPackItem

import com.silkysys.warehouse.sharedcode.domain.packing.addPackItem.entity.AddPackEntity
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.data.packing.addPack.dto.AddPackRequest
import com.silkysys.warehouse.sharedcode.data.packing.addPack.dto.AddPackResponse
import kotlinx.coroutines.flow.Flow

/**
 *
 * Created by: Mahmoud Ibrahim on 5/11/2023
 * Email: mahmoudibrahiem57@gmail.com
 *
 **/
interface AddPackRepo {
    suspend fun addPackItem(companyId: Int, fulfillmentId: Int,addPackRequest: AddPackRequest):
            Flow<BaseResult<AddPackEntity, WrappedResponse<AddPackResponse>>>
}

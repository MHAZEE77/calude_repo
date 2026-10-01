package com.silkysys.warehouse.sharedcode.domain.picking.updatePickStatus

import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.data.picking.updatePickStatus.dto.UpdatePickStatusRequest
import com.silkysys.warehouse.sharedcode.data.picking.updatePickStatus.dto.UpdatePickStatusResponse
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.domain.picking.updatePickStatus.entity.UpdatePickStatusEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by: Mahmoud Ibrahim on 3/22/2023
 * Email: mahmoudibrahiem57@gmail.com
 **/
interface UpdatePickStatusRepo {
    suspend fun updatePickStatus(companyId: Int, updatePickStatusRequest: UpdatePickStatusRequest):
            Flow<BaseResult<UpdatePickStatusEntity, WrappedResponse<UpdatePickStatusResponse>>>
}

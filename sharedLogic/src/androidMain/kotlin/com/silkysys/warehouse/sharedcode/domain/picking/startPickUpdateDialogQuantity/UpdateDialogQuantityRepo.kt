package com.silkysys.warehouse.sharedcode.domain.picking.startPickUpdateDialogQuantity

import com.silkysys.warehouse.data.picking.startPickUpdateDialogQuantity.dto.UpdateDialogQuantityResponse
import com.silkysys.warehouse.data.picking.startPickUpdateDialogQuantity.dto.UpdateQuantityRequest
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.domain.picking.startPickUpdateDialogQuantity.entity.UpdateDialogQuantityEntity
import kotlinx.coroutines.flow.Flow

/**
 *
 * Created by: Mahmoud Ibrahim on 4/16/2023
 * Email: mahmoudibrahiem57@gmail.com
 *
 **/
interface UpdateDialogQuantityRepo {
    suspend fun updateDialogQuantity(companyId: Int,fulfilmentId: Int,updateQuantityRequest: UpdateQuantityRequest):
            Flow<BaseResult<UpdateDialogQuantityEntity, WrappedResponse<UpdateDialogQuantityResponse>>>
}

package com.silkysys.warehouse.sharedcode.domain.picking.startPickUpdateDialogQuantity.useCase

import com.silkysys.warehouse.data.picking.startPickUpdateDialogQuantity.dto.UpdateDialogQuantityResponse
import com.silkysys.warehouse.data.picking.startPickUpdateDialogQuantity.dto.UpdateQuantityRequest
import com.silkysys.warehouse.sharedcode.domain.picking.startPickUpdateDialogQuantity.UpdateDialogQuantityRepo
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
class UpdateDialogQuantityUseCase  (private val updateDialogQuantityRepo: UpdateDialogQuantityRepo) {
    suspend fun updateDialogQuantity(companyId: Int,fulfllmentId: Int,
                               updateQuantityRequest: UpdateQuantityRequest
    ):
            Flow<BaseResult<UpdateDialogQuantityEntity, WrappedResponse<UpdateDialogQuantityResponse>>> {
        return updateDialogQuantityRepo.updateDialogQuantity(companyId,fulfllmentId,updateQuantityRequest)
    }
}

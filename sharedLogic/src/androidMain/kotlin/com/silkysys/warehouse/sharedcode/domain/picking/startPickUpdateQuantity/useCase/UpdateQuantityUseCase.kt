package com.silkysys.warehouse.sharedcode.domain.picking.startPickUpdateQuantity.useCase

import com.silkysys.warehouse.data.picking.startPickUpdateQuantity.dto.UpdateQuantityResponse
import com.silkysys.warehouse.sharedcode.domain.picking.startPickUpdateQuantity.UpdateQuantityRepo
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.domain.picking.startPickUpdateQuantity.entity.UpdateQuantityEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by: Mahmoud Ibrahim on 3/27/2023
 * Email: mahmoudibrahiem57@gmail.com
 **/
class UpdateQuantityUseCase  (private val updateQuantityRepo: UpdateQuantityRepo) {
    suspend fun updateQuantity(companyId: Int,fulfllmentId: Int,
                               itemCode: String):
            Flow<BaseResult<UpdateQuantityEntity, WrappedResponse<UpdateQuantityResponse>>>{
        return updateQuantityRepo.updateQuantity(companyId,fulfllmentId,itemCode)
    }
}

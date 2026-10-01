package com.silkysys.warehouse.sharedcode.domain.putAwayModule.deletePinLocation.useCase

import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.data.putAwayModule.deletePinLocation.dto.DeleteLocationRequest
import com.silkysys.warehouse.sharedcode.data.putAwayModule.deletePinLocation.dto.DeleteLocationResponse
import com.silkysys.warehouse.sharedcode.domain.putAwayModule.deletePinLocation.DeleteLocationRepo
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.domain.putAwayModule.deletePinLocation.entity.DeleteLocationEntity
import kotlinx.coroutines.flow.Flow

/**
 *
 * Created by: Mahmoud Ibrahim on 4/18/2023
 * Email: mahmoudibrahiem57@gmail.com
 *
 **/
class DeleteLocationUseCase  (private val deleteLocationRepo: DeleteLocationRepo) {
    suspend fun deleteLocation(companyId: Int, deleteLocationRequest: DeleteLocationRequest)
    : Flow<BaseResult<DeleteLocationEntity, WrappedResponse<DeleteLocationResponse>>>{
        return deleteLocationRepo.deleteLocation(companyId,deleteLocationRequest)
    }
}

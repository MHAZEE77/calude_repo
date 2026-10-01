package com.silkysys.warehouse.sharedcode.domain.autentcation.updateForgetPassword.useCase

import com.silkysys.warehouse.sharedcode.data.autentcation.updateForgetPassword.dto.UpdateForgetPasswordRequest
import com.silkysys.warehouse.sharedcode.data.autentcation.updateForgetPassword.dto.UpdateForgetPasswordResponse
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.domain.autentcation.updateForgetPassword.UpdateForgetPasswordRepo
import com.silkysys.warehouse.sharedcode.domain.autentcation.updateForgetPassword.entity.UpdateForgetPasswordEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by: Mahmoud Ibrahim on 3/6/2023
 * Email: mahmoudibrahiem57@gmail.com
 **/
class UpdateForgetPasswordUseCase  (private val updateForgetPasswordRepo: UpdateForgetPasswordRepo){

    suspend fun updateForgetPassword(updateForgetPasswordRequest: UpdateForgetPasswordRequest)
    : Flow<BaseResult<UpdateForgetPasswordEntity, WrappedResponse<UpdateForgetPasswordResponse>>> {
        return updateForgetPasswordRepo.updateForgetPassword(updateForgetPasswordRequest)
    }
}

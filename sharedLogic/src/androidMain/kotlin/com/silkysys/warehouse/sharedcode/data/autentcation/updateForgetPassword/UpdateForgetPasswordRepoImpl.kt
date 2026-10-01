package com.silkysys.warehouse.sharedcode.data.autentcation.updateForgetPassword

import com.silkysys.warehouse.sharedcode.data.autentcation.updateForgetPassword.dto.UpdateForgetPasswordRequest
import com.silkysys.warehouse.sharedcode.data.autentcation.updateForgetPassword.dto.UpdateForgetPasswordResponse
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.data.remote.ApiService
import com.silkysys.warehouse.sharedcode.domain.autentcation.updateForgetPassword.UpdateForgetPasswordRepo
import com.silkysys.warehouse.sharedcode.domain.autentcation.updateForgetPassword.entity.UpdateForgetPasswordEntity
import com.silkysys.warehouse.sharedcode.Utility.parseBodyOrDefault
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Created by: Mahmoud Ibrahim on 3/6/2023
 * Email: mahmoudibrahiem57@gmail.com
 **/
class UpdateForgetPasswordRepoImpl  (private val apiService: ApiService) :
    UpdateForgetPasswordRepo {
    override suspend fun updateForgetPassword(updateForgetPasswordRequest: UpdateForgetPasswordRequest)
            : Flow<BaseResult<UpdateForgetPasswordEntity, WrappedResponse<UpdateForgetPasswordResponse>>> {
        return flow {
            val response = apiService.updateForgetPassword(updateForgetPasswordRequest)

            response.parseBodyOrDefault<WrappedResponse<UpdateForgetPasswordResponse>>().let { parsed ->
                if (response.status.isSuccess()) {
                    emit(BaseResult.Success(UpdateForgetPasswordEntity(parsed.message)))
                } else {
                    emit(BaseResult.Error(parsed))
                }
            }
        }
    }
}

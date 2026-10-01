package com.silkysys.warehouse.sharedcode.data.autentcation.forgetPasswordVerifyCode

import com.silkysys.warehouse.sharedcode.data.autentcation.forgetPasswordVerifyCode.dto.VerifyCodeRequest
import com.silkysys.warehouse.sharedcode.data.autentcation.forgetPasswordVerifyCode.dto.VerifyCodeResponse
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.data.remote.ApiService
import com.silkysys.warehouse.sharedcode.domain.autentcation.forgetPasswordVerifyCode.VerifyCodeRepo
import com.silkysys.warehouse.sharedcode.domain.autentcation.forgetPasswordVerifyCode.entity.VerifyCodeEntity
import com.silkysys.warehouse.sharedcode.Utility.parseBodyOrDefault
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.silkysys.warehouse.sharedcode.data.base.*

class SendVerifyCodeRepoImpl  (private val apiService: ApiService) :
    VerifyCodeRepo {
    override suspend fun verifyCode(verifyCodeRequest: VerifyCodeRequest): Flow<BaseResult<VerifyCodeEntity, WrappedResponse<VerifyCodeResponse>>> {
        return flow {
            val response = apiService.verifyEmail(verifyCodeRequest)
            val parsed =
                response.parseBodyOrDefault<WrappedResponse<VerifyCodeResponse>> ()
            if (response.status.isSuccess()) {
                val token = parsed.data?.resetToken.orEmpty()
                emit(BaseResult.Success(VerifyCodeEntity(parsed.message, token)))
            } else {
                emit(BaseResult.Error(parsed))
            }
        }
    }
}

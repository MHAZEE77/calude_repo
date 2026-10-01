package com.silkysys.warehouse.sharedcode.domain.autentcation.forgetPasswordVerifyCode.useCase

import com.silkysys.warehouse.sharedcode.data.autentcation.forgetPasswordVerifyCode.dto.VerifyCodeRequest
import com.silkysys.warehouse.sharedcode.data.autentcation.forgetPasswordVerifyCode.dto.VerifyCodeResponse
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.domain.autentcation.forgetPasswordVerifyCode.VerifyCodeRepo
import com.silkysys.warehouse.sharedcode.domain.autentcation.forgetPasswordVerifyCode.entity.VerifyCodeEntity
import kotlinx.coroutines.flow.Flow


class VerifyCodeUseCase  (private val verifyCodeRepo: VerifyCodeRepo){
    suspend fun verifyCode(verifyCodeRequest: VerifyCodeRequest):
            Flow<BaseResult<VerifyCodeEntity,WrappedResponse<VerifyCodeResponse>>>{
        return verifyCodeRepo.verifyCode(verifyCodeRequest)
    }

}

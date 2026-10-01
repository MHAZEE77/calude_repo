package com.silkysys.warehouse.sharedcode.domain.autentcation.forgetPasswordVerifyCode

import com.silkysys.warehouse.sharedcode.data.autentcation.forgetPasswordVerifyCode.dto.VerifyCodeRequest
import com.silkysys.warehouse.sharedcode.data.autentcation.forgetPasswordVerifyCode.dto.VerifyCodeResponse
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.domain.autentcation.forgetPasswordVerifyCode.entity.VerifyCodeEntity
import kotlinx.coroutines.flow.Flow


interface VerifyCodeRepo {
    suspend fun verifyCode(verifyCodeRequest: VerifyCodeRequest):
            Flow<BaseResult<VerifyCodeEntity, WrappedResponse<VerifyCodeResponse>>>
}

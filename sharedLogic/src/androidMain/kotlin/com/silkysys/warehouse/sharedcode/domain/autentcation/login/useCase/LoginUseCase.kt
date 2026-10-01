package com.silkysys.warehouse.sharedcode.domain.autentcation.login.useCase

import com.silkysys.warehouse.sharedcode.data.autentcation.login.dto.request.LoginRequest
import com.silkysys.warehouse.sharedcode.data.autentcation.login.dto.response.LoginResponse
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.domain.autentcation.login.LoginRepo
import com.silkysys.warehouse.sharedcode.domain.autentcation.login.entity.LoginEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by: Mahmoud Ibrahim on 3/4/2023
 * Email: mahmoudibrahiem57@gmail.com
 **/
class LoginUseCase(private val loginRepo: LoginRepo) {
    suspend fun loginUseCase(loginRequest: LoginRequest): Flow<BaseResult<LoginEntity, WrappedResponse<LoginResponse>>> {
        return loginRepo.login(loginRequest)
    }
}

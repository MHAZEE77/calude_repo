package com.silkysys.warehouse.sharedcode.domain.autentcation.login

import com.silkysys.warehouse.sharedcode.data.autentcation.login.dto.request.LoginRequest
import com.silkysys.warehouse.sharedcode.data.autentcation.login.dto.response.LoginResponse
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.domain.autentcation.login.entity.LoginEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by: Mahmoud Ibrahim on 3/4/2023
 * Email: mahmoudibrahiem57@gmail.com
 **/
interface LoginRepo {
     suspend fun login(loginRequest: LoginRequest): Flow<BaseResult<LoginEntity, WrappedResponse<LoginResponse>>>
}

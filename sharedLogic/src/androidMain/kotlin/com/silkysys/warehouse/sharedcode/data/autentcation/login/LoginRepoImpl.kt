package com.silkysys.warehouse.sharedcode.data.autentcation.login

import com.silkysys.warehouse.sharedcode.data.remote.ApiService
import com.silkysys.warehouse.domain.autentcation.login.entity.Companies
import com.silkysys.warehouse.sharedcode.Utility.parseBodyOrDefault
import com.silkysys.warehouse.sharedcode.data.autentcation.login.dto.request.LoginRequest
import com.silkysys.warehouse.sharedcode.data.autentcation.login.dto.response.LoginResponse
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.domain.autentcation.login.LoginRepo
import com.silkysys.warehouse.sharedcode.domain.autentcation.login.entity.LoginEntity
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LoginRepoImpl(private val apiService: ApiService) : LoginRepo {
    override suspend fun login(
        loginRequest: LoginRequest,
    ): Flow<BaseResult<LoginEntity, WrappedResponse<LoginResponse>>> = flow {
        val response = apiService.login(loginRequest)
        val parsed = response.parseBodyOrDefault<WrappedResponse<LoginResponse>>()

        if (!response.status.isSuccess()) {
            emit(BaseResult.Error(parsed))
            return@flow
        }

        val data = parsed.data
        if (data == null || data.accessToken.isBlank() || data.refreshToken!!.isBlank() || data.clientId!!.isBlank()) {
            emit(
                BaseResult.Error(
                    WrappedResponse(
                        code = response.status.value,
                        message = "The server returned an incomplete sign-in response.",
                        status = false,
                        error = "invalid_login_response",
                    ),
                ),
            )
            return@flow
        }

        emit(
            BaseResult.Success(
                LoginEntity(
                    data.id,
                    data.name!!,
                    data.email!!,
                    data.accessToken,
                    Companies(data.tenantId),
                    data.refreshToken,
                    data.clientId,
                ),
            ),
        )
    }
}

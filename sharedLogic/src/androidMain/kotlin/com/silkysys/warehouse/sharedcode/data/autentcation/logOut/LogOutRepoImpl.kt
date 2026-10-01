package com.silkysys.warehouse.sharedcode.data.autentcation.logOut

import com.silkysys.warehouse.sharedcode.data.autentcation.logOut.dto.LogOutResponse
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.data.remote.ApiService
import com.silkysys.warehouse.sharedcode.domain.autentcation.logOut.LogOutRepo
import com.silkysys.warehouse.domain.autentcation.logOut.entity.LogOutEntity
import com.silkysys.warehouse.sharedcode.Utility.parseBodyOrDefault
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Created by: Mahmoud Ibrahim on 3/8/2023
 * Email: mahmoudibrahiem57@gmail.com
 **/
class LogOutRepoImpl  (private val apiService: ApiService) : LogOutRepo {
    override suspend fun logOut(companyId: Int): Flow<BaseResult<LogOutEntity, WrappedResponse<LogOutResponse>>> {
        return flow {
            val response = apiService.logout()

            response.parseBodyOrDefault<WrappedResponse<LogOutResponse>>().let { parsed ->
                if (response.status.isSuccess()) {
                    emit(BaseResult.Success(LogOutEntity(parsed.message)))
                } else {
                    emit(BaseResult.Error(parsed))
                }
            }
        }
    }
}

package com.silkysys.warehouse.sharedcode.data.autentcation.sendemail

import com.silkysys.warehouse.sharedcode.data.autentcation.sendemail.dto.SendEmailRequest
import com.silkysys.warehouse.sharedcode.data.autentcation.sendemail.dto.SendEmailResponse
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.domain.autentcation.sendemail.SendEmailRepo
import com.silkysys.warehouse.sharedcode.Utility.parseBodyOrDefault
import com.silkysys.warehouse.sharedcode.data.remote.ApiService
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.silkysys.warehouse.sharedcode.domain.autentcation.sendemail.entity.SendEmailEntity

/**
 * Created by: Mahmoud Ibrahim on 3/13/2023
 * Email: mahmoudibrahiem57@gmail.com
 **/
class SendEmailRepoImpl(private val apiService: ApiService) :
    SendEmailRepo {
    override suspend fun updatePassword(
        request: SendEmailRequest
    ): Flow<BaseResult<SendEmailEntity, WrappedResponse<SendEmailResponse>>> {
        return flow {
            val response = apiService.forgotPassword(request)
            response.parseBodyOrDefault<WrappedResponse<SendEmailResponse>>().let { parsed ->
                if (response.status.isSuccess()) {
                    emit(BaseResult.Success(SendEmailEntity(parsed.message)))
                } else {
                    emit(BaseResult.Error(parsed))
                }
            }
        }
    }
}

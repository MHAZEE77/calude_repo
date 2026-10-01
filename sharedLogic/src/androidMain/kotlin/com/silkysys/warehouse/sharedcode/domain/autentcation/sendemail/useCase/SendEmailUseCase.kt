package com.silkysys.warehouse.sharedcode.domain.autentcation.sendemail.useCase


import com.silkysys.warehouse.sharedcode.data.autentcation.sendemail.dto.SendEmailRequest
import com.silkysys.warehouse.sharedcode.data.autentcation.sendemail.dto.SendEmailResponse
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.domain.autentcation.sendemail.SendEmailRepo
import com.silkysys.warehouse.sharedcode.domain.autentcation.sendemail.entity.SendEmailEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by: Mahmoud Ibrahim on 3/13/2023
 * Email: mahmoudibrahiem57@gmail.com
 **/
class SendEmailUseCase  (private val updatePasswordRepo: SendEmailRepo){
    suspend fun updatePassword(request: SendEmailRequest):
            Flow<BaseResult<SendEmailEntity,WrappedResponse<SendEmailResponse>>>{
            return updatePasswordRepo.updatePassword(request)
    }
}

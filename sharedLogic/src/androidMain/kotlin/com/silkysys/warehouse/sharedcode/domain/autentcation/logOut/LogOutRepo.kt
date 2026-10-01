package com.silkysys.warehouse.sharedcode.domain.autentcation.logOut

import com.silkysys.warehouse.sharedcode.data.autentcation.logOut.dto.LogOutResponse
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.domain.autentcation.logOut.entity.LogOutEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by: Mahmoud Ibrahim on 3/8/2023
 * Email: mahmoudibrahiem57@gmail.com
 **/
interface LogOutRepo {
    suspend fun logOut(companyId: Int): Flow<BaseResult<LogOutEntity, WrappedResponse<LogOutResponse>>>
}

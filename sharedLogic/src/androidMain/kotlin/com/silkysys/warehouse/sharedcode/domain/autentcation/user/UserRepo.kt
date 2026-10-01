package com.silkysys.warehouse.sharedcode.domain.autentcation.user

import com.silkysys.warehouse.sharedcode.data.autentcation.user.dto.UserResponse
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.domain.autentcation.user.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by: Mahmoud Ibrahim on 3/7/2023
 * Email: mahmoudibrahiem57@gmail.com
 **/
interface UserRepo {
    suspend fun user(companyId: Int): Flow<BaseResult<UserEntity, WrappedResponse<UserResponse>>>
}

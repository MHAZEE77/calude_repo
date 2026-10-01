package com.silkysys.warehouse.sharedcode.data.autentcation.user

import android.annotation.SuppressLint
import com.silkysys.warehouse.sharedcode.data.autentcation.user.dto.UserResponse
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.data.remote.ApiService
import com.silkysys.warehouse.sharedcode.domain.autentcation.user.UserRepo
import com.silkysys.warehouse.sharedcode.domain.autentcation.user.entity.UserEntity
import com.silkysys.warehouse.sharedcode.Utility.parseBodyOrDefault
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Created by: Mahmoud Ibrahim on 3/7/2023
 * Email: mahmoudibrahiem57@gmail.com
 **/
class UserRepoImpl  (private val apiService: ApiService) : UserRepo {
    @SuppressLint("SuspiciousIndentation")
    override suspend fun user(companyId: Int): Flow<BaseResult<UserEntity, WrappedResponse<UserResponse>>> {
        return flow {

            val response = apiService.user()

            response.parseBodyOrDefault<WrappedResponse<UserResponse>>().let { parsed ->

                if (response.status.isSuccess()) {
                    val user = parsed.data?.user ?: run {
                        emit(BaseResult.Error(parsed))
                        return@flow
                    }
                    emit(
                        BaseResult.Success(
                            UserEntity(
                                companies = user.companies?.mapNotNull { company ->
                                    company?.let { UserEntity.Company(it.id, it.location) }
                                } ?: emptyList(),
                                email = user.email,
                                id = user.id,
                                image_url = user.imageUrl,
                                name = user.name,
                                phone = user.phone
                            )
                        ))
                } else {
                    emit(BaseResult.Error(parsed))
                }
            }
        }
    }
}

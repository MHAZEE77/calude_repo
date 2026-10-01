package com.silkysys.warehouse.app.autentcation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.silkysys.warehouse.sharedcode.data.autentcation.user.dto.UserResponse
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.domain.autentcation.user.entity.UserEntity
import com.silkysys.warehouse.sharedcode.domain.autentcation.user.useCase.UserUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class ProfileViewModel (private val userUseCase: UserUseCase): ViewModel(){
    // state for get user
    private val _userState = MutableStateFlow<UserStatus>(UserStatus.Init)
    val userStatus: StateFlow<UserStatus> get() = _userState

    private fun setGetUserLoading(loading: Boolean) {
        _userState.value = UserStatus.IsLoading(loading)
    }
    private fun showToast(exception: Throwable) {
        _userState.value = UserStatus.ShowToast(exception)
    }
    fun user(companyId: Int) {
        viewModelScope.launch {
            userUseCase.user(companyId)
                .onStart {
                    setGetUserLoading(true)
                }.catch { exception ->
                    setGetUserLoading(false)
                    showToast(exception)
                }.collect { baseResult ->
                    setGetUserLoading(false)
                    when (baseResult) {
                        //success user data returned
                        is BaseResult.Success -> _userState.value =
                            UserStatus.SuccessUser(baseResult.data)
                        //failed to get user data
                        is BaseResult.Error -> _userState.value =
                            UserStatus.ErrorUser(baseResult.rawResponse)
                    }
                }
        }
    }
}
sealed class UserStatus(){
    object Init: UserStatus()
    data class SuccessUser(val userEntity: UserEntity): UserStatus()
    data class ErrorUser(val userResponse: WrappedResponse<UserResponse>): UserStatus()
    data class IsLoading(val isLoading: Boolean): UserStatus()
    data class ShowToast(val exception: Throwable): UserStatus()
}

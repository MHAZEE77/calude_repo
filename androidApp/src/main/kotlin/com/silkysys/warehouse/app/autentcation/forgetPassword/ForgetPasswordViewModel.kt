package com.silkysys.warehouse.app.autentcation.forgetPassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.silkysys.warehouse.sharedcode.domain.autentcation.updateForgetPassword.entity.UpdateForgetPasswordEntity
import com.silkysys.warehouse.sharedcode.data.autentcation.updateForgetPassword.dto.UpdateForgetPasswordRequest
import com.silkysys.warehouse.sharedcode.data.autentcation.updateForgetPassword.dto.UpdateForgetPasswordResponse
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.domain.autentcation.updateForgetPassword.useCase.UpdateForgetPasswordUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch


class ForgetPasswordViewModel(
    private val updateForgetPasswordUseCase: UpdateForgetPasswordUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<UpdatePassword>(UpdatePassword.Init)
    val state: StateFlow<UpdatePassword> get() = _state
    private fun setLoading(loading: Boolean) {
        _state.value = UpdatePassword.SetLoading(loading)
    }

    private fun showToast(message: Throwable) {
        _state.value = UpdatePassword.ShowToast(message)
    }

    fun updatePassword(updateForgetPasswordRequest: UpdateForgetPasswordRequest) {
        viewModelScope.launch {
            setLoading(true)
            updateForgetPasswordUseCase.updateForgetPassword(updateForgetPasswordRequest).onStart {
            }.catch { exception ->
                setLoading(false)
                showToast(exception)
            }.collect { baseResult ->
                setLoading(false)
                when (baseResult) {
                    is BaseResult.Success -> _state.value =
                        UpdatePassword.SuccessUpdate(baseResult.data)

                    is BaseResult.Error -> _state.value =
                        UpdatePassword.ErrorUpdate(baseResult.rawResponse)
                }
            }
        }
    }
}

sealed class UpdatePassword {
    object Init : UpdatePassword()
    data class SetLoading(val isLoading: Boolean) : UpdatePassword()
    data class ShowToast(val message: Throwable) : UpdatePassword()
    data class SuccessUpdate(val updateForgetPasswordEntity: UpdateForgetPasswordEntity) :
        UpdatePassword()

    data class ErrorUpdate(val updateForgetPasswordResponse: WrappedResponse<UpdateForgetPasswordResponse>) :
        UpdatePassword()

}

package com.silkysys.warehouse.app.autentcation.forgetPasswordSendEmail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.silkysys.warehouse.sharedcode.data.autentcation.sendemail.dto.SendEmailRequest
import com.silkysys.warehouse.sharedcode.data.autentcation.sendemail.dto.SendEmailResponse
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.domain.autentcation.sendemail.entity.SendEmailEntity
import com.silkysys.warehouse.sharedcode.domain.autentcation.sendemail.useCase.SendEmailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch


class SendEmailViewmodel (private val sendEmailUseCase: SendEmailUseCase): ViewModel() {
    private val _updateState = MutableStateFlow<UpdatePasswordStatus>(UpdatePasswordStatus.Init)
    val updateState: StateFlow<UpdatePasswordStatus> get() = _updateState
    private fun setLoading(loading: Boolean) {
        _updateState.value = UpdatePasswordStatus.IsLoading(loading)
    }

    private fun showToast(message: Throwable) {
        _updateState.value = UpdatePasswordStatus.ShowToast(message)
    }

    fun updatePassword(request: SendEmailRequest) {
        viewModelScope.launch {
            sendEmailUseCase.updatePassword(request).onStart {
                setLoading(true)
            }.catch { exception ->
                setLoading(false)
                showToast(exception)
            }.collect { baseResult ->
                setLoading(false)
                when (baseResult) {
                    is BaseResult.Success -> _updateState.value =
                        UpdatePasswordStatus.UpdateSuccess(baseResult.data)
                    is BaseResult.Error -> _updateState.value =
                        UpdatePasswordStatus.UpdateError(baseResult.rawResponse)
                }
            }
        }
    }
}
    sealed class UpdatePasswordStatus(){
        object Init: UpdatePasswordStatus()
        data class IsLoading(val isLoading: Boolean): UpdatePasswordStatus()
        data class ShowToast(val message: Throwable): UpdatePasswordStatus()
        data class UpdateSuccess(val updatePasswordEntity: SendEmailEntity): UpdatePasswordStatus()
        data class UpdateError(val sendEmailResponse: WrappedResponse<SendEmailResponse>): UpdatePasswordStatus()
    }

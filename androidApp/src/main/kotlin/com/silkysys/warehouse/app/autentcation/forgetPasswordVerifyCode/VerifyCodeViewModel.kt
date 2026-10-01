package com.silkysys.warehouse.app.autentcation.forgetPasswordVerifyCode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.silkysys.warehouse.sharedcode.data.autentcation.forgetPasswordVerifyCode.dto.VerifyCodeRequest
import com.silkysys.warehouse.sharedcode.data.autentcation.forgetPasswordVerifyCode.dto.VerifyCodeResponse
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.domain.autentcation.forgetPasswordVerifyCode.entity.VerifyCodeEntity
import com.silkysys.warehouse.sharedcode.domain.autentcation.forgetPasswordVerifyCode.useCase.VerifyCodeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch


class VerifyCodeViewModel (private val verifyCodeUseCase: VerifyCodeUseCase):ViewModel() {

    private val _codeStatus = MutableStateFlow<CodeStatus>(CodeStatus.Init)
    val codeStatus: StateFlow<CodeStatus> get() = _codeStatus

    private fun setLoading(loading: Boolean) {
        _codeStatus.value = CodeStatus.SetLoading(loading)
    }
    private fun showToast(message: Throwable) {
        _codeStatus.value = CodeStatus.ShowToast(message)
    }
    fun verifyCode(verifyCodeRequest: VerifyCodeRequest) {
        viewModelScope.launch {
            verifyCodeUseCase.verifyCode(verifyCodeRequest).onStart {
                setLoading(true)
            }.catch { exception ->
                setLoading(false)
                showToast(exception)
            }.collect { result ->
                setLoading(false)
                when(result) {
                    is BaseResult.Success -> _codeStatus.value = CodeStatus.SuccessCode(result.data)
                    is BaseResult.Error -> _codeStatus.value =
                        CodeStatus.ErrorCode(result.rawResponse)
                }
            }
        }
    }
}
sealed class CodeStatus{
    object Init: CodeStatus()
    data class SuccessCode(val verifyCodeEntity: VerifyCodeEntity): CodeStatus()
    data class ErrorCode(val verifyCodeResponse: WrappedResponse<VerifyCodeResponse>): CodeStatus()
    data class ShowToast(val message: Throwable): CodeStatus()
    data class SetLoading(val loading: Boolean): CodeStatus()
}

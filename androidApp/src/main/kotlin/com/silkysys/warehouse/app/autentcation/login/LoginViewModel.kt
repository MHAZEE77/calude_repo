package com.silkysys.warehouse.app.autentcation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.recaptcha.RecaptchaAction
import com.silkysys.warehouse.BuildConfig
import com.silkysys.warehouse.app.security.RecaptchaManager
import com.silkysys.warehouse.sharedcode.data.autentcation.login.dto.request.LoginRequest
import com.silkysys.warehouse.sharedcode.data.autentcation.login.dto.response.LoginResponse
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.domain.autentcation.login.entity.LoginEntity
import com.silkysys.warehouse.sharedcode.domain.autentcation.login.useCase.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch


class LoginViewModel (
    private val loginUseCase: LoginUseCase,
    private val recaptchaManager: RecaptchaManager,
) : ViewModel() {

    private val _state = MutableStateFlow<LoginStatus>(LoginStatus.Init)
    val state: StateFlow<LoginStatus> get() = _state

    private fun setLoading(loading: Boolean) {
        _state.value = LoginStatus.IsLoading(loading)
    }

    private fun showToast(message: Throwable) {
        _state.value = LoginStatus.ShowToast(message)
    }

    private fun showReCaptchaToast(exception: Throwable) {
        _state.value = LoginStatus.RecaptchaError(exception)
    }

    fun clearStatus() {
        _state.value = LoginStatus.Init
    }

    fun login(loginRequest: LoginRequest) {
        viewModelScope.launch {
            setLoading(true)
            if (BuildConfig.BUILD_TYPE.contentEquals("release")) {
                recaptchaManager.executeRecaptcha(RecaptchaAction.LOGIN)
                    .onSuccess { token ->
                        val requestWithToken = loginRequest.copy(gRecaptchaResponse = token)
                        loginApiCall(requestWithToken)
                    }
                    .onFailure { exception ->
                        setLoading(false)
                        showReCaptchaToast(exception)
                    }
            } else {
                loginApiCall(loginRequest)
            }
        }
    }

    private suspend fun loginApiCall(loginRequest: LoginRequest) {
        loginUseCase.loginUseCase(loginRequest)
            .catch { exception ->
                setLoading(false)
                showToast(exception)
            }
            .collect { baseResult ->
                setLoading(false)
                when (baseResult) {
                    is BaseResult.Success -> _state.value =
                        LoginStatus.SuccessLogin(baseResult.data)

                    is BaseResult.Error -> _state.value =
                        LoginStatus.ErrorLogin(baseResult.rawResponse)
                }
            }
    }
}

sealed class LoginStatus {
    object Init : LoginStatus()
    data class IsLoading(val isLoading: Boolean) : LoginStatus()
    data class ShowToast(val message: Throwable) : LoginStatus()
    data class SuccessLogin(val loginEntity: LoginEntity) : LoginStatus()
    data class ErrorLogin(val loginResponse: WrappedResponse<LoginResponse>) : LoginStatus()
    data class RecaptchaError(val message: Throwable) : LoginStatus()
}

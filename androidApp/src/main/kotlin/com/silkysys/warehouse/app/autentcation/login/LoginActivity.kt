package com.silkysys.warehouse.app.autentcation.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.silkysys.warehouse.R
import com.silkysys.warehouse.app.error_handle.ErrorHandler
import com.silkysys.warehouse.app.autentcation.forgetPasswordSendEmail.SendEmailActivity
import com.silkysys.warehouse.app.autentcation.main.activity.MainActivity
import com.silkysys.warehouse.app.ui.activity.BaseActivity
import com.silkysys.warehouse.app.ui.compose_ui.LoginField
import com.silkysys.warehouse.app.ui.compose_ui.LoginFormState
import com.silkysys.warehouse.app.ui.compose_ui.LoginScreen
import com.silkysys.warehouse.app.ui.utility.SavePref
import com.silkysys.warehouse.app.ui.utility.extension.showErrorAlert
import com.silkysys.warehouse.sharedcode.data.autentcation.login.dto.request.LoginRequest
import com.silkysys.warehouse.sharedcode.data.autentcation.login.dto.response.LoginResponse
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.domain.autentcation.login.entity.LoginEntity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.silkysys.warehouse.app.ui.compose_ui.dialog_boxes.AppProgressDialog

class LoginActivity : BaseActivity() {

    private val form = LoginFormState()
    private var showProgress by mutableStateOf(false)
    private val loginViewModel: LoginViewModel by viewModel()
    private val savePref: SavePref by inject()
    private var pendingEmail = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savePref.hasActiveSession()) {
            startHomeActivity()
            return
        }


        form.email = savedInstanceState?.getString(STATE_EMAIL) ?: savePref.rememberedEmail
        form.rememberMe = savedInstanceState?.getBoolean(STATE_REMEMBER_ME)
            ?: (savePref.rememberEmail && savePref.rememberedEmail.isNotBlank())

        setContent {
            LoginScreen(
                form = form,
                onSignIn = ::onSignInButtonPressed,
                onForgotPassword = {
                    loginViewModel.clearStatus()
                    startActivity(Intent(this, SendEmailActivity::class.java))
                },
            )

            if (showProgress) AppProgressDialog()
        }

        observeLogin()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_EMAIL, form.email)
        outState.putBoolean(STATE_REMEMBER_ME, form.rememberMe)
    }

    private fun onSignInButtonPressed() {
        if (!isValidInput()) return

        pendingEmail = form.email.trim()
        val password = form.password
        form.isSignInEnabled = false

        loginViewModel.login(
            LoginRequest(
                pendingEmail,
                password,
                "Android",
                savePref.deviceToken.ifBlank { "unavailable" },
                recaptcha_action = "LOGIN",
                gRecaptchaResponse = null,
            ),
        )
    }

    private fun observeLogin() {
        loginViewModel.state
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach(::handleLoginState)
            .launchIn(lifecycleScope)
    }

    private fun handleLoginState(state: LoginStatus) {
        when (state) {
            LoginStatus.Init -> Unit
            is LoginStatus.SuccessLogin -> handleSuccessfulLogin(state.loginEntity)
            is LoginStatus.ErrorLogin -> handleLoginError(state.loginResponse)
            is LoginStatus.IsLoading -> handleLoading(state.isLoading)
            is LoginStatus.ShowToast -> {
                handleLoading(false)
                ErrorHandler.handleException(state.message, this)
            }
            is LoginStatus.RecaptchaError -> {
                handleLoading(false)
                showErrorAlert(getString(R.string.recaptcha_unavailable))
            }
        }
    }

    private fun handleSuccessfulLogin(loginEntity: LoginEntity) {
        val companyId = loginEntity.companies?.id
        if (companyId == null || loginEntity.token.isBlank() || loginEntity.refreshToken.isBlank() || loginEntity.clientId.isBlank()) {
            handleLoading(false)
            showErrorAlert(getString(R.string.invalid_login_response))
            return
        }

        savePref.companyId = companyId
        savePref.authorization = loginEntity.token
        savePref.refreshToken = loginEntity.refreshToken
        savePref.clientId = loginEntity.clientId
        savePref.rememberEmail = form.rememberMe
        savePref.rememberedEmail = if (form.rememberMe) pendingEmail else ""
        form.password = ""
        startHomeActivity()
    }

    private fun handleLoginError(loginResponse: WrappedResponse<LoginResponse>) {
        handleLoading(false)
        showErrorAlert(loginResponse.message.ifBlank { getString(R.string.sign_in_failed) })
    }

    private fun handleLoading(loading: Boolean) {
        form.isSignInEnabled = !loading
        showProgress = loading && !isFinishing
    }

    private fun startHomeActivity() {
        startActivity(
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            },
        )
        finish()
    }

    private fun isValidInput(): Boolean {
        val email = form.email.trim()
        val password = form.password
        if (ValidateLogin.validateEmail(email)) {
            form.emailError = getString(R.string.username_empty_error)
            form.focusRequest = LoginField.Email
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            form.emailError = getString(R.string.enter_valid_email)
            form.focusRequest = LoginField.Email
            return false
        }
        if (ValidateLogin.validatePassword(password)) {
            form.passwordError = getString(R.string.password_empty_error)
            form.focusRequest = LoginField.Password
            return false
        }
        return true
    }

    private companion object {
        const val STATE_EMAIL = "state_email"
        const val STATE_REMEMBER_ME = "state_remember_me"
    }
}
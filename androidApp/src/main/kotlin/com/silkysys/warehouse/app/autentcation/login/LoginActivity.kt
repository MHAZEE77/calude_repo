package com.silkysys.warehouse.app.autentcation.login

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.silkysys.warehouse.R
import com.silkysys.warehouse.app.error_handle.ErrorHandler
import com.silkysys.warehouse.app.autentcation.forgetPasswordSendEmail.SendEmailActivity
import com.silkysys.warehouse.app.autentcation.main.activity.MainActivity
import com.silkysys.warehouse.app.ui.activity.BaseActivity
import com.silkysys.warehouse.app.ui.utility.SavePref
import com.silkysys.warehouse.app.ui.utility.Utility
import com.silkysys.warehouse.app.ui.utility.extension.showErrorAlert
import com.silkysys.warehouse.databinding.ActivityLoginBinding
import com.silkysys.warehouse.sharedcode.data.autentcation.login.dto.request.LoginRequest
import com.silkysys.warehouse.sharedcode.data.autentcation.login.dto.response.LoginResponse
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.domain.autentcation.login.entity.LoginEntity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var progressDialog: AlertDialog
    private val loginViewModel: LoginViewModel by viewModel()
    private val savePref: SavePref by inject()
    private var pendingEmail = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savePref.hasActiveSession()) {
            startHomeActivity()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressDialog = Utility.getProgressDialog(this)

        binding.usernameEditText.setText(savePref.rememberedEmail)
        binding.cbRemeberMe.isChecked = savePref.rememberEmail && savePref.rememberedEmail.isNotBlank()
        binding.usernameEditText.setSelection(binding.usernameEditText.text?.length ?: 0)

        binding.signInButton.setOnClickListener { onSignInButtonPressed() }
        binding.tvForgotPassword.setOnClickListener {
            loginViewModel.clearStatus()
            startActivity(Intent(this, SendEmailActivity::class.java))
        }

        binding.passwordEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                onSignInButtonPressed()
                true
            } else {
                false
            }
        }

        binding.usernameEditText.doAfterTextChanged { binding.usernameEditText.error = null }
        binding.passwordEditText.doAfterTextChanged { binding.passwordEditText.error = null }

        observeLogin()
    }

    override fun onDestroy() {
        if (::progressDialog.isInitialized && progressDialog.isShowing) progressDialog.dismiss()
        super.onDestroy()
    }

    private fun onSignInButtonPressed() {
        if (!isValidInput()) return

        pendingEmail = binding.usernameEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString()
        binding.signInButton.isEnabled = false

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
        savePref.rememberEmail = binding.cbRemeberMe.isChecked
        savePref.rememberedEmail = if (binding.cbRemeberMe.isChecked) pendingEmail else ""
        binding.passwordEditText.text?.clear()
        startHomeActivity()
    }

    private fun handleLoginError(loginResponse: WrappedResponse<LoginResponse>) {
        handleLoading(false)
        showErrorAlert(loginResponse.message.ifBlank { getString(R.string.sign_in_failed) })
    }

    private fun handleLoading(loading: Boolean) {
        binding.signInButton.isEnabled = !loading
        if (loading && !isFinishing) {
            progressDialog.show()
        } else if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
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
        val email = binding.usernameEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString()
        if (ValidateLogin.validateEmail(email)) {
            binding.usernameEditText.error = getString(R.string.username_empty_error)
            binding.usernameEditText.requestFocus()
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.usernameEditText.error = getString(R.string.enter_valid_email)
            binding.usernameEditText.requestFocus()
            return false
        }
        if (ValidateLogin.validatePassword(password)) {
            binding.passwordEditText.error = getString(R.string.password_empty_error)
            binding.passwordEditText.requestFocus()
            return false
        }
        return true
    }
}

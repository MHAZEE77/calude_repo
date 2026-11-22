package com.silkysys.warehouse.app.autentcation.forgetPassword

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.silkysys.warehouse.app.error_handle.ErrorHandler
import com.silkysys.warehouse.app.autentcation.login.LoginActivity
import com.silkysys.warehouse.app.ui.activity.BaseActivity
import com.silkysys.warehouse.app.ui.utility.extension.hideKeyboard
import com.silkysys.warehouse.app.ui.utility.extension.showErrorAlert
import com.silkysys.warehouse.app.ui.utility.extension.showSuccessAlert
import com.silkysys.warehouse.sharedcode.data.autentcation.updateForgetPassword.dto.UpdateForgetPasswordRequest
import com.silkysys.warehouse.sharedcode.data.autentcation.updateForgetPassword.dto.UpdateForgetPasswordResponse
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.domain.autentcation.updateForgetPassword.entity.UpdateForgetPasswordEntity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.silkysys.warehouse.app.ui.compose_ui.dialog_boxes.AppProgressDialog
import com.silkysys.warehouse.app.ui.compose_ui.ForgotPasswordScreen


class ForgotPasswordActivity : BaseActivity() {
    private var showProgress by mutableStateOf(false)
    private lateinit var userEmail: String
    private lateinit var resetToken: String
    private var passwordInput by mutableStateOf("")
    private var confirmPasswordInput by mutableStateOf("")

    private val forgetPasswordViewModel: ForgetPasswordViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userEmail = intent.getStringExtra("email")?.trim().orEmpty()
        if (userEmail.isBlank()) {
            startActivity(Intent(this, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            })
            finish()
            return
        }

        resetToken = intent.getStringExtra("reset_token")?.trim().orEmpty()
        if (resetToken.isBlank()) {
            startActivity(Intent(this, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            })
            finish()
            return
        }

        setContent {
            ForgotPasswordScreen(
                password = passwordInput,
                onPasswordChange = { passwordInput = it },
                confirmPassword = confirmPasswordInput,
                onConfirmPasswordChange = { confirmPasswordInput = it },
                onCreate = { onCreateClick() },
                onBack = {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                },
            )

            if (showProgress) AppProgressDialog()
        }
        hideKeyboard()
        observeUpdatePassword()
    }

    private fun onCreateClick() {
        val errors = validatePassword()
        if (errors.isEmpty()) {
            requestCreateNewPasswordApi()
        } else {
            showPasswordErrorDialog(this, errors)
        }
    }

    fun validatePassword(): List<String> {

        val password = passwordInput
        val confirmPassword = confirmPasswordInput
        val errors = mutableListOf<String>()

        if (password != confirmPassword){
            errors.add("Password and confirm Password not similar")
        }
        if (password.length < 8) {
            errors.add("Password must be at least 8 characters long")
        }
        if (password.isEmpty() || !password[0].isUpperCase()) {
            errors.add("Password must start with a capital letter")
        }
        if (password.none { !it.isLetterOrDigit() }) {
            errors.add("Password must contain at least one special character")
        }
        if (password.none { it.isDigit() }) {
            errors.add("Password must contain at least one number")
        }

        return errors
    }

    private fun observeUpdatePassword() {
        forgetPasswordViewModel.state.flowWithLifecycle(
            lifecycle, Lifecycle.State.STARTED
        ).onEach { updateState ->
            handleUpdateState(updateState)
        }.launchIn(lifecycleScope)
    }

    private fun handleUpdateState(updateState: UpdatePassword) {
        when (updateState) {
            is UpdatePassword.Init -> Unit
            is UpdatePassword.SuccessUpdate -> handleSuccessUpdate(updateState.updateForgetPasswordEntity)
            is UpdatePassword.ErrorUpdate -> handleErrorUpdate(updateState.updateForgetPasswordResponse)
            is UpdatePassword.SetLoading -> handleLoading(updateState.isLoading)
            is UpdatePassword.ShowToast -> {
                handleLoading(false)
                ErrorHandler.handleException(updateState.message, this)
            }
        }
    }

    private fun handleLoading(loading: Boolean) {
        showProgress = loading
    }

    private fun handleErrorUpdate(updateForgetPasswordResponse: WrappedResponse<UpdateForgetPasswordResponse>) {
        showErrorAlert(updateForgetPasswordResponse.message)
    }

    private fun handleSuccessUpdate(updateForgetPasswordEntity: UpdateForgetPasswordEntity) {
        showSuccessAlert(updateForgetPasswordEntity.message) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun requestCreateNewPasswordApi() {
        val passwordText = passwordInput
        val confirmPasswordText = confirmPasswordInput
        forgetPasswordViewModel.updatePassword(
            UpdateForgetPasswordRequest(userEmail, passwordText, confirmPasswordText, resetToken)
        )
    }
    fun showPasswordErrorDialog(context: Context, errors: List<String>) {
        if (errors.isEmpty()) return

        val message = errors.joinToString("\n") { "• $it" }

        AlertDialog.Builder(context)
            .setTitle("Invalid Password")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

}
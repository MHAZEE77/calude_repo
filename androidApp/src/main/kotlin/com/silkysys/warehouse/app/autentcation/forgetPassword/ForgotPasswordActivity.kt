package com.silkysys.warehouse.app.autentcation.forgetPassword

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.silkysys.warehouse.R
import com.silkysys.warehouse.app.error_handle.ErrorHandler
import com.silkysys.warehouse.app.autentcation.login.LoginActivity
import com.silkysys.warehouse.app.ui.activity.BaseActivity
import com.silkysys.warehouse.app.ui.utility.SavePref
import com.silkysys.warehouse.app.ui.utility.Utility
import com.silkysys.warehouse.app.ui.utility.extension.hideKeyboard
import com.silkysys.warehouse.app.ui.utility.extension.showErrorAlert
import com.silkysys.warehouse.app.ui.utility.extension.showSuccessAlert
import com.silkysys.warehouse.databinding.ActivityForgotPasswordBinding
import com.silkysys.warehouse.sharedcode.data.autentcation.updateForgetPassword.dto.UpdateForgetPasswordRequest
import com.silkysys.warehouse.sharedcode.data.autentcation.updateForgetPassword.dto.UpdateForgetPasswordResponse
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.domain.autentcation.updateForgetPassword.entity.UpdateForgetPasswordEntity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class ForgotPasswordActivity : BaseActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var mDialog: AlertDialog
    private lateinit var userEmail: String
    private lateinit var resetToken: String

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

        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        mDialog = Utility.getProgressDialog(this)
        setContentView(binding.root)
        hideKeyboard()
        setupClickListener()
        observeUpdatePassword()
    }

    private fun setupClickListener() {
        binding.ivBackarrow.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.createButton.setOnClickListener {
            val errors = validatePassword()
            if (errors.isEmpty()) {
                requestCreateNewPasswordApi()
            } else {
                showPasswordErrorDialog(this, errors)
            }
        }
    }

    fun validatePassword(): List<String> {

        val password = binding.password.text.toString()
        val confirmPassword = binding.confirmPassword.text.toString()
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
        if (loading)
            mDialog.show()
        else
            mDialog.dismiss()
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
        val passwordText = binding.password.text.toString()
        val confirmPasswordText = binding.confirmPassword.text.toString()
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

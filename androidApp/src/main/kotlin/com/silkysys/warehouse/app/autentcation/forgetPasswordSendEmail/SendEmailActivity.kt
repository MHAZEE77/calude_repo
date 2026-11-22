package com.silkysys.warehouse.app.autentcation.forgetPasswordSendEmail

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.silkysys.warehouse.R
import com.silkysys.warehouse.app.error_handle.ErrorHandler
import com.silkysys.warehouse.app.autentcation.forgetPasswordVerifyCode.VerifyCodeActivity
import com.silkysys.warehouse.app.ui.activity.BaseActivity
import com.silkysys.warehouse.app.ui.compose_ui.SendEmailScreen
import com.silkysys.warehouse.app.ui.utility.extension.showErrorAlert
import com.silkysys.warehouse.app.ui.utility.extension.showSuccessAlert
import com.silkysys.warehouse.sharedcode.data.autentcation.sendemail.dto.SendEmailRequest
import com.silkysys.warehouse.sharedcode.data.autentcation.sendemail.dto.SendEmailResponse
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.domain.autentcation.sendemail.entity.SendEmailEntity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.silkysys.warehouse.app.ui.compose_ui.dialog_boxes.AppProgressDialog

class SendEmailActivity : BaseActivity() {
    private val sendEmailViewmodel: SendEmailViewmodel by viewModel()

    private var showProgress by mutableStateOf(false)
    private lateinit var email: String
    private var emailInput by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SendEmailScreen(
                email = emailInput,
                onEmailChange = { emailInput = it },
                onSend = { sendEmail() },
                onBack = { finish() },
            )

            if (showProgress) AppProgressDialog()
        }
        observeUpdatePassword()
    }

    private fun sendEmail() {
        email = emailInput
        if (email.trim().isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches())
            sendEmailViewmodel.updatePassword(SendEmailRequest(email))
        else
            showErrorAlert(getString(R.string.enter_valid_email))
    }

    private fun handleUpdatePassword(updateState: UpdatePasswordStatus) {
        when (updateState) {
            is UpdatePasswordStatus.Init -> Unit
            is UpdatePasswordStatus.ShowToast -> {
                setLoading(false)
                ErrorHandler.handleException(updateState.message, this)
            }
            is UpdatePasswordStatus.IsLoading -> setLoading(updateState.isLoading)
            is UpdatePasswordStatus.UpdateSuccess -> handleSuccess(updateState.updatePasswordEntity)
            is UpdatePasswordStatus.UpdateError -> handleError(updateState.sendEmailResponse)
        }
    }
    private fun handleSuccess(entity: SendEmailEntity) {

        setLoading(false)
        showSuccessAlert(entity.message) {
            val intent = Intent(this, VerifyCodeActivity::class.java)
            intent.putExtra("email",emailInput)
            startActivity(intent)
            finish()
        }
    }

    private fun handleError(sendEmailResponse: WrappedResponse<SendEmailResponse>) {
        setLoading(false)
        showErrorAlert(sendEmailResponse.message)
    }

    private fun setLoading(loading: Boolean) {
        showProgress = loading
    }

    private fun observeUpdatePassword() {
        sendEmailViewmodel.updateState.flowWithLifecycle(
            lifecycle, Lifecycle.State.STARTED
        ).onEach { updateState ->
            handleUpdatePassword(updateState)
        }.launchIn(lifecycleScope)
    }
}
package com.silkysys.warehouse.app.autentcation.forgetPasswordSendEmail

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.silkysys.warehouse.R
import com.silkysys.warehouse.app.error_handle.ErrorHandler
import com.silkysys.warehouse.app.autentcation.forgetPasswordVerifyCode.VerifyCodeActivity
import com.silkysys.warehouse.app.ui.activity.BaseActivity
import com.silkysys.warehouse.app.ui.utility.Utility
import com.silkysys.warehouse.app.ui.utility.extension.showErrorAlert
import com.silkysys.warehouse.app.ui.utility.extension.showSuccessAlert
import com.silkysys.warehouse.databinding.ActivityForgetPasswordSendEmailBinding
import com.silkysys.warehouse.sharedcode.data.autentcation.sendemail.dto.SendEmailRequest
import com.silkysys.warehouse.sharedcode.data.autentcation.sendemail.dto.SendEmailResponse
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.domain.autentcation.sendemail.entity.SendEmailEntity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class SendEmailActivity : BaseActivity() {
    private lateinit var binding: ActivityForgetPasswordSendEmailBinding
    private val sendEmailViewmodel: SendEmailViewmodel by viewModel()

    private lateinit var mDialog: AlertDialog
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordSendEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mDialog = Utility.getProgressDialog(this)
        setClicks()
        observeUpdatePassword()

        binding.ivBackarrow.setOnClickListener {
            finish()
        }
    }

    private fun setClicks() {
        binding.btSendEmail.setOnClickListener {
            email = binding.etEmail.text.toString()
            if (email.trim().isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches())
                sendEmailViewmodel.updatePassword(SendEmailRequest(email))
            else
                showErrorAlert(getString(R.string.enter_valid_email))
        }

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
            intent.putExtra("email",binding.etEmail.text.toString())
            startActivity(intent)
            finish()
        }
    }

    private fun handleError(sendEmailResponse: WrappedResponse<SendEmailResponse>) {
        setLoading(false)
        showErrorAlert(sendEmailResponse.message)
    }

    private fun setLoading(loading: Boolean) {
        if (loading)
            mDialog.show()
        else
            mDialog.dismiss()
    }

    private fun observeUpdatePassword() {
        sendEmailViewmodel.updateState.flowWithLifecycle(
            lifecycle, Lifecycle.State.STARTED
        ).onEach { updateState ->
            handleUpdatePassword(updateState)
        }.launchIn(lifecycleScope)
    }
}

package com.silkysys.warehouse.app.autentcation.forgetPasswordVerifyCode

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.silkysys.warehouse.app.error_handle.ErrorHandler
import com.silkysys.warehouse.app.autentcation.forgetPassword.ForgotPasswordActivity
import com.silkysys.warehouse.app.ui.activity.BaseActivity
import com.silkysys.warehouse.app.ui.utility.Utility
import com.silkysys.warehouse.app.ui.utility.extension.showErrorAlert
import com.silkysys.warehouse.sharedcode.data.autentcation.forgetPasswordVerifyCode.dto.VerifyCodeRequest
import com.silkysys.warehouse.sharedcode.data.autentcation.forgetPasswordVerifyCode.dto.VerifyCodeResponse
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.databinding.ActivityForgetPasswordSendCodeBinding
import com.silkysys.warehouse.sharedcode.domain.autentcation.forgetPasswordVerifyCode.entity.VerifyCodeEntity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel


class VerifyCodeActivity : BaseActivity() {
    private lateinit var binding: ActivityForgetPasswordSendCodeBinding
    private val forgetPasswordViewModel: VerifyCodeViewModel by viewModel()
    private lateinit var email: String
    private lateinit var code: String
    private lateinit var mDialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityForgetPasswordSendCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mDialog = Utility.getProgressDialog(this)
        email = intent.getStringExtra("email").toString()
        setClicks()
        observeVerifyCode()

    }

    private fun observeVerifyCode() {
        forgetPasswordViewModel.codeStatus.flowWithLifecycle(
            lifecycle,Lifecycle.State.STARTED)
            .onEach { state -> handleState(state)
            }.launchIn(lifecycleScope)
    }

    private fun handleState(state: CodeStatus) {
        when(state){
            is CodeStatus.Init -> Unit
            is CodeStatus.SuccessCode -> successVerify(state.verifyCodeEntity)
            is CodeStatus.ErrorCode -> errorVerify(state.verifyCodeResponse)
            is CodeStatus.SetLoading -> setLoading(state.loading)
            is CodeStatus.ShowToast -> showToast(state.message)
        }

    }

    private fun showToast(message: Throwable) {
        setLoading(false)
        ErrorHandler.handleException(message,this)
    }

    private fun setLoading(loading: Boolean) {
        if (loading)
            mDialog.show()
        else
            mDialog.dismiss()
    }

    private fun errorVerify(verifyCodeResponse: WrappedResponse<VerifyCodeResponse>) {
        showErrorAlert(verifyCodeResponse.message.toString())
    }

    private fun successVerify(verifyCodeEntity: VerifyCodeEntity) {
        val intent = Intent(this, ForgotPasswordActivity::class.java)
        intent.putExtra("email", email)
        intent.putExtra("reset_token", verifyCodeEntity.resetToken)
        startActivity(intent)
    }

    private fun setClicks() {
        binding.ivBackarrow.setOnClickListener {
            finish()
        }
        binding.btSendCode.setOnClickListener {
            code= binding.etCode.text.toString().trim()
            if (code.isNotEmpty())
                forgetPasswordViewModel.verifyCode(VerifyCodeRequest(email,code))
        }
    }
}

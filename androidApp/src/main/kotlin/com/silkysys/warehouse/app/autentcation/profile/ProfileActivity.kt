package com.silkysys.warehouse.app.autentcation.profile

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.silkysys.warehouse.R
import com.silkysys.warehouse.app.error_handle.ErrorHandler
import com.silkysys.warehouse.app.autentcation.forgetPasswordSendEmail.SendEmailActivity
import com.silkysys.warehouse.app.ui.activity.BaseActivity
import com.silkysys.warehouse.app.ui.utility.SavePref
import com.silkysys.warehouse.app.ui.utility.extension.showErrorAlert
import com.silkysys.warehouse.sharedcode.data.autentcation.user.dto.UserResponse
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.domain.autentcation.user.entity.UserEntity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.silkysys.warehouse.app.ui.compose_ui.dialog_boxes.AppProgressDialog
import com.silkysys.warehouse.app.ui.compose_ui.ProfileScreen


class ProfileActivity : BaseActivity() {

    // Properties
    private var showProgress by mutableStateOf(false)
    private val profileViewModel: ProfileViewModel by viewModel()
    private val savePref: SavePref by inject()
    private var userName by mutableStateOf<String?>(null)
    private var userEmail by mutableStateOf<String?>(null)
    private var userImageUrl by mutableStateOf<String?>(null)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!requireAuthenticatedSession()) return
        setContent {
            ProfileScreen(
                name = userName,
                email = userEmail,
                imageUrl = userImageUrl,
                onBack = { finish() },
                onChangePassword = {
                    val intent = Intent(this, SendEmailActivity::class.java)
                    startActivity(intent)
                },
            )

            if (showProgress) AppProgressDialog()
        }
        profileViewModel.user(savePref.companyId)
        observeUser()
        // Call Methods
        setupActionBar()

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Private Methods
     * */
    private fun observeUser() {
        profileViewModel.userStatus.flowWithLifecycle(
            lifecycle, Lifecycle.State.STARTED
        )
            .onEach { userState -> handleUser(userState) }
            .launchIn(lifecycleScope)
    }
    private fun handleUser(userState: UserStatus) {
        when (userState) {
            is UserStatus.Init -> Unit
            is UserStatus.SuccessUser -> handleUserSuccess(userState.userEntity)
            is UserStatus.ErrorUser -> handleUserError(userState.userResponse)
            is UserStatus.IsLoading -> handleLoading(userState.isLoading)
            is UserStatus.ShowToast -> handleShowToast(userState.exception)
        }
    }
    private fun handleUserSuccess(userEntity: UserEntity) {
        handleLoading(false)
        savePref.user = userEntity
        userName = userEntity.name
        userEmail = userEntity.email
        if (!userEntity.image_url.isNullOrEmpty()) {
            userImageUrl = userEntity.image_url
        }

    }
    private fun handleLoading(loading: Boolean) {
        showProgress = loading
    }
    private fun handleShowToast(exception: Throwable) {
        handleLoading(false)
        ErrorHandler.handleException(exception,this)
    }
    private fun handleUserError(userResponse: WrappedResponse<UserResponse>) {
        showErrorAlert(userResponse.message)
    }
    private fun setupActionBar() {
        // set title action bar
        supportActionBar?.title = getString(R.string.nav_profile)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

}
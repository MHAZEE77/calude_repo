package com.silkysys.warehouse.app.autentcation.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.LinearLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.size.Scale
import com.silkysys.warehouse.R
import com.silkysys.warehouse.app.error_handle.ErrorHandler
import com.silkysys.warehouse.app.autentcation.forgetPasswordSendEmail.SendEmailActivity
import com.silkysys.warehouse.app.ui.activity.BaseActivity
import com.silkysys.warehouse.app.ui.utility.SavePref
import com.silkysys.warehouse.app.ui.utility.Utility
import com.silkysys.warehouse.app.ui.utility.extension.showErrorAlert
import com.silkysys.warehouse.sharedcode.data.autentcation.user.dto.UserResponse
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.databinding.ActivityProfileBinding
import com.silkysys.warehouse.sharedcode.domain.autentcation.user.entity.UserEntity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class ProfileActivity : BaseActivity() {

    // Properties
    private lateinit var binding: ActivityProfileBinding
    private lateinit var mDialog: AlertDialog
    private val profileViewModel: ProfileViewModel by viewModel()
    private val savePref: SavePref by inject()
    private var personalOpened: Boolean = false
    private var warehouseOpened: Boolean = false
    private lateinit var personalLayout :LinearLayout
    private lateinit var warehouseLayout :LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!requireAuthenticatedSession()) return
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mDialog = Utility.getProgressDialog(this)
        personalLayout= binding.linPersonalInfo
        warehouseLayout= binding.linWarehouseInfo
        setupClickListeners()
        profileViewModel.user(savePref.companyId)
        observeUser()
        // Call Methods
        setupActionBar()

    }
    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.tvChangePassword.setOnClickListener {
            val intent = Intent(this, SendEmailActivity::class.java)
            startActivity(intent)
        }
        binding.ivHideShowPersonalInfo.setOnClickListener {
            if (personalOpened) {
                // visibility of view
                binding.ivHideShowPersonalInfo.setImageResource(R.drawable.hide_icon)
                personalLayout.visibility = View.VISIBLE
                val animate = TranslateAnimation(0F, 0F, personalLayout.height.toFloat(), 0F)
                // duration of animation
                animate.duration = 200
                animate.fillAfter = true
                personalLayout.startAnimation(animate)
            } else {
                binding.ivHideShowPersonalInfo.setImageResource(R.drawable.show_icon)
                personalLayout.visibility = View.GONE
                val animate = TranslateAnimation(0F, 0F, 0F, personalLayout.height.toFloat())
                 animate.duration = 0
                personalLayout.startAnimation(animate)
             }
            personalOpened = !personalOpened
        }
        binding.ivHideShowWarehouseInfo.setOnClickListener {
            if (warehouseOpened) {
                // visibility of view
                binding.ivHideShowWarehouseInfo.setImageResource(R.drawable.hide_icon)
                warehouseLayout.visibility = View.VISIBLE
                val animate = TranslateAnimation(0F, 0F, warehouseLayout.height.toFloat(), 0F)
                // duration of animation
                animate.duration = 200
                animate.fillAfter = true
                warehouseLayout.startAnimation(animate)
            } else {
                binding.ivHideShowWarehouseInfo.setImageResource(R.drawable.show_icon)
                warehouseLayout.visibility = View.GONE
                val animate = TranslateAnimation(0F, 0F, 0F, warehouseLayout.height.toFloat())
                animate.duration = 0
                warehouseLayout.startAnimation(animate)
            }
            warehouseOpened = !warehouseOpened
        }
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
        binding.tvName.text = userEntity.name
        binding.tvEmail.text = userEntity.email
        if (!userEntity.image_url.isNullOrEmpty()) {

            binding.userImage.load(userEntity.image_url) {
                scale(Scale.FIT)
                placeholder(R.drawable.image_placeholder)
                error(R.drawable.image_placeholder)
            }
        }

    }
    private fun handleLoading(loading: Boolean) {
        if (loading)
            mDialog.show()
        else
            mDialog.dismiss()
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

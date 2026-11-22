package com.silkysys.warehouse.app.autentcation.main.activity


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.silkysys.warehouse.BuildConfig
import com.silkysys.warehouse.R
import com.silkysys.warehouse.app.addStock.AddStockActivity
import com.silkysys.warehouse.app.autentcation.login.LoginActivity
import com.silkysys.warehouse.app.autentcation.main.LogOutStatus
import com.silkysys.warehouse.app.autentcation.main.MainViewModel
import com.silkysys.warehouse.app.autentcation.main.adapter.GridViewModal
import com.silkysys.warehouse.app.autentcation.profile.ProfileActivity
import com.silkysys.warehouse.app.packing.pack.activity.PackActivity
import com.silkysys.warehouse.app.picking.pick.activity.PickActivity
import com.silkysys.warehouse.app.putAway.putAway.activity.PutAwayActivity
import com.silkysys.warehouse.app.ui.activity.BaseActivity
import com.silkysys.warehouse.app.ui.adapter.MainAdapter
import com.silkysys.warehouse.app.ui.compose_ui.dialog_boxes.ChangeLanguageDialog
import com.silkysys.warehouse.app.ui.compose_ui.MainDrawerItem
import com.silkysys.warehouse.app.ui.compose_ui.MainScreen
import com.silkysys.warehouse.app.ui.utility.LocaleManager
import com.silkysys.warehouse.app.ui.utility.SavePref
import com.silkysys.warehouse.app.ui.utility.Utility
import com.silkysys.warehouse.app.ui.utility.extension.showErrorAlert
import com.silkysys.warehouse.domain.autentcation.logOut.entity.LogOutEntity
import com.silkysys.warehouse.sharedcode.data.autentcation.logOut.dto.LogOutResponse
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.wms.app.ui.utility.alert
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.activity.OnBackPressedCallback
import com.silkysys.warehouse.app.ui.compose_ui.dialog_boxes.AppProgressDialog
import com.silkysys.warehouse.app.ui.compose_ui.dialog_boxes.LogoutDialog


class MainActivity : BaseActivity() {

    // Properties
    private var showProgress by mutableStateOf(false)
    private lateinit var adapter: MainAdapter
    private var isOpenPickActivity = false
    private val mainViewModel: MainViewModel by viewModel()

    private val savePref: SavePref by inject()
    private var buildTypeLabel by mutableStateOf<String?>(null)
    private var showLogoutDialog by mutableStateOf(false)
    private var showChangeLanguageDialog by mutableStateOf(false)
    lateinit var serviceList: List<GridViewModal>

    // Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!requireAuthenticatedSession()) return
        // Initializes properties
        // Call Methods
        observeLogOut()
        setGridView()

        setBuildTypeInfo()

        setContent {
            MainScreen(
                services = serviceList,
                buildTypeLabel = buildTypeLabel,
                onServiceClick = ::onServiceClick,
                onDrawerItemClick = ::onDrawerItemSelected,
            )

            if (showLogoutDialog) {
                LogoutDialog(
                    title = getString(R.string.log_out),
                    message = getString(R.string.alarm_log_out),
                    cancelable = false,
                    confirmText = getString(android.R.string.yes),
                    onConfirm = {
                        showLogoutDialog = false
                        requestLogoutApi()
                    },
                    dismissText = getString(android.R.string.no),
                    onDismiss = { showLogoutDialog = false },
                )
            }

            if (showChangeLanguageDialog) {
                ChangeLanguageDialog(
                    title = getString(R.string.change_language),
                    languages = LocaleManager.arrayLanguage.toList(),
                    initialSelectedIndex = LocaleManager.getLocaleIndex(savePref.language) ?: 0,
                    confirmText = getString(R.string.change),
                    onConfirm = { selectIndex ->
                        showChangeLanguageDialog = false
                        changeLanguage(selectIndex)
                    },
                    dismissText = getString(android.R.string.cancel),
                    onDismiss = { showChangeLanguageDialog = false },
                )
            }

            if (showProgress) AppProgressDialog()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                alert(getString(R.string.are_you_sure_to_exit)) {
                    title = getString(R.string.exit)
                    yesButton { requestLogoutApi() }
                    noButton { }
                }.show()
            }
        })
    }

    private fun setBuildTypeInfo() {
        when (BuildConfig.BUILD_TYPE) {
            "debug" -> buildTypeLabel = getString(R.string.demo)

            "stage" -> buildTypeLabel = getString(R.string.stage)

            "release" -> buildTypeLabel = null
        }
    }

    private fun setGridView() {
        serviceList = ArrayList()
        serviceList = serviceList + GridViewModal(getString(R.string.picking), R.drawable.pick)
        serviceList = serviceList + GridViewModal(getString(R.string.pack), R.drawable.pack)
        serviceList =
            serviceList + GridViewModal(getString(R.string.put_away_menu), R.drawable.inventory)
        serviceList =
            serviceList + GridViewModal(getString(R.string.add_stock), R.drawable.add_stock)
    }

    private fun onServiceClick(position: Int) {
        when (serviceList[position].serviceName) {
            getString(R.string.picking) -> {
                openPickActivity()
            }

            getString(R.string.pack) -> {
                openPackActivity()
            }

            getString(R.string.put_away_menu) -> {
                openPutAwayActivity()
            }

            getString(R.string.add_stock) -> {
                openAddStockActivity()
            }

        }
    }

    private fun onDrawerItemSelected(item: MainDrawerItem) {
        when (item) {
            MainDrawerItem.Home -> Unit

            MainDrawerItem.Profile -> openProfile()

            MainDrawerItem.ChangeLanguage -> changeLanguageDialog()

            MainDrawerItem.Logout -> showLogoutUserAlert()
        }
    }

    private fun changeLanguageDialog() {
        showChangeLanguageDialog = true
    }

    // positive button of the old Utility.changeLanguageDialog
    private fun changeLanguage(selectIndex: Int) {
        val language = savePref.language
        val selectedLanguage = LocaleManager.arrayFlag[selectIndex]

        if (language != selectedLanguage) {
            LocaleManager.setNewLocale(this, selectedLanguage)
            startActivity(
                Intent(this, MainActivity::class.java).apply {
                    addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_SINGLE_TOP
                    )
                }
            )
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun showLogoutUserAlert() {
        showLogoutDialog = true
    }

    private fun logoutUser() {
        Utility.logoutAccount(this)
    }

    // Open Activities
    private fun openPickActivity() {
        val intent = Intent(this, PickActivity::class.java)
        startActivity(intent)
    }

    private fun openPackActivity() {
        val intent = Intent(this, PackActivity::class.java)
        startActivity(intent)
    }

    private fun openPutAwayActivity() {
        val intent = Intent(this, PutAwayActivity::class.java)
        startActivity(intent)
    }

    private fun openAddStockActivity() {
        val intent = Intent(this, AddStockActivity::class.java)
        startActivity(intent)
    }

    private fun openProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    private fun requestLogoutApi() {
        mainViewModel.logOut(savePref.companyId)
    }

    private fun observeLogOut() {
        mainViewModel.logOutStatus.flowWithLifecycle(
            lifecycle, Lifecycle.State.STARTED
        )
            .onEach { logOutState -> handleLogOut(logOutState) }
            .launchIn(lifecycleScope)
    }

    private fun handleLogOut(logOutState: LogOutStatus) {
        when (logOutState) {
            is LogOutStatus.Init -> Unit
            is LogOutStatus.SuccessLogOut -> handleLogOutSuccess(logOutState.logOutEntity)
            is LogOutStatus.ErrorLogOut -> handleLogOutError(logOutState.logOutResponse)
            is LogOutStatus.IsLoading -> handleLoading(logOutState.loading)
            is LogOutStatus.ShowLogOutToast -> handleShowToast(logOutState.message)
        }
    }

    private fun handleLogOutSuccess(logOutEntity: LogOutEntity) {
        logoutUser()
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun handleLogOutError(logOutResponse: WrappedResponse<LogOutResponse>) {
        showErrorAlert(logOutResponse.message)
    }

    private fun handleShowToast(message: String) {
        //showErrorAlert(getString(R.string.api_request_error))
    }

    private fun handleLoading(loading: Boolean) {
        showProgress = loading
    }

}
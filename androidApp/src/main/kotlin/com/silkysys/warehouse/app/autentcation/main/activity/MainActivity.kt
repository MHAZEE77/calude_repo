package com.silkysys.warehouse.app.autentcation.main.activity


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import androidx.core.view.GravityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.silkysys.warehouse.BuildConfig
import com.silkysys.warehouse.R
import com.silkysys.warehouse.app.addStock.AddStockActivity
import com.silkysys.warehouse.app.autentcation.login.LoginActivity
import com.silkysys.warehouse.app.autentcation.main.LogOutStatus
import com.silkysys.warehouse.app.autentcation.main.MainViewModel
import com.silkysys.warehouse.app.autentcation.main.adapter.GridRVAdapter
import com.silkysys.warehouse.app.autentcation.main.adapter.GridViewModal
import com.silkysys.warehouse.app.autentcation.profile.ProfileActivity
import com.silkysys.warehouse.app.packing.pack.activity.PackActivity
import com.silkysys.warehouse.app.picking.pick.activity.PickActivity
import com.silkysys.warehouse.app.putAway.putAway.activity.PutAwayActivity
import com.silkysys.warehouse.app.ui.activity.BaseActivity
import com.silkysys.warehouse.app.ui.adapter.MainAdapter
import com.silkysys.warehouse.app.ui.utility.SavePref
import com.silkysys.warehouse.app.ui.utility.Utility
import com.silkysys.warehouse.app.ui.utility.extension.showErrorAlert
import com.silkysys.warehouse.databinding.ActivityMainBinding
import com.silkysys.warehouse.domain.autentcation.logOut.entity.LogOutEntity
import com.silkysys.warehouse.sharedcode.data.autentcation.logOut.dto.LogOutResponse
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.wms.app.ui.utility.alert
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.activity.OnBackPressedCallback



class MainActivity : BaseActivity() {

    // Properties
    private lateinit var mDialog: AlertDialog
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MainAdapter
    private var isOpenPickActivity = false
    private val mainViewModel: MainViewModel by viewModel()

    private val savePref: SavePref by inject()
    lateinit var servicesGRV: GridView
    lateinit var serviceList: List<GridViewModal>

    // Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!requireAuthenticatedSession()) return
        // Initializes properties
        mDialog = Utility.getProgressDialog(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Call Methods
        setupActionBar()
        setupDrawer()
        observeLogOut()
        setGridView()
        binding.navigationView.itemIconTintList = null;

        setBuildTypeInfo()

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
            "debug" -> {
                binding.tvBuildType.text = getString(R.string.demo)
                binding.tvBuildType.visibility = View.VISIBLE
            }

            "stage" -> {
                binding.tvBuildType.text = getString(R.string.stage)
                binding.tvBuildType.visibility = View.VISIBLE
            }

            "release" -> binding.tvBuildType.visibility = View.GONE
        }
    }

    private fun setGridView() {
        servicesGRV = findViewById(R.id.idGRV)
        serviceList = ArrayList()
        serviceList = serviceList + GridViewModal(getString(R.string.picking), R.drawable.pick)
        serviceList = serviceList + GridViewModal(getString(R.string.pack), R.drawable.pack)
        serviceList =
            serviceList + GridViewModal(getString(R.string.put_away_menu), R.drawable.inventory)
        serviceList =
            serviceList + GridViewModal(getString(R.string.add_stock), R.drawable.add_stock)

        val courseAdapter = GridRVAdapter(courseList = serviceList, this@MainActivity)
        servicesGRV.adapter = courseAdapter
        servicesGRV.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
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
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                binding.drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbar)
        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setHomeAsUpIndicator(R.drawable.ic_menu)
    }

    @SuppressLint("SetTextI18n")
    private fun setupDrawer() {
        binding.navigationView.setNavigationItemSelectedListener {
            binding.drawerLayout.closeDrawers()
            when (it.itemId) {
                R.id.nav_home -> {

                    true
                }

                R.id.nav_profile -> {
                    openProfile()
                    true
                }

                R.id.nav_change_language -> {
                    changeLanguageDialog()
                    true
                }

                R.id.nav_logout -> {
                    showLogoutUserAlert()
                    true
                }

                else -> false
            }
        }
    }

    private fun changeLanguageDialog() {
        Utility.changeLanguageDialog(this)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun showLogoutUserAlert() {
        alert(getString(R.string.alarm_log_out)) {
            title = getString(R.string.log_out)
            isCancelable = false
            positiveButton(getString(android.R.string.yes)) {
                requestLogoutApi()

            }
            negativeButton(getString(android.R.string.no)) {}
        }.show()
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
        if (loading)
            mDialog.show()
        else
            mDialog.dismiss()
    }

}

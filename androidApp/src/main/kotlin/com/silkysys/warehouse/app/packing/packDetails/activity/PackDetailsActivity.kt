package com.silkysys.warehouse.app.packing.packDetails.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.silkysys.warehouse.R
import com.silkysys.warehouse.app.error_handle.ErrorHandler
import com.silkysys.warehouse.app.packing.addPack.activity.AddPackActivity
import com.silkysys.warehouse.app.packing.packDetails.FinishStatus
import com.silkysys.warehouse.app.packing.packDetails.PackDetailsViewModel
import com.silkysys.warehouse.app.packing.packDetails.PackStatus
import com.silkysys.warehouse.app.ui.activity.BaseActivity
import com.silkysys.warehouse.app.ui.compose_ui.dialog_boxes.AppAlertDialog
import com.silkysys.warehouse.app.ui.compose_ui.dialog_boxes.AppProgressDialog
import com.silkysys.warehouse.app.ui.compose_ui.packdetails.PackDetailsScreen
import com.silkysys.warehouse.app.ui.utility.SavePref
import com.silkysys.warehouse.app.ui.utility.extension.showErrorAlert
import com.silkysys.warehouse.sharedcode.domain.packing.finishPack.entity.FinishPackEntity
import com.silkysys.warehouse.sharedcode.domain.packing.packDetails.entity.PackDetailsEntity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PackDetailsActivity : BaseActivity() {

    private val  savePref: SavePref by inject()
    private var showProgress by mutableStateOf(false)
    private val packDetailsViewModel: PackDetailsViewModel by viewModel()
    private var packName by mutableStateOf<String?>(null)
    private var packListItems by mutableStateOf<List<PackDetailsEntity.PackList>>(emptyList())
    private var isEmptyVisible by mutableStateOf(false)
    private var canPack by mutableStateOf(true)
    private var showFinishDialog by mutableStateOf(false)
    private var packID:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!requireAuthenticatedSession()) return
        packName = intent.getStringExtra("packName")
        setContent {
            PackDetailsScreen(
                packName = packName,
                packListItems = packListItems,
                isEmptyVisible = isEmptyVisible,
                canPack = canPack,
                onBack = { finish() },
                onAddPack = { onAddPackClick() },
                onFinish = { onFinishClick() },
            )

            if (showFinishDialog) {
                AppAlertDialog(
                    message = getString(R.string.finish_pack_confirmation_message),
                    confirmText = getString(R.string.ok),
                    onConfirm = {
                        showFinishDialog = false
                        packDetailsViewModel.finishPack(savePref.companyId,packID)
                    },
                    dismissText = getString(R.string.cancel),
                    onDismiss = { showFinishDialog = false },
                )
            }

            if (showProgress) AppProgressDialog()
        }
        packID= intent.getIntExtra("packID",0)
        observePackDetails()
        observeFinisPack()
    }

    private fun observeFinisPack() {
        packDetailsViewModel.finishStatus.
        flowWithLifecycle(lifecycle,Lifecycle.State.STARTED)
            .onEach { finishStatus -> handleFinishStatus(finishStatus) }
            .launchIn(lifecycleScope)
    }

    override fun onResume() {
        super.onResume()
        packDetailsViewModel.getPackDetails(savePref.companyId,packID)
    }

    private fun handleFinishStatus(finishStatus: FinishStatus) {
        when(finishStatus){
            is FinishStatus.Init -> Unit
            is FinishStatus.SuccessFinis -> handleSuccessFinish(finishStatus.finishPackEntity)
            is FinishStatus.ErrorFinish -> showErrorAlert(finishStatus.finishPackResponse.message)
            is FinishStatus.SetFinishLoading -> handleLoading(finishStatus.loading)
            is FinishStatus.ShowFinishToast -> {
                handleLoading(false)
                ErrorHandler.handleException(finishStatus.message, this@PackDetailsActivity)
                //showErrorAlert(getString(R.string.api_request_error))
            }
        }
    }

    private fun handleSuccessFinish(finishPackEntity: FinishPackEntity) {
        finish()
    }
    //to add pack Item
    private fun onAddPackClick() {
        val intent= Intent(this,AddPackActivity::class.java)
        intent.putExtra("packID",packID)
        startActivity(intent)
    }

    // to finish this pack list
    private fun onFinishClick() {
        showFinishDialog = true
    }

    private fun observePackDetails() {
        packDetailsViewModel.packStatus.flowWithLifecycle(lifecycle,Lifecycle.State.STARTED)
            .onEach {detailsStatus -> handlePackDetailsStatus(detailsStatus)}
            .launchIn(lifecycleScope)
    }

    private fun handlePackDetailsStatus(detailsStatus: PackStatus){
        when(detailsStatus){
            is PackStatus.Init -> Unit
            is PackStatus.SuccessPackDetails -> handleSuccessGetDetails(detailsStatus.packDetailsEntity)
            is PackStatus.ErrorPackDetails -> showErrorAlert(detailsStatus.packDetailsResponse.message)
            is PackStatus.SetLoading -> handleLoading(detailsStatus.loading)
            is PackStatus.ShowToast -> {
                handleLoading(false)
                ErrorHandler.handleException(detailsStatus.message, this@PackDetailsActivity)

                //showErrorAlert(getString(R.string.api_request_error))
            }
        }
    }
    private fun handleLoading(loading: Boolean) {
        showProgress = loading
    }
    private fun handleSuccessGetDetails(packDetailsEntity: PackDetailsEntity) {
        canPack = packDetailsEntity.canPack
        if (packDetailsEntity.packListItems.isNotEmpty()) {
            packListItems = packDetailsEntity.packListItems
            isEmptyVisible = false
        }else{
            isEmptyVisible = true
        }
    }

}
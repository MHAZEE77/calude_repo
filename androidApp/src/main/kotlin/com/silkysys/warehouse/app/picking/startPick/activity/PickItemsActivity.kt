package com.silkysys.warehouse.app.picking.startPick.activity


import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.MenuItem
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.silkysys.warehouse.R
import com.silkysys.warehouse.app.error_handle.ErrorHandler
import com.silkysys.warehouse.app.picking.pick.activity.PickActivity
import com.silkysys.warehouse.app.picking.startPick.DialogQuantityStatus
import com.silkysys.warehouse.app.picking.startPick.GetStartedListStatus
import com.silkysys.warehouse.app.picking.startPick.QuantityStatus
import com.silkysys.warehouse.app.picking.startPick.StartPickViewModel
import com.silkysys.warehouse.app.picking.startPick.StartStatus
import com.silkysys.warehouse.app.security.BarcodeInputValidator
import com.silkysys.warehouse.app.ui.activity.BaseActivity
import com.silkysys.warehouse.app.ui.compose_ui.pick.PickItemsScreen
import com.silkysys.warehouse.app.ui.compose_ui.pick_items.PickQuantityDialog
import com.silkysys.warehouse.app.ui.utility.BarcodeScanningActivity
import com.silkysys.warehouse.app.ui.utility.SavePref
import com.silkysys.warehouse.app.ui.utility.Utility
import com.silkysys.warehouse.app.ui.utility.extension.showErrorAlert
import com.silkysys.warehouse.data.picking.startPickUpdateDialogQuantity.dto.UpdateQuantityRequest
import com.silkysys.warehouse.sharedcode.domain.picking.pick.entity.PickEntity
import com.silkysys.warehouse.sharedcode.domain.picking.startPick.entity.StartPickEntity
import com.silkysys.warehouse.sharedcode.domain.picking.startPickUpdateDialogQuantity.entity.UpdateDialogQuantityEntity
import com.silkysys.warehouse.sharedcode.domain.picking.startPickUpdateQuantity.entity.UpdateQuantityEntity
import com.silkysys.warehouse.app.ui.utility.ToastHelper.toast
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PickItemsActivity : BaseActivity() {

    // Properties
    private var isFirstTimeLoading = true
    private lateinit var startedItemsEntity: StartPickEntity

    private val savePref: SavePref by inject()          // koin inject
    private lateinit var mDialog: AlertDialog
    private lateinit var pickOrder: PickEntity.Fulfillments.Started
    private val startPickViewModel: StartPickViewModel by viewModel()
    private lateinit var mediaPlayer: MediaPlayer

    // Compose UI state
    private var screenTitle by mutableStateOf("")
    private var showStart by mutableStateOf(false)
    private var items by mutableStateOf<List<StartPickEntity.Fulfillment.Item>>(emptyList())
    private var selectedItem by mutableStateOf<StartPickEntity.Fulfillment.Item?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!requireAuthenticatedSession()) return
        pickOrder = intent.getParcelableExtra("startedList") ?: run {
            finish()
            return
        }
        mDialog = Utility.getProgressDialog(this)

        val isPicking = pickOrder.status == "picking"
        screenTitle = if (isPicking) pickOrder.name else getString(R.string.picking)
        showStart = !isPicking

        setContent {
            PickItemsScreen(
                title = screenTitle,
                items = items,
                showStart = showStart,
                onBackClick = ::openPickScreen,
                onScanClick = ::openBarcodeScanner,
                onStartClick = { startPickViewModel.startPick(savePref.companyId, pickOrder.id) },
                onItemClick = ::onItemClick,
            )
            selectedItem?.let { item ->
                PickQuantityDialog(
                    orderName = pickOrder.name,
                    item = item,
                    onDismiss = { selectedItem = null },
                    onSubmit = { quantity, hasIssue, note -> submitQuantity(item, quantity, hasIssue, note) },
                )
            }
        }

        onBackPressedDispatcher.addCallback(this) { openPickScreen() }
        // Call Methods
        observeStartPick()
        observeGetStartedList()
        observeUpdate()
        observeDialogUpdate()
        if (isPicking) {
            startPickViewModel.getStartedListStarted(savePref.companyId, pickOrder.id)
        }
    }

    private fun observeGetStartedList() {
        startPickViewModel.getStartedStatus.flowWithLifecycle(
            lifecycle,
            Lifecycle.State.STARTED
        ).onEach { state ->
            handleGetStartedListState(state)
        }.launchIn(lifecycleScope)
    }

    private fun handleGetStartedListState(state: GetStartedListStatus) {
        when (state) {
            is GetStartedListStatus.Init -> Unit
            is GetStartedListStatus.SuccessGetStartedList -> successGetStartedList(state.startedItemsEntity)
            is GetStartedListStatus.ErrorGetStartedList -> showErrorAlert(state.startPickResponse.toString())
            is GetStartedListStatus.SetStartedListLoading -> setGetListLoading(state.loading)
            is GetStartedListStatus.ShowStartedListToast -> {
                handleLoading(false)
                ErrorHandler.handleException(state.message, this@PickItemsActivity)
                //showErrorAlert(getString(R.string.api_request_error))
            }
        }
    }

    private fun setGetListLoading(loading: Boolean) {
        if (loading)
            mDialog.show()
        else
            mDialog.dismiss()
    }

    private fun successGetStartedList(startedItemsEntity: StartPickEntity) {
        this.startedItemsEntity = startedItemsEntity
        items = startedItemsEntity.fulfillment.items.toList()
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

    private fun onItemClick(item: StartPickEntity.Fulfillment.Item) {
        if (item.status != "picked") {
            selectedItem = item
        } else {
            toast(getString(R.string.item_picked))
        }
    }

    private fun submitQuantity(
        item: StartPickEntity.Fulfillment.Item,
        quantity: Int,
        hasIssue: Boolean,
        note: String,
    ) {
        startPickViewModel.updateDialogQuantity(
            savePref.companyId, pickOrder.id,
            UpdateQuantityRequest(
                item.id,
                item.quantity,
                item.picked_quantity + quantity,
                hasIssue,
                if (hasIssue) note else ""
            )
        )
    }

    private fun setScannedBarcode(rawValue: String) {
        startPickViewModel.updateQuantity(savePref.companyId, pickOrder.id, rawValue)
    }

    private fun observeUpdate() {
        startPickViewModel.quantityStatus.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state -> handleUpdate(state) }
            .launchIn(lifecycleScope)
    }

    private fun observeDialogUpdate() {
        startPickViewModel.dialogQuantityStatus.flowWithLifecycle(
            lifecycle,
            Lifecycle.State.STARTED
        )
            .onEach { dialogState -> handleDialogUpdate(dialogState) }
            .launchIn(lifecycleScope)
    }

    @SuppressLint("LongLogTag")
    private fun handleDialogUpdate(state: DialogQuantityStatus) {
        when (state) {
            is DialogQuantityStatus.Init -> Unit
            is DialogQuantityStatus.SuccessDialogQuantity -> handleSuccessDialogUpdate(state.updateQuantityEntity)
            is DialogQuantityStatus.ErrorDialogQuantity -> showErrorAlert(state.updateQuantityResponse.message)
            is DialogQuantityStatus.SetDialogLoading -> handleLoading(state.loading)
            is DialogQuantityStatus.ShowDialogToast -> {
                handleLoading(false)
                ErrorHandler.handleException(state.message, this@PickItemsActivity)
                //showErrorAlert(getString(R.string.api_request_error))
            }
        }
    }

    private fun handleSuccessDialogUpdate(updateQuantityEntity: UpdateDialogQuantityEntity) {
        selectedItem = null
        startPickViewModel.getStartedListStarted(savePref.companyId, pickOrder.id)
    }

    private fun handleUpdate(state: QuantityStatus) {
        when (state) {
            is QuantityStatus.Init -> Unit
            is QuantityStatus.SuccessSendQuantity -> handleSuccessUpdate(state.updateQuantityEntity)
            is QuantityStatus.ErrorSendQuantity -> handleErrorUpdate(state.updateQuantityResponse.message)
            is QuantityStatus.SetLoading -> handleLoading(state.loading)
            is QuantityStatus.ShowToast -> {
                handleLoading(false)
                ErrorHandler.handleException(state.message, this@PickItemsActivity)
                //showErrorAlert(getString(R.string.api_request_error))
            }
        }
    }

    private fun handleSuccessUpdate(updateQuantityEntity: UpdateQuantityEntity) {
        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.correct)
        mediaPlayer.start()
        startPickViewModel.getStartedListStarted(savePref.companyId, pickOrder.id)
    }

    private fun handleErrorUpdate(updateStatus: String) {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(200)
        }
        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.wrong)
        mediaPlayer.start()
        showErrorAlert(updateStatus)
    }

    private fun handleLoading(loading: Boolean) {
        if (loading)
            mDialog.show()
        else
            mDialog.dismiss()
    }

    private fun openPickScreen() {
        val intent = Intent(this, PickActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun observeStartPick() {
        startPickViewModel.startStatus.flowWithLifecycle(
            lifecycle, Lifecycle.State.STARTED
        )
            .onEach { state -> handleState(state) }
            .launchIn(lifecycleScope)
    }

    private fun handleState(state: StartStatus) {
        when (state) {
            is StartStatus.Init -> Unit
            is StartStatus.SuccessStart -> handleStart(state.startPickEntity)
            is StartStatus.ErrorStart -> showErrorAlert(state.startPickResponse.toString())
            is StartStatus.ShowToast -> {
                handleLoading(false)
                ErrorHandler.handleException(state.message, this@PickItemsActivity)
                //showErrorAlert(getString(R.string.api_request_error))
            }

            is StartStatus.SetLoading -> handleLoading(state.loading)

        }
    }

    private fun handleStart(startPickEntity: StartPickEntity) {
        showStart = false
        items = startPickEntity.fulfillment.items.toList()
    }

    private val barcodeLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val barcodeValue = BarcodeInputValidator.normalize(
                result.data?.getStringExtra(BarcodeScanningActivity.BARCODE_RESULT_KEY)
            )
            if (barcodeValue == null) {
                toast(getString(R.string.enter_valid_barcode))
                return@registerForActivityResult
            }
            toast(barcodeValue)
            setScannedBarcode(barcodeValue)

        } else
            toast(getString(R.string.error_in_scanning))
    }

    private fun openBarcodeScanner() {
        val intent = Intent(this, BarcodeScanningActivity::class.java)
        barcodeLauncher.launch(intent)
    }
}
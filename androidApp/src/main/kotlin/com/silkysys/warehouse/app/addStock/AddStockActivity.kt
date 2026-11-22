package com.silkysys.warehouse.app.addStock

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.InputType
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.silkysys.warehouse.R
import com.silkysys.warehouse.app.autentcation.main.MainViewModel
import com.silkysys.warehouse.app.autentcation.main.ProductInfoStatus
import com.silkysys.warehouse.app.autentcation.main.activity.MainActivity
import com.silkysys.warehouse.app.error_handle.ErrorHandler
import com.silkysys.warehouse.app.security.BarcodeInputValidator
import com.silkysys.warehouse.app.ui.activity.BaseActivity
import com.silkysys.warehouse.app.ui.compose_ui.addstock.AddStockFormState
import com.silkysys.warehouse.app.ui.compose_ui.addstock.AddStockScreen
import com.silkysys.warehouse.app.ui.compose_ui.addstock.BarcodeDialogContent
import com.silkysys.warehouse.app.ui.utility.BarcodeScanningActivity
import com.silkysys.warehouse.app.ui.utility.extension.customAlertDialog
import com.silkysys.warehouse.app.ui.utility.extension.showErrorAlert
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.domain.addstock.AddStockRequest
import com.silkysys.warehouse.sharedcode.domain.addstock.ProductInfoEntity
import com.silkysys.warehouse.sharedcode.domain.addstock.ProductInfoResponse
import com.silkysys.warehouse.app.ui.utility.ToastHelper.toast
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.silkysys.warehouse.app.ui.compose_ui.dialog_boxes.AppProgressDialog

class AddStockActivity : BaseActivity() {
    private var showProgress by mutableStateOf(false)
    private val form = AddStockFormState()
    private val addStockViewModel: AddStockViewModel by viewModel()
    private val mainViewModel: MainViewModel by viewModel()

    private var selectedWarehouse: String? = null
    private var selectedBinLocation: String? = null
    private var warehouseList: List<ProductInfoResponse.Warehouse> = emptyList()
    private var currentBins: List<ProductInfoResponse.Warehouse.BinLocation> = emptyList()
    private var mediaPlayer: MediaPlayer? = null
    private var lastBarcode: String? = null
    private var lastBarcodeAt = 0L
    var isMunBynPDA: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!requireAuthenticatedSession()) return

        form.itemCode = getString(R.string.not_active)
        form.itemName = getString(R.string.not_active)
        form.quantity = getString(R.string.zero)
        setContent {
            AddStockScreen(
                form = form,
                onWarehouseSelected = ::onWarehouseSelected,
                onBinSelected = ::onBinSelected,
                onBack = { navigateHome() },
                onScan = {
                    if (isMunBynPDA)
                        showScanDialog()
                    else
                        openBarcodeScanner()
                },
                onScanLongClick = { showSimpleInputDialog() },
                onUpdateStock = { updateStockData() },
            )

            if (showProgress) AppProgressDialog()
        }
        observeAddStock()
        observeVerifyProduct()
        isMunBynPDA = verifyMunBynPDA()

        onBackPressedDispatcher.addCallback(this) {
            navigateHome()
        }
    }

    fun verifyMunBynPDA(): Boolean {
        val model = Build.MODEL.lowercase()
        return model.contains("munbyn") ||
                model.contains("ipda") ||
                model.contains("035")
    }

    private fun handleLoading(loading: Boolean) {
        showProgress = loading && !isFinishing
    }

    private fun observeAddStock() {
        addStockViewModel.addStockStatus
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach(::handleAddStockStatus)
            .launchIn(lifecycleScope)
    }

    private fun observeVerifyProduct() {
        mainViewModel.productInfo
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach(::handleVerifyProductStatus)
            .launchIn(lifecycleScope)
    }

    private fun handleVerifyProductStatus(status: ProductInfoStatus) {
        when (status) {
            is ProductInfoStatus.Init -> Unit
            is ProductInfoStatus.IsLoading -> handleLoading(true)
            is ProductInfoStatus.SuccessProduct -> handleVerifyProductSuccess(status.productInfoEntity)
            is ProductInfoStatus.ErrorProduct -> handleVerifyProductError(status)
            is ProductInfoStatus.ShowToast -> {
                handleLoading(false)
                ErrorHandler.handleException(status.exception, this)
                mainViewModel.resetProductInfo()
            }
        }
    }

    private fun handleVerifyProductError(status: ProductInfoStatus.ErrorProduct) {
        handleLoading(false)
        mainViewModel.resetProductInfo()
        vibrateError()
        playSound(R.raw.wrong)
        showErrorAlert(status.productInfoResponse.message)
    }

    private fun handleVerifyProductSuccess(product: ProductInfoEntity) {
        handleLoading(false)
        mainViewModel.resetProductInfo()
        playSound(R.raw.correct)
        warehouseList = product.warehouses
        form.itemName = product.name
        form.imageUrl = product.image_url
        setupWarehouseSpinner(product)
    }

    private fun handleAddStockStatus(status: AddStockStatus) {
        when (status) {
            is AddStockStatus.Init -> Unit
            is AddStockStatus.SetLoading -> handleLoading(true)
            is AddStockStatus.SuccessAddStock -> handleAddStockSuccess(status.wrappedResponse)
            is AddStockStatus.ErrorAddStock -> {
                handleLoading(false)
                showErrorAlert(
                    status.errorResponse.message ?: getString(R.string.something_went_wrong)
                )
            }

            is AddStockStatus.ShowToast -> {
                handleLoading(false)
                ErrorHandler.handleException(status.message, this)
            }
        }
    }

    private fun handleAddStockSuccess(response: WrappedResponse<Unit>) {
        handleLoading(false)
        form.itemCode = getString(R.string.not_active)
        form.itemName = getString(R.string.not_active)
        form.quantity = "0"
        selectedWarehouse = null
        selectedBinLocation = null
        form.warehouseNames.clear()
        form.binNames.clear()
        form.imageUrl = null
        customAlertDialog(getString(R.string.success), response.message)
        addStockViewModel.resetStatus()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            navigateHome()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navigateHome() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private val barcodeLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getStringExtra("BARCODE_VALUE")?.let(::acceptBarcode)
                    ?: toast(getString(R.string.error_in_scanning))
            } else {
                toast(getString(R.string.error_in_scanning))
            }
        }

    private fun openBarcodeScanner() {
        barcodeLauncher.launch(Intent(this, BarcodeScanningActivity::class.java))
    }

    private fun acceptBarcode(value: String) {
        val normalized = BarcodeInputValidator.normalize(value) ?: return
        val now = System.currentTimeMillis()
        if (normalized == lastBarcode && now - lastBarcodeAt < BARCODE_DEBOUNCE_MS) return
        lastBarcode = normalized
        lastBarcodeAt = now
        verifyProductFromAPI(normalized)
    }

    private fun updateStockData() {
        val quantity = form.quantity.trim().toIntOrNull()
        when {
            form.itemCode == getString(R.string.not_active) ->
                customAlertDialog(null, getString(R.string.scan_item_barcode_first_to_proceed))

            quantity == null || quantity <= 0 ->
                customAlertDialog(null, getString(R.string.add_item_quantity_to_proceed))

            selectedWarehouse == null ->
                customAlertDialog(null, getString(R.string.select_warehouse_to_proceed))

            else -> AlertDialog.Builder(this)
                .setTitle(getString(R.string.add_stock))
                .setMessage(getString(R.string.do_you_want_to_add_this_stock))
                .setPositiveButton(R.string.yes) { _, _ ->
                    addStockViewModel.addStock(
                        AddStockRequest(
                            selectedWarehouse,
                            selectedBinLocation,
                            form.itemCode.trim(),
                            quantity.toString(),
                        ),
                    )
                }
                .setNegativeButton(R.string.no, null)
                .show()
        }
    }

    private fun setupWarehouseSpinner(product: ProductInfoEntity) {
        val names = arrayListOf(getString(R.string.select_warehouse_prompt)).apply {
            addAll(product.warehouses.map { it.name })
        }
        form.warehouseNames.clear()
        form.warehouseNames.addAll(names)
        // a Spinner selects its first item as soon as it gets an adapter
        onWarehouseSelected(0)
    }

    // spnWarehouse.onItemSelectedListener
    private fun onWarehouseSelected(position: Int) {
        form.selectedWarehouseIndex = position
        if (position in 1..warehouseList.size) {
            val warehouse = warehouseList[position - 1]
            selectedWarehouse = warehouse.id.toString()
            setupBinLocationSpinner(warehouse.bin_locations)
        } else {
            selectedWarehouse = null
            selectedBinLocation = null
            form.binNames.clear()
        }
    }

    private fun setupBinLocationSpinner(bins: List<ProductInfoResponse.Warehouse.BinLocation>) {
        currentBins = bins
        form.binNames.clear()
        form.binNames.add(if (bins.isEmpty()) getString(R.string.no_items_available) else getString(R.string.select_bin_prompt))
        form.binNames.addAll(bins.map { it.code })
        onBinSelected(0)
    }

    // spnBin.onItemSelectedListener
    private fun onBinSelected(position: Int) {
        form.selectedBinIndex = position
        selectedBinLocation = currentBins.getOrNull(position - 1)?.id?.toString()
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }

    private fun verifyProductFromAPI(sku: String) {
        form.itemCode = sku
        mainViewModel.getProductInfo(sku)
    }

    private fun showSimpleInputDialog() {
        val input = EditText(this).apply {
            setPadding(50, 40, 50, 40)
            inputType = InputType.TYPE_CLASS_TEXT
        }
        AlertDialog.Builder(this)
            .setMessage(R.string.enter_your_code)
            .setView(input)
            .setPositiveButton(R.string.verify) { _, _ ->
                input.text.toString().trim().takeIf { it.isNotEmpty() }?.let(::acceptBarcode)
                    ?: Toast.makeText(this, R.string.please_enter_code, Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.close, null)
            .show()
        input.requestFocus()
    }

    private fun playSound(resource: Int) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(applicationContext, resource)?.apply {
            setOnCompletionListener { player ->
                player.release(); if (mediaPlayer === player) mediaPlayer = null
            }
            start()
        }
    }

    @Suppress("DEPRECATION")
    private fun vibrateError() {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(200)
        }
    }

    companion object {
        private const val BARCODE_DEBOUNCE_MS = 750L
    }

    fun showScanDialog() {
        val builder = AlertDialog.Builder(this)
        lateinit var scanDialog: AlertDialog
        val dialogLayout = ComposeView(this).apply {
            // the dialog window is outside the activity's view tree, so give the ComposeView its owners
            setViewTreeLifecycleOwner(this@AddStockActivity)
            setViewTreeSavedStateRegistryOwner(this@AddStockActivity)
            setContent {
                BarcodeDialogContent(
                    onBarcodeEntered = { text ->
                        verifyProductFromAPI(text)
                        scanDialog.dismiss()
                    },
                )
            }
        }
        builder.setView(dialogLayout)

        builder.setNegativeButton("Close") { dialog, _ ->
            dialog.dismiss()
        }

        builder.setPositiveButton("Enter Code") { dialog, _ ->
            dialog.dismiss()
            showSimpleInputDialog()
        }

        scanDialog = builder.create()
        scanDialog.show()
    }
}
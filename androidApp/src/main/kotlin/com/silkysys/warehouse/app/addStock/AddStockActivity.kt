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
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.size.Scale
import com.silkysys.warehouse.R
import com.silkysys.warehouse.app.autentcation.main.MainViewModel
import com.silkysys.warehouse.app.autentcation.main.ProductInfoStatus
import com.silkysys.warehouse.app.autentcation.main.activity.MainActivity
import com.silkysys.warehouse.app.error_handle.ErrorHandler
import com.silkysys.warehouse.app.security.BarcodeInputValidator
import com.silkysys.warehouse.app.ui.activity.BaseActivity
import com.silkysys.warehouse.app.ui.utility.BarcodeScanningActivity
import com.silkysys.warehouse.app.ui.utility.Utility
import com.silkysys.warehouse.app.ui.utility.extension.customAlertDialog
import com.silkysys.warehouse.app.ui.utility.extension.showErrorAlert
import com.silkysys.warehouse.databinding.ActivityAddStockBinding
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.domain.addstock.AddStockRequest
import com.silkysys.warehouse.sharedcode.domain.addstock.ProductInfoEntity
import com.silkysys.warehouse.sharedcode.domain.addstock.ProductInfoResponse
import com.silkysys.warehouse.wms.app.ui.utility.ToastHelper.toast
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddStockActivity : BaseActivity() {
    private lateinit var progressDialog: AlertDialog
    private lateinit var binding: ActivityAddStockBinding
    private val addStockViewModel: AddStockViewModel by viewModel()
    private val mainViewModel: MainViewModel by viewModel()

    private var selectedWarehouse: String? = null
    private var selectedBinLocation: String? = null
    private var warehouseList: List<ProductInfoResponse.Warehouse> = emptyList()
    private var adapterBin: ArrayAdapter<String?>? = null
    private val binList = mutableListOf<String?>()
    private var mediaPlayer: MediaPlayer? = null
    private var lastBarcode: String? = null
    private var lastBarcodeAt = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!requireAuthenticatedSession()) return

        progressDialog = Utility.getProgressDialog(this)
        binding = ActivityAddStockBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupClickListeners()
        observeAddStock()
        observeVerifyProduct()

    }

    private fun handleLoading(loading: Boolean) {
        if (loading && !isFinishing) progressDialog.show() else if (progressDialog.isShowing) progressDialog.dismiss()
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
        binding.tvItemName.text = product.name
        binding.prodImage.load(product.image_url) {
            scale(Scale.FIT)
            placeholder(R.drawable.placeholder_view)
            error(R.drawable.placeholder_view)
        }
        setupWarehouseSpinner(product)
    }

    private fun handleAddStockStatus(status: AddStockStatus) {
        when (status) {
            is AddStockStatus.Init -> Unit
            is AddStockStatus.SetLoading -> handleLoading(true)
            is AddStockStatus.SuccessAddStock -> handleAddStockSuccess(status.wrappedResponse)
            is AddStockStatus.ErrorAddStock -> {
                handleLoading(false)
                showErrorAlert(status.errorResponse.message ?: getString(R.string.something_went_wrong))
            }
            is AddStockStatus.ShowToast -> {
                handleLoading(false)
                ErrorHandler.handleException(status.message, this)
            }
        }
    }

    private fun handleAddStockSuccess(response: WrappedResponse<Unit>) {
        handleLoading(false)
        binding.tvItemcode.text = getString(R.string.not_active)
        binding.tvItemName.text = getString(R.string.not_active)
        binding.etQty.setText("0")
        selectedWarehouse = null
        selectedBinLocation = null
        binding.spnWarehouse.adapter = null
        binding.spnBin.adapter = null
        binding.prodImage.setImageResource(R.drawable.placeholder_view)
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

    private fun setupClickListeners() {
        binding.btStartScan.setOnClickListener { openBarcodeScanner() }
        binding.btStartScan.setOnLongClickListener {
            showSimpleInputDialog()
            true
        }
        binding.ivBack.setOnClickListener { navigateHome() }
        binding.btUpdateStock.setOnClickListener { updateStockData() }
    }

    @Deprecated("Deprecated by Android")
    override fun onBackPressed() = navigateHome()

    private fun navigateHome() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private val barcodeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
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
        val quantity = binding.etQty.text.toString().trim().toIntOrNull()
        when {
            binding.tvItemcode.text == getString(R.string.not_active) ->
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
                            binding.tvItemcode.text.toString().trim(),
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
        binding.spnWarehouse.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            names,
        ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        adapterBin = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, binList)
        binding.spnBin.adapter = adapterBin
        binding.spnWarehouse.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position in 1..warehouseList.size) {
                    val warehouse = warehouseList[position - 1]
                    selectedWarehouse = warehouse.id.toString()
                    setupBinLocationSpinner(warehouse.bin_locations)
                } else {
                    selectedWarehouse = null
                    selectedBinLocation = null
                    binList.clear()
                    adapterBin?.notifyDataSetChanged()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }

    private fun setupBinLocationSpinner(bins: List<ProductInfoResponse.Warehouse.BinLocation>) {
        binList.clear()
        binList.add(if (bins.isEmpty()) getString(R.string.no_items_available) else getString(R.string.select_bin_prompt))
        binList.addAll(bins.map { it.code })
        adapterBin?.notifyDataSetChanged()
        binding.spnBin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedBinLocation = bins.getOrNull(position - 1)?.id?.toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
        binding.spnBin.setSelection(0)
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        if (::progressDialog.isInitialized && progressDialog.isShowing) progressDialog.dismiss()
        super.onDestroy()
    }

    private fun verifyProductFromAPI(sku: String) {
        binding.tvItemcode.text = sku
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
            setOnCompletionListener { player -> player.release(); if (mediaPlayer === player) mediaPlayer = null }
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
}

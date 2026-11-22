package com.silkysys.warehouse.app.putAway.putAwayProductDetails.activity

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
import com.silkysys.warehouse.app.putAway.putAway.activity.PutAwayActivity
import com.silkysys.warehouse.app.putAway.putAwayProductDetails.DeleteStatus
import com.silkysys.warehouse.app.putAway.putAwayProductDetails.PutAwayProductDetailsStatus
import com.silkysys.warehouse.app.putAway.putAwayProductDetails.PutAwayProductDetailsViewModel
import com.silkysys.warehouse.app.security.BarcodeInputValidator
import com.silkysys.warehouse.app.ui.activity.BaseActivity
import com.silkysys.warehouse.app.ui.compose_ui.dialog_boxes.AppAlertDialog
import com.silkysys.warehouse.app.ui.compose_ui.product_details.ProductDetailsScreen
import com.silkysys.warehouse.app.ui.utility.BarcodeScanningActivity
import com.silkysys.warehouse.app.ui.utility.SavePref
import com.silkysys.warehouse.app.ui.utility.ToastHelper.toast
import com.silkysys.warehouse.app.ui.utility.Utility
import com.silkysys.warehouse.app.ui.utility.extension.showErrorAlert
import com.silkysys.warehouse.data.putAwayModule.deletePinLocation.dto.DeleteLocationRequest
import com.silkysys.warehouse.sharedcode.data.putAwayModule.putAwayProductDetails.dto.PutAwayProductDetailsRequest
import com.silkysys.warehouse.sharedcode.domain.putAwayModule.deletePinLocation.entity.DeleteLocationEntity
import com.silkysys.warehouse.sharedcode.domain.putAwayModule.putAway.entity.PutAwayScanEntity
import com.silkysys.warehouse.sharedcode.domain.putAwayModule.putAwayProductDetails.entity.PutAwayProductDetailsEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProductDetailsActivity : BaseActivity() {

    private lateinit var mDialog: AlertDialog
    lateinit var product: PutAwayScanEntity.Item
    private val putAwayProductDetailsViewModel: PutAwayProductDetailsViewModel by viewModel()

    // Compose UI state
    private var racks by mutableStateOf<List<PutAwayScanEntity.Item.BinLocations>>(emptyList())
    private var rackToUnassign by mutableStateOf<PutAwayScanEntity.Item.BinLocations?>(null)
    private lateinit var mediaPlayer: MediaPlayer
    private val  savePref: SavePref by inject()
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!requireAuthenticatedSession()) return
        mDialog = Utility.getProgressDialog(this)
        product = intent.getParcelableExtra("product") ?: run {
            finish()
            return
        }
        racks = product.bin_locations.orEmpty().toList()

        setContent {
            ProductDetailsScreen(
                product = product,
                racks = racks,
                onBackClick = ::openPutAwayScreen,
                onAddLocationClick = ::openBarcodeScanner,
                onDeleteRackClick = { rackToUnassign = it },
            )
            rackToUnassign?.let { rack ->
                AppAlertDialog(
                    title = getString(R.string.unassign_pin_location),
                    message = getString(R.string.confirmation_unassign_message),
                    confirmText = getString(R.string.ok),
                    dismissText = getString(R.string.cancel),
                    onConfirm = {
                        rackToUnassign = null
                        putAwayProductDetailsViewModel.deleteLocation(
                            savePref.companyId, DeleteLocationRequest(
                                product.id,
                                rack.id
                            )
                        )
                    },
                    onDismiss = { rackToUnassign = null },
                )
            }
        }

        /// Setup Methods
        observeAssign()
        observeUnassign()
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



    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this,PutAwayActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun openPutAwayScreen() {
        val intent = Intent(this,PutAwayActivity::class.java)
        startActivity(intent)
        finish()
    }
    /**barcode**

    private fun openActivity(){


    val launchIntent = BarcodeReaderActivity.getLaunchIntent(this, true, false)
    launchIntent.putExtra("type","addLocation")
    startActivityForResult(launchIntent, BarcodeReaderActivity.BARCODE_READER_ACTIVITY_REQUEST)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode != Activity.RESULT_OK) {
    toast(getString(R.string.error_in_scanning))
    return
    }
    if(requestCode == BarcodeReaderActivity.BARCODE_READER_ACTIVITY_REQUEST
    && data != null) {
    val barcode = data.getParcelableExtra<Barcode>(BarcodeReaderActivity.KEY_CAPTURED_BARCODE)
    if (barcode == null) {
    val barcodeString= data.getStringExtra((BarcodeReaderActivity.KEY_CAPTURED_BARCODE))
    toast(barcodeString!!)
    assignLocation(barcodeString)
    } else {
    barcode.let {
    toast(it.rawValue)
    assignLocation(it.rawValue)
    }
    }
    }
    }
     */
    private fun assignLocation(barcode: String) {
        putAwayProductDetailsViewModel.assignProductToBinLocation(
            savePref.companyId,
            PutAwayProductDetailsRequest(product.barcode, barcode.trim())
        )
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
            assignLocation(barcodeValue)

        } else
            toast(getString(R.string.error_in_scanning))
    }

    private fun openBarcodeScanner() {
        val intent = Intent(this, BarcodeScanningActivity::class.java)
        barcodeLauncher.launch(intent)
    }


    private fun observeAssign() {
        putAwayProductDetailsViewModel.assignStatus
            .flowWithLifecycle(lifecycle,Lifecycle.State.STARTED)
            .onEach { state -> handleState(state) }
            .launchIn(lifecycleScope)
    }
    private fun observeUnassign() {
        putAwayProductDetailsViewModel.deleteStatus
            .flowWithLifecycle(lifecycle,Lifecycle.State.STARTED)
            .onEach { state -> handleUnassignState(state) }
            .launchIn(lifecycleScope)
    }

    private fun handleState(state: PutAwayProductDetailsStatus) {
        when(state){
            is PutAwayProductDetailsStatus.Init -> Unit
            is PutAwayProductDetailsStatus.SuccessAssign -> handleSuccess(state.putAwayProductDetailsEntity)
            is PutAwayProductDetailsStatus.ErrorAssign -> handleErrorAssign(state.putAwayProductDetailsResponse.message)
            is PutAwayProductDetailsStatus.SetLoading -> handleLoading(state.loading)
            is PutAwayProductDetailsStatus.ShowToast -> {
                handleLoading(false)
                //showErrorAlert(getString(R.string.api_request_error))
                ErrorHandler.handleException(state.message, this@ProductDetailsActivity)

            }
        }

    }

    private fun handleErrorAssign(putAwayStatus: String) {
        val vibrator= getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >=26){
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        }else{
            vibrator.vibrate(200)
        }
        mediaPlayer= MediaPlayer.create(applicationContext,R.raw.wrong)
        mediaPlayer.start()
        showErrorAlert(putAwayStatus)
    }
    private fun handleUnassignState(state: DeleteStatus) {
        when(state){
            is DeleteStatus.Init -> Unit
            is DeleteStatus.SuccessDelete -> handleUnassignSuccess(state.deleteLocationEntity)
            is DeleteStatus.ErrorDeleteLocation -> showErrorAlert(state.deleteLocationResponse.message)
            is DeleteStatus.SetDeleteLoading -> handleLoading(state.loading)
            is DeleteStatus.ShowDeleteToast -> {
                handleLoading(false)
                //showErrorAlert(getString(R.string.api_request_error))
                ErrorHandler.handleException(state.message, this@ProductDetailsActivity)
            }
        }
    }
    private fun handleUnassignSuccess(deleteLocationEntity: DeleteLocationEntity) {
        racks = deleteLocationEntity.binLocations.map { item ->
            PutAwayScanEntity.Item.BinLocations(
                item.id, item.company_id, item.warehouse_id, item.code
            )
        }
    }

    private fun handleLoading(loading: Boolean) {
        if (loading)
            mDialog.show()
        else
            mDialog.dismiss()
    }
    private fun handleSuccess(putAwayProductDetailsEntity: PutAwayProductDetailsEntity) {
        mediaPlayer= MediaPlayer.create(applicationContext,R.raw.correct)
        mediaPlayer.start()
        racks = putAwayProductDetailsEntity.binLocations.toList()
    }
}
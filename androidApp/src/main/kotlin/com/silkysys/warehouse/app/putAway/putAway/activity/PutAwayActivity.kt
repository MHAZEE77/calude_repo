package com.silkysys.warehouse.app.putAway.putAway.activity


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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.silkysys.warehouse.R
import com.silkysys.warehouse.app.error_handle.ErrorHandler
import com.silkysys.warehouse.app.autentcation.main.activity.MainActivity
import com.silkysys.warehouse.app.putAway.putAway.PutAwayStatus
import com.silkysys.warehouse.app.putAway.putAway.PutAwayViewModel
import com.silkysys.warehouse.app.putAway.putAwayProductDetails.activity.ProductDetailsActivity
import com.silkysys.warehouse.app.security.BarcodeInputValidator
import com.silkysys.warehouse.app.ui.activity.BaseActivity
import com.silkysys.warehouse.app.ui.utility.BarcodeScanningActivity
import com.silkysys.warehouse.app.ui.utility.SavePref
import com.silkysys.warehouse.app.ui.utility.Utility
import com.silkysys.warehouse.app.ui.utility.extension.showErrorAlert
import com.silkysys.warehouse.databinding.ActivityPutAwayBinding
import com.silkysys.warehouse.sharedcode.domain.putAwayModule.putAway.entity.PutAwayScanEntity
import com.silkysys.warehouse.wms.app.ui.utility.ToastHelper.toast
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PutAwayActivity : BaseActivity() {


    private val savePref: SavePref by inject()
    private lateinit var mDialog: AlertDialog
    private lateinit var binding: ActivityPutAwayBinding
    private val putAwayViewModel: PutAwayViewModel by viewModel()
    private lateinit var mediaPlayer: MediaPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!requireAuthenticatedSession()) return
        mDialog = Utility.getProgressDialog(this)
        binding = ActivityPutAwayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Setup Methods
        setupClickListeners()
        setupEditText()
        observePutAwayScan()
    }

    private fun observePutAwayScan() {
        putAwayViewModel.putAwayStatus.flowWithLifecycle(
            lifecycle,Lifecycle.State.STARTED
        ).onEach { putAwayStatus -> handlePutAwayStatus(putAwayStatus)
        }.launchIn(lifecycleScope)
    }

    private fun handlePutAwayStatus(putAwayStatus: PutAwayStatus) {
        when(putAwayStatus){
            is PutAwayStatus.Init -> Unit
            is PutAwayStatus.SuccessPutAway -> handlePutAwaySuccess(putAwayStatus.putAwayScanEntity)
            is PutAwayStatus.ErrorPutAway -> handleErrorPutAway(putAwayStatus)
            is PutAwayStatus.ShowToast -> {
                handleLoading(false)
                //showErrorAlert(getString(R.string.api_request_error))
                ErrorHandler.handleException(putAwayStatus.throwable, this@PutAwayActivity)
            }
            is PutAwayStatus.SetLoading -> handleLoading(putAwayStatus.loading)
        }
    }

    private fun handleErrorPutAway(putAwayStatus: PutAwayStatus.ErrorPutAway) {
        val vibrator= getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >=26){
            vibrator.vibrate(VibrationEffect.createOneShot(200,VibrationEffect.DEFAULT_AMPLITUDE))
        }else{
            vibrator.vibrate(200)
        }
        mediaPlayer= MediaPlayer.create(applicationContext,R.raw.wrong)
        mediaPlayer.start()
        showErrorAlert(putAwayStatus.putAwayScanResponse.message)
    }

    private fun handleLoading(loading: Boolean) {
        if (loading)
            mDialog.show()
        else
            mDialog.dismiss()
    }

    private fun handlePutAwaySuccess(putAwayScanEntity: PutAwayScanEntity) {
        mediaPlayer= MediaPlayer.create(applicationContext,R.raw.correct)
        mediaPlayer.start()
        val product = putAwayScanEntity.item
        val intent= Intent(this, ProductDetailsActivity::class.java)
        intent.putExtra("product",product)
        startActivity(intent)
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

    private fun setupClickListeners() {
        binding.btStartScan.setOnClickListener {

            openBarcodeScanner()

        }
        binding.ivBack.setOnClickListener {
            val i =Intent(this,MainActivity::class.java)
            startActivity(i)
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val i =Intent(this,MainActivity::class.java)
        startActivity(i)
        finish()
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    private fun setupEditText() {
//        binding.searchEditText.setOnDebounceTextWatcher(lifecycle) { input ->
//            if (input.isNotEmpty())
//                setScannedBarcode(input)
//        }
    }

/**barcode**
    private fun openActivity(){
        val launchIntent = BarcodeReaderActivity.getLaunchIntent(this, true, false)
        launchIntent.putExtra("type","putAway")
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
                setScannedBarcode(barcodeString)
            } else {
                barcode.let {
                    toast(it.rawValue)
                    setScannedBarcode(it.rawValue)
                }
            }
        }
    }

    */


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

    private fun setScannedBarcode(code: String) {
         requestFetchItemApi(code)
    }

    private fun requestFetchItemApi(sku: String) {
        putAwayViewModel.putAwayScan(sku.trim())
    }
}

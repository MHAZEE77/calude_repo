package com.silkysys.warehouse.app.ui.utility

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.silkysys.warehouse.R
import com.silkysys.warehouse.app.security.BarcodeInputValidator
import com.silkysys.warehouse.app.ui.activity.BaseActivity
import com.silkysys.warehouse.databinding.ActivityBarcodeScanningBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class BarcodeScanningActivity : BaseActivity() {

    private lateinit var binding: ActivityBarcodeScanningBinding
    private lateinit var cameraExecutor: ExecutorService
    private var barcodeScanner: BarcodeScanner? = null
    private var isScanning = false

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        if (isGranted) startCamera() else showManualEntryMode()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!requireAuthenticatedSession()) return

        binding = ActivityBarcodeScanningBinding.inflate(layoutInflater)
        setContentView(binding.root)
        cameraExecutor = Executors.newSingleThreadExecutor()
        barcodeScanner = BarcodeScanning.getClient()

        binding.barcodeOk.setOnClickListener {
            submitBarcode(binding.etBarcode.text?.toString())
        }
        binding.backButton.setOnClickListener { finish() }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        ProcessCameraProvider.getInstance(this).addListener({
            runCatching {
                val cameraProvider = ProcessCameraProvider.getInstance(this).get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }
                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysis -> analysis.setAnalyzer(cameraExecutor, ::processImage) }

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalysis,
                )
            }.onFailure { showManualEntryMode() }
        }, ContextCompat.getMainExecutor(this))
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processImage(imageProxy: ImageProxy) {
        if (isScanning) {
            imageProxy.close()
            return
        }
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        barcodeScanner?.process(inputImage)
            ?.addOnSuccessListener { barcodes ->
                BarcodeInputValidator.normalize(barcodes.firstOrNull()?.rawValue)?.let { value ->
                    isScanning = true
                    sendResults(value)
                }
            }
            ?.addOnCompleteListener { imageProxy.close() }
            ?: imageProxy.close()
    }

    private fun showManualEntryMode() {
        binding.previewView.visibility = View.GONE
        binding.scanningFrame.visibility = View.GONE
        binding.instructionText.setText(R.string.camera_permission_manual_entry)
        binding.etBarcode.requestFocus()
    }

    private fun submitBarcode(rawValue: String?) {
        val value = BarcodeInputValidator.normalize(rawValue)
        if (value == null) {
            binding.etBarcode.error = getString(R.string.enter_valid_barcode)
            return
        }
        sendResults(value)
    }

    private fun sendResults(barcodeValue: String) {
        setResult(
            RESULT_OK,
            Intent().putExtra(BARCODE_RESULT_KEY, barcodeValue),
        )
        finish()
    }

    override fun onDestroy() {
        barcodeScanner?.close()
        if (::cameraExecutor.isInitialized) cameraExecutor.shutdown()
        super.onDestroy()
    }

    companion object {
        const val BARCODE_RESULT_KEY = "BARCODE_VALUE"
    }
}

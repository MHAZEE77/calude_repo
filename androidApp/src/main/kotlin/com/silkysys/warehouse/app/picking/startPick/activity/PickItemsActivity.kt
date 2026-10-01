package com.silkysys.warehouse.app.picking.startPick.activity


import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.size.Scale
import com.google.android.material.textfield.TextInputLayout
import com.silkysys.warehouse.R
import com.silkysys.warehouse.app.error_handle.ErrorHandler
import com.silkysys.warehouse.app.picking.pick.activity.PickActivity
import com.silkysys.warehouse.app.picking.startPick.DialogQuantityStatus
import com.silkysys.warehouse.app.picking.startPick.GetStartedListStatus
import com.silkysys.warehouse.app.picking.startPick.QuantityStatus
import com.silkysys.warehouse.app.picking.startPick.StartPickViewModel
import com.silkysys.warehouse.app.picking.startPick.StartStatus
import com.silkysys.warehouse.app.picking.startPick.adapter.PickItemsAdapter
import com.silkysys.warehouse.app.security.BarcodeInputValidator
import com.silkysys.warehouse.app.ui.activity.BaseActivity
import com.silkysys.warehouse.app.ui.utility.BarcodeScanningActivity
import com.silkysys.warehouse.app.ui.utility.SavePref
import com.silkysys.warehouse.app.ui.utility.Utility
import com.silkysys.warehouse.app.ui.utility.extension.showErrorAlert
import com.silkysys.warehouse.data.picking.startPickUpdateDialogQuantity.dto.UpdateQuantityRequest
import com.silkysys.warehouse.databinding.ActivityPickItemsBinding
import com.silkysys.warehouse.sharedcode.domain.picking.pick.entity.PickEntity
import com.silkysys.warehouse.sharedcode.domain.picking.startPick.entity.StartPickEntity
import com.silkysys.warehouse.sharedcode.domain.picking.startPickUpdateDialogQuantity.entity.UpdateDialogQuantityEntity
import com.silkysys.warehouse.sharedcode.domain.picking.startPickUpdateQuantity.entity.UpdateQuantityEntity
import com.silkysys.warehouse.wms.app.ui.utility.ToastHelper.toast
import com.silkysys.warehouse.wms.app.ui.utility.alert
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PickItemsActivity : BaseActivity() {

    // Properties
    private var isFirstTimeLoading = true
    private lateinit var mOnClickListener: View.OnClickListener
    private lateinit var startedItemsEntity: StartPickEntity

    private val savePref: SavePref by inject()          // koin inject
    private lateinit var binding: ActivityPickItemsBinding
    private lateinit var mDialog: AlertDialog
    private lateinit var adapter: PickItemsAdapter
    private lateinit var pickOrder: PickEntity.Fulfillments.Started
    private val startPickViewModel: StartPickViewModel by viewModel()
    private lateinit var rootDialog: Dialog
    private lateinit var mediaPlayer: MediaPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!requireAuthenticatedSession()) return
        pickOrder = intent.getParcelableExtra("startedList") ?: run {
            finish()
            return
        }
        mDialog = Utility.getProgressDialog(this)
        binding = ActivityPickItemsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Call Methods
        setupRecyclerView()
        setupClickListeners()
        observeStartPick()
        observeGetStartedList()
        observeUpdate()
        observeDialogUpdate()
        if (pickOrder.status == "picking") {
            binding.startPickOrderContainer.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
            binding.tvName.text = pickOrder.name
            startPickViewModel.getStartedListStarted(savePref.companyId, pickOrder.id)
        } else {
            binding.tvName.text = getString(R.string.picking)
            binding.startPickOrderContainer.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        }
        // Bind ViewModel
//        bindViewModel()
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
        adapter = PickItemsAdapter(startedItemsEntity.fulfillment.items, mOnClickListener)
        binding.recyclerView.apply {
            val linearLayoutManager = LinearLayoutManager(context)
            layoutManager = linearLayoutManager
            adapter = this@PickItemsActivity.adapter
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

    private fun setupRecyclerView() {
        mOnClickListener = View.OnClickListener { view ->
            val item = view.tag as StartPickEntity.Fulfillment.Item
//                ItemsDetailsActivity.startActivity(this, pickOrder, item)
            if (item.status != "picked") {
                rootDialog = Dialog(this)
                rootDialog.setContentView(R.layout.pick_pop_up)
                val itemImage = rootDialog.findViewById<ImageView>(R.id.imageView)
                val increase = rootDialog.findViewById<ImageView>(R.id.iv_increase)
                val decrease = rootDialog.findViewById<ImageView>(R.id.iv_decrease)
                val quantity = rootDialog.findViewById<TextView>(R.id.tv_quantity)
                val name = rootDialog.findViewById<TextView>(R.id.nameTextView)
                val title = rootDialog.findViewById<TextView>(R.id.tv_pickPopTitle)
                val subtitle = rootDialog.findViewById<TextView>(R.id.subtitleTextView)
                val remain = rootDialog.findViewById<TextView>(R.id.remainTextView)
                val picked = rootDialog.findViewById<TextView>(R.id.pickedTextView)
                val status = rootDialog.findViewById<TextView>(R.id.statusCardView)
                val note = rootDialog.findViewById<EditText>(R.id.et_note)
                val noteLayout = rootDialog.findViewById<TextInputLayout>(R.id.usernameEditText)
                val issueCB = rootDialog.findViewById<CheckBox>(R.id.cb_issue)
                val submitQuantity = rootDialog.findViewById<Button>(R.id.bt_submitQuantity)
                title.text = pickOrder.name
                name.text = item.name
                subtitle.text = item.description ?: "Item description"
                remain.text = (item.quantity - item.picked_quantity).toString()
                picked.text = item.picked_quantity.toString()
                status.text = item.status
                increase.setOnClickListener {
                    when {
                        quantity.text.toString().toInt() == remain.text.toString().toInt() -> {

                        }

                        else -> quantity.text = (quantity.text.toString().toInt() + 1).toString()
                    }
                }
                decrease.setOnClickListener {
                    when {
                        quantity.text.toString().toInt() > 1 -> quantity.text =
                            (quantity.text.toString().toInt() - 1).toString()
                    }
                }
                issueCB.setOnCheckedChangeListener { _, b ->
                    if (b)
                        noteLayout.visibility = View.VISIBLE
                    else
                        noteLayout.visibility = View.GONE
                }
                submitQuantity.setOnClickListener {
                    when {
                        issueCB.isChecked -> {
                            startPickViewModel.updateDialogQuantity(
                                savePref.companyId, pickOrder.id,
                                UpdateQuantityRequest(
                                    item.id,
                                    item.quantity,
                                    item.picked_quantity + quantity.text.toString().toInt(),
                                    true,
                                    note.text.toString()
                                )
                            )
                        }

                        else -> {
                            startPickViewModel.updateDialogQuantity(
                                savePref.companyId, pickOrder.id,
                                UpdateQuantityRequest(
                                    item.id, item.quantity,
                                    item.picked_quantity + quantity.text.toString().toInt(),
                                    false, ""
                                )
                            )
                        }
                    }
                }

                itemImage.load(item.image) {
                    scale(Scale.FIT)
                    placeholder(R.drawable.image_placeholder)
                    error(R.drawable.image_placeholder)
                }

                rootDialog.window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                rootDialog.window?.attributes?.windowAnimations = R.style.animation
                rootDialog.show()
            } else {
                toast(getString(R.string.item_picked))
//                showSuccessAlert(getString(R.string.item_picked))
            }
        }

        binding.searchEditText.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                adapter.filter(s.toString())
            }
        })
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
        rootDialog.dismiss()
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

    private fun setupClickListeners() {
        binding.civScan.setOnClickListener {
            openBarcodeScanner()
        }
        binding.ivBack.setOnClickListener {
            val intent = Intent(this, PickActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.finishButton.setOnClickListener {
            alert {
                message = getString(R.string.sure_to_finish_pick_order_alert)
                positiveButton(android.R.string.yes.toString()) {
//                    viewModel.finishPickOrder()
                }
                negativeButton(android.R.string.no.toString()) { }
            }.show()
        }

        binding.startButton.setOnClickListener {
//            alert {
//                message = getString(R.string.sure_to_start_pick_order)
//                positiveButton(android.R.string.yes) {
            startPickViewModel.startPick(savePref.companyId, pickOrder.id)
//                }
//                negativeButton(android.R.string.no) { }
//            }.show()
        }
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
        binding.startPickOrderContainer.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
        adapter = PickItemsAdapter(startPickEntity.fulfillment.items, mOnClickListener)
        binding.recyclerView.apply {
            val linearLayoutManager = LinearLayoutManager(context)
            layoutManager = linearLayoutManager
            adapter = this@PickItemsActivity.adapter

        }
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


    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, PickActivity::class.java)
        startActivity(intent)
        finish()
    }
}

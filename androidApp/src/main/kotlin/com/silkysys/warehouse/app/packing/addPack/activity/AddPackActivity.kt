package com.silkysys.warehouse.app.packing.addPack.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.silkysys.warehouse.R
import com.silkysys.warehouse.app.error_handle.ErrorHandler
import com.silkysys.warehouse.app.packing.addPack.AddPackStatus
import com.silkysys.warehouse.app.packing.addPack.AddPackViewModel
import com.silkysys.warehouse.app.packing.addPack.PackItemsStatus
import com.silkysys.warehouse.app.ui.activity.BaseActivity
import com.silkysys.warehouse.app.ui.compose_ui.addpack.AddPackFormState
import com.silkysys.warehouse.app.ui.compose_ui.addpack.AddPackScreen
import com.silkysys.warehouse.app.ui.compose_ui.dialog_boxes.AppProgressDialog
import com.silkysys.warehouse.app.ui.utility.SavePref
import com.silkysys.warehouse.app.ui.utility.extension.showErrorAlert
import com.silkysys.warehouse.app.ui.utility.extension.showSuccessAlert
import com.silkysys.warehouse.sharedcode.data.packing.addPack.dto.AddPackRequest
import com.silkysys.warehouse.sharedcode.domain.packing.addPackItem.entity.AddPackEntity
import com.silkysys.warehouse.sharedcode.domain.packing.packItems.entity.PackItemsEntity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class AddPackActivity : BaseActivity() {
    private lateinit var updatedItems: ArrayList<AddPackRequest.Items>
    private var showProgress by mutableStateOf(false)
    private val  savePref: SavePref by inject()
    private val form = AddPackFormState()
    private var packID: Int = 0
    private var packHeight=0
    private var packWidth=0
    private var packWeight=0
    private var packLength=0
    private  val addPackViewModel: AddPackViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!requireAuthenticatedSession()) return
        setContent {
            AddPackScreen(
                form = form,
                onBack = { finish() },
                onAddPack = { onAddPackClick() },
            )

            if (showProgress) AppProgressDialog()
        }
        packID= intent.getIntExtra("packID",0)
        observePackItems()
        observeAddItems()
    }

    private fun observeAddItems() {
        addPackViewModel.addPackStatus.flowWithLifecycle(lifecycle,Lifecycle.State.STARTED)
            .onEach { addPackStatus -> handleAddStatus(addPackStatus) }
            .launchIn(lifecycleScope)
    }

    private fun handleAddStatus(addPackStatus: AddPackStatus) {
        when(addPackStatus){
            is AddPackStatus.Init -> Unit
            is AddPackStatus.SuccessAddPack -> handleAddSuccess(addPackStatus.addPackEntity)
            is AddPackStatus.ErrorAddPAck -> showErrorAlert(addPackStatus.addPackResponse.message)
            is AddPackStatus.SetAddPackLoading -> handleLoading(addPackStatus.loading)
            is AddPackStatus.ShowAddToast -> {
                handleLoading(false)
                ErrorHandler.handleException(addPackStatus.message, this@AddPackActivity)

                //showErrorAlert(addPackStatus.message.toString())
            }
        }
    }

    private fun handleLoading(loading: Boolean) {
        showProgress = loading
    }

    private fun handleAddSuccess(addPackEntity: AddPackEntity) {
        showSuccessAlert(addPackEntity.message){
            finish()
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun onAddPackClick() {
        updatedItems=  form.getAllPackedItems()
        if (isDataValid()) {
            var uom = "box"
            if(form.isBagSelected)
                uom= "bag"
            addPackViewModel.addPackItem(
                savePref.companyId, packID, AddPackRequest(
                    packHeight,
                    packWidth,
                    packLength,
                    packWeight,
                    "pack-",
//                        binding.etPackName.text.toString(),
                    uom,form.isFragile,
                    true,updatedItems
                )
            )
        }
    }

    private fun isDataValid(): Boolean {
        if (form.height.isNotEmpty()){
            packHeight= form.height.toInt()
        }
        if (form.width.isNotEmpty()){
            packWidth= form.width.toInt()
        }
        if (form.length.isNotEmpty()) {
            packLength= form.length.toInt()
        }
        if (form.weight.isNotEmpty()) {
            packWeight= form.weight.toInt()
        }
//        if (binding.etPackName.text.toString().isEmpty()) {
//            binding.etPackName.error = getString(R.string.enter_name)
//            return false
//        } else
        if (updatedItems.isEmpty()) {
            Toast.makeText(this, getString(R.string.enter_quantity), Toast.LENGTH_SHORT).show()
            return false
        } else return true
    }
    private fun observePackItems() {
        addPackViewModel.itemsStatus.flowWithLifecycle(lifecycle,Lifecycle.State.STARTED)
            .onEach { itemsState -> handleItemsState(itemsState)}
            .launchIn(lifecycleScope)
    }

    private fun handleItemsState(itemsState: PackItemsStatus) {
        when(itemsState){
            is PackItemsStatus.Init -> Unit
            is PackItemsStatus.SuccessGetItems -> handleSuccessGetItems(itemsState.packItemsEntity)
            is PackItemsStatus.ErrorGetItems -> showErrorAlert(itemsState.response.message)
            is PackItemsStatus.SetItemsLoading -> Unit
            is PackItemsStatus.ShowItemsToast -> {
                handleLoading(false)
                ErrorHandler.handleException(itemsState.message, this@AddPackActivity)

                //showErrorAlert(getString(R.string.api_request_error))
            }
        }
    }
    private fun handleSuccessGetItems(packItemsEntity: PackItemsEntity) {
        form.loadItems(packItemsEntity.packItems)
    }

    override fun onResume() {
        super.onResume()
        addPackViewModel.getPackItems(savePref.companyId,packID)
    }
}
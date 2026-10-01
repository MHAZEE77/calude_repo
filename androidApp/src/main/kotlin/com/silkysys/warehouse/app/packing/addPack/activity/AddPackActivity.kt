package com.silkysys.warehouse.app.packing.addPack.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.silkysys.warehouse.R
import com.silkysys.warehouse.app.error_handle.ErrorHandler
import com.silkysys.warehouse.app.packing.addPack.AddPackStatus
import com.silkysys.warehouse.app.packing.addPack.AddPackViewModel
import com.silkysys.warehouse.app.packing.addPack.PackItemsStatus
import com.silkysys.warehouse.app.packing.addPack.adapter.Adapter.ItemsInPackAdapter
import com.silkysys.warehouse.app.ui.activity.BaseActivity
import com.silkysys.warehouse.app.ui.utility.SavePref
import com.silkysys.warehouse.app.ui.utility.Utility
import com.silkysys.warehouse.app.ui.utility.extension.showErrorAlert
import com.silkysys.warehouse.app.ui.utility.extension.showSuccessAlert
import com.silkysys.warehouse.sharedcode.data.packing.addPack.dto.AddPackRequest
import com.silkysys.warehouse.databinding.ActivityAddPackBinding
import com.silkysys.warehouse.sharedcode.domain.packing.addPackItem.entity.AddPackEntity
import com.silkysys.warehouse.sharedcode.domain.packing.packItems.entity.PackItemsEntity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class AddPackActivity : BaseActivity() {
    private lateinit var updatedItems: ArrayList<AddPackRequest.Items>
    private lateinit var mDialog: AlertDialog
    private val  savePref: SavePref by inject()
    private lateinit var binding: ActivityAddPackBinding
    private var packID: Int = 0
    private lateinit var  itemsAdapter: ItemsInPackAdapter
    private var packHeight=0
    private var packWidth=0
    private var packWeight=0
    private var packLength=0
    private  val addPackViewModel: AddPackViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!requireAuthenticatedSession()) return
        binding= ActivityAddPackBinding.inflate(layoutInflater)
        mDialog = Utility.getProgressDialog(this)
        setContentView(binding.root)
        packID= intent.getIntExtra("packID",0)
        observePackItems()
        observeAddItems()
        setOnClicks()
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
        if (loading)
            mDialog.show()
        else
            mDialog.dismiss()
    }

    private fun handleAddSuccess(addPackEntity: AddPackEntity) {
        showSuccessAlert(addPackEntity.message){
         finish()
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun setOnClicks() {
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.btAddPack.setOnClickListener {
            updatedItems=  itemsAdapter.getAllPackedItems()
            if (isDataValid()) {
                var uom = "box"
                    if(binding.rbBag.isSelected)
                        uom= "bag"
                    else if(binding.rbBox.isSelected)
                        uom= "box"
                addPackViewModel.addPackItem(
                    savePref.companyId, packID, AddPackRequest(
                        packHeight,
                        packWidth,
                        packLength,
                        packWeight,
                        "pack-",
//                        binding.etPackName.text.toString(),
                        uom,binding.cbIsFragile.isChecked,
                        true,updatedItems
                    )
                )
            }
        }
    }

    private fun isDataValid(): Boolean {
       if (binding.etPackHeight.text.toString().isNotEmpty()){
           packHeight= binding.etPackHeight.text.toString().toInt()
        }
        if (binding.etPackWidth.text.toString().isNotEmpty()){
           packWidth= binding.etPackWidth.text.toString().toInt()
        }
        if (binding.etPackLength.text.toString().isNotEmpty()) {
           packLength= binding.etPackLength.text.toString().toInt()
       }
        if (binding.etPackWeight.text.toString().isNotEmpty()) {
           packWeight= binding.etPackWeight.text.toString().toInt()
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
        itemsAdapter= ItemsInPackAdapter(packItemsEntity.packItems)
        binding.rvPackItems.adapter= itemsAdapter
        binding.rvPackItems.layoutManager=LinearLayoutManager(this)
    }

    override fun onResume() {
        super.onResume()
        addPackViewModel.getPackItems(savePref.companyId,packID)
    }
}

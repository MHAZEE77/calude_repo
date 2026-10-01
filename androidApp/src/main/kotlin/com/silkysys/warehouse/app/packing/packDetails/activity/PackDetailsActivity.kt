package com.silkysys.warehouse.app.packing.packDetails.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.silkysys.warehouse.R
import com.silkysys.warehouse.app.error_handle.ErrorHandler
import com.silkysys.warehouse.app.packing.addPack.activity.AddPackActivity
import com.silkysys.warehouse.app.packing.packDetails.FinishStatus
import com.silkysys.warehouse.app.packing.packDetails.PackDetailsViewModel
import com.silkysys.warehouse.app.packing.packDetails.PackStatus
import com.silkysys.warehouse.app.packing.packDetails.adapter.PackDetailsAdapter
import com.silkysys.warehouse.app.ui.activity.BaseActivity
import com.silkysys.warehouse.app.ui.utility.SavePref
import com.silkysys.warehouse.app.ui.utility.Utility
import com.silkysys.warehouse.app.ui.utility.extension.showErrorAlert
import com.silkysys.warehouse.databinding.ActivityPackDetailsBinding
import com.silkysys.warehouse.sharedcode.domain.packing.finishPack.entity.FinishPackEntity
import com.silkysys.warehouse.sharedcode.domain.packing.packDetails.entity.PackDetailsEntity
import com.silkysys.warehouse.wms.app.ui.utility.alert
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PackDetailsActivity : BaseActivity() {

    private val  savePref: SavePref by inject()
    private lateinit var mDialog: AlertDialog
    private val packDetailsViewModel: PackDetailsViewModel by viewModel()
    private lateinit var binding: ActivityPackDetailsBinding
    private var packID:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!requireAuthenticatedSession()) return
        binding= ActivityPackDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
         mDialog = Utility.getProgressDialog(this)
        binding.tvDetailsItem.text= intent.getStringExtra("packName")
        packID= intent.getIntExtra("packID",0)
        observePackDetails()
        observeFinisPack()
        setOnClicks()
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
    @SuppressLint("SuspiciousIndentation")
    private fun setOnClicks() {
        binding.ivBack.setOnClickListener {
            finish()
        }
        //to add pack Item
        binding.civAddPack.setOnClickListener {
        val intent= Intent(this,AddPackActivity::class.java)
            intent.putExtra("packID",packID)
            startActivity(intent)
        }
        // to finish this pack list
        binding.btFinish.setOnClickListener {
            val confirmDialog= AlertDialog.Builder(this)
            alert {
                message = getString(R.string.finish_pack_confirmation_message)
                positiveButton(getString(R.string.ok)) {
                    packDetailsViewModel.finishPack(savePref.companyId,packID)
                }
                negativeButton(getString(R.string.cancel)) { }
            }.show()
        }
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
        if (loading)
            mDialog.show()
        else
            mDialog.dismiss()
    }
    private fun handleSuccessGetDetails(packDetailsEntity: PackDetailsEntity) {
        if (packDetailsEntity.canPack) {
            binding.civAddPack.visibility = View.VISIBLE
            binding.btFinish.visibility = View.GONE
        } else {
            binding.civAddPack.visibility = View.GONE
            binding.btFinish.visibility = View.VISIBLE
        }
        if (packDetailsEntity.packListItems.isNotEmpty()) {
            val adapter = PackDetailsAdapter(packDetailsEntity.packListItems)
            binding.rvPackDetailsRecycler.layoutManager = LinearLayoutManager(this)
            binding.rvPackDetailsRecycler.adapter = adapter
        }else{
            binding.rvPackDetailsRecycler.visibility= View.GONE
            binding.linEmptyContainer.visibility = View.VISIBLE
        }
    }

}

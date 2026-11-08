package com.silkysys.warehouse.app.packing.pack.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.silkysys.warehouse.app.error_handle.ErrorHandler
import com.silkysys.warehouse.app.packing.pack.PackListState
import com.silkysys.warehouse.app.packing.pack.PackViewModel
import com.silkysys.warehouse.app.packing.pack.adapter.PackAdapter
import com.silkysys.warehouse.app.packing.packDetails.activity.PackDetailsActivity
import com.silkysys.warehouse.app.ui.activity.BaseActivity
import com.silkysys.warehouse.app.ui.utility.SavePref
import com.silkysys.warehouse.app.ui.utility.Utility
import com.silkysys.warehouse.app.ui.utility.extension.showErrorAlert
import com.silkysys.warehouse.databinding.ActivityPackBinding
import com.silkysys.warehouse.sharedcode.domain.packing.packList.entity.PackListEntity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PackActivity : BaseActivity(), PackAdapter.OnClickListener {

    private val savePref: SavePref by inject()
    private lateinit var mDialog: AlertDialog
    private lateinit var binding: ActivityPackBinding
    private lateinit var adapter: PackAdapter
    private val packViewModel: PackViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!requireAuthenticatedSession()) return
        mDialog = Utility.getProgressDialog(this)
        binding = ActivityPackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setClicks()
        observePackingList()
    }

    private fun observePackingList() {
    packViewModel.packListState.flowWithLifecycle(lifecycle,Lifecycle.State.STARTED)
        .onEach { getPackList -> handleGetPackList(getPackList) }
        .launchIn(lifecycleScope)
    }

    private fun handleGetPackList(packListState: PackListState) {
        when(packListState){
            is PackListState.Init -> Unit
            is PackListState.SuccessPackList -> successGetPackList(packListState.packListEntity)
            is PackListState.ErrorPackList -> showErrorAlert(packListState.response.message)
            is PackListState.SetPackListLoading -> setLoading(packListState.loading)
            is PackListState.ShowGetPackListToast ->{
                setLoading(false)
                ErrorHandler.handleException(packListState.message, this@PackActivity)

                //showErrorAlert(getString(R.string.api_request_error))
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        if (loading)
            mDialog.show()
        else
            mDialog.dismiss()
    }

    private fun successGetPackList(packListEntity: PackListEntity) {
        setLoading(false)
        if (packListEntity.fulfillments.isNotEmpty()) {
            adapter = PackAdapter(packListEntity.fulfillments, this)
            binding.rvPackList.layoutManager = LinearLayoutManager(this)
            binding.rvPackList.adapter = adapter
        }else{
            binding.rvPackList.visibility= View.GONE
            binding.linEmptyContainer.visibility =  View.VISIBLE
        }
    }

    private fun setClicks() {
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.searchEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapter.filter(p0.toString())
            }
            override fun afterTextChanged(p0: Editable?) {
                adapter.filter(p0.toString())
            }
        })
    }
    override fun onClickPackItem(packItem: PackListEntity.Fulfillments) {
        val intent = Intent(this,PackDetailsActivity::class.java)
        intent.putExtra("packID",packItem.id)
        intent.putExtra("packName",packItem.document.document_name)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        packViewModel.getPackList(savePref.companyId,"packing")
    }
}

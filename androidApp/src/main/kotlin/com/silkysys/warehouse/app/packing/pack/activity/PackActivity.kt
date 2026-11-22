package com.silkysys.warehouse.app.packing.pack.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.silkysys.warehouse.app.error_handle.ErrorHandler
import com.silkysys.warehouse.app.packing.pack.PackListState
import com.silkysys.warehouse.app.packing.pack.PackViewModel
import com.silkysys.warehouse.app.packing.packDetails.activity.PackDetailsActivity
import com.silkysys.warehouse.app.ui.activity.BaseActivity
import com.silkysys.warehouse.app.ui.compose_ui.pack.PackScreen
import com.silkysys.warehouse.app.ui.utility.SavePref
import com.silkysys.warehouse.app.ui.utility.extension.showErrorAlert
import com.silkysys.warehouse.sharedcode.domain.packing.packList.entity.PackListEntity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.Locale
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.silkysys.warehouse.app.ui.compose_ui.dialog_boxes.AppProgressDialog

class PackActivity : BaseActivity() {

    private val savePref: SavePref by inject()
    private var showProgress by mutableStateOf(false)
    private var packList by mutableStateOf<List<PackListEntity.Fulfillments>>(emptyList())
    private var searchQuery by mutableStateOf("")
    private var isEmptyVisible by mutableStateOf(false)
    private val packViewModel: PackViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!requireAuthenticatedSession()) return
        setContent {
            PackScreen(
                packList = filteredPackList(),
                isEmptyVisible = isEmptyVisible,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onBack = { finish() },
                onItemClick = ::onClickPackItem,
            )

            if (showProgress) AppProgressDialog()
        }
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
        showProgress = loading
    }

    private fun successGetPackList(packListEntity: PackListEntity) {
        setLoading(false)
        if (packListEntity.fulfillments.isNotEmpty()) {
            packList = packListEntity.fulfillments
            isEmptyVisible = false
        }else{
            isEmptyVisible = true
        }
    }

    // PackAdapter.filter(query)
    private fun filteredPackList(): List<PackListEntity.Fulfillments> {
        val query = searchQuery
        if (query.isEmpty()) return packList
        return packList.filter { item ->
            item.name.contains(query) ||
                    item.name.lowercase(Locale.ROOT).contains(query) ||
                    item.name.uppercase(Locale.ROOT).contains(query)
        }
    }

    private fun onClickPackItem(packItem: PackListEntity.Fulfillments) {
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
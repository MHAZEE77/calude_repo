package com.silkysys.warehouse.app.picking.pick.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.silkysys.warehouse.app.error_handle.ErrorHandler
import com.silkysys.warehouse.app.picking.pick.PickViewModel
import com.silkysys.warehouse.app.picking.pick.UpdateStatus
import com.silkysys.warehouse.app.picking.pick.WaitingStatus
import com.silkysys.warehouse.app.picking.pick.activity.PickActivity
import com.silkysys.warehouse.app.ui.compose_ui.pick.PickWaitingContent
import com.silkysys.warehouse.app.ui.utility.SavePref
import com.silkysys.warehouse.app.ui.utility.extension.showErrorAlert
import com.silkysys.warehouse.sharedcode.data.picking.updatePickStatus.dto.UpdatePickStatusRequest
import com.silkysys.warehouse.sharedcode.domain.picking.pick.entity.PickEntity
import com.silkysys.warehouse.sharedcode.domain.picking.updatePickStatus.entity.UpdatePickStatusEntity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PickWaitingFragment() : Fragment() {
    private var waiting by mutableStateOf<List<PickEntity.Fulfillments.Waiting>>(emptyList())
    private var selected by mutableStateOf<List<PickEntity.Fulfillments.Waiting>>(emptyList())
    private var isEmptyVisible by mutableStateOf(true)
    private var showShimmer by mutableStateOf(false)
    private val savePref: SavePref by inject()
    private val pickViewModel: PickViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        pickViewModel.getWaitingList(savePref.companyId)
        observeGetWaitingList()
        observeAcceptWaitingList()
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                PickWaitingContent(
                    waiting = waiting,
                    selected = selected,
                    isEmptyVisible = isEmptyVisible,
                    isLoading = showShimmer,
                    onToggleAll = ::onToggleAll,
                    onToggleItem = ::onToggleItem,
                    onStart = ::onStartClick,
                    onRefresh = { pickViewModel.getWaitingList(savePref.companyId) },
                )
            }
        }
    }

    private fun observeGetWaitingList() {
        pickViewModel.getWaitingStatus.flowWithLifecycle(
            viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED
        )
            .onEach { state -> handleGetWaiting(state) }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleGetWaiting(state: WaitingStatus) {
        when (state) {
            is WaitingStatus.Init -> Unit
            is WaitingStatus.SuccessGetWaiting -> handleSuccessGetWaiting(state.pickEntity)
            is WaitingStatus.ErrorGetWaiting -> {
                setLoading(false)
                activity?.showErrorAlert(state.pickResponse.message)
            }
            is WaitingStatus.SetGetWaitingLoading -> setLoading(state.loading)
            is WaitingStatus.ShowGetWaitingToast -> {
                setLoading(false)
                ErrorHandler.handleException(state.message, requireContext())
            }
        }
    }

    private fun handleSuccessGetWaiting(pickEntity: PickEntity) {
        setLoading(false)
        waiting = pickEntity.fulfillments.waiting
        // PickWaitingAdapter used to receive this same list as its selection, so every order starts selected
        selected = waiting
        bindViewModelObserver()
    }

    private fun observeAcceptWaitingList() {
        pickViewModel.updateStatus.flowWithLifecycle(
            viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED
        )
            .onEach { state -> handleAcceptWaiting(state) }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleAcceptWaiting(state: UpdateStatus) {
        when (state) {
            is UpdateStatus.Init -> Unit
            is UpdateStatus.SuccessUpdate -> handleSuccessAccept(state.updatePickStatusEntity)
            is UpdateStatus.ErrorUpdate -> {
                setLoading(false)
                Toast.makeText(context, state.updatePickStatusResponse.message, Toast.LENGTH_SHORT)
                    .show()
            }

            is UpdateStatus.SetLoading -> setLoading(state.loading)
            is UpdateStatus.ShowToast -> {
                setLoading(false)
                ErrorHandler.handleException(state.message, requireContext())
            }
        }
    }

    private fun handleSuccessAccept(updatePickStatusEntity: UpdatePickStatusEntity) {
//         activity?.showToast(updatePickStatusEntity.message)
        val intent = Intent(activity, PickActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun setLoading(loading: Boolean) {
        showShimmer = loading
    }

    private fun bindViewModelObserver() {
        updateEmptyContainer(waiting.isEmpty())
    }

    private fun updateEmptyContainer(visible: Boolean) {
        isEmptyVisible = visible
    }

    // PickWaitingAdapter.updateAllItemCheck
    private fun onToggleAll() {
        selected = if (selected.size == waiting.size) emptyList() else waiting
    }

    // PickWaitingAdapter.updateItemChecked
    private fun onToggleItem(item: PickEntity.Fulfillments.Waiting) {
        selected = if (selected.any { it.id == item.id }) {
            selected.filterNot { it.id == item.id }
        } else {
            selected + item
        }
    }

    private fun onStartClick() {
//             alert {
//                message = getString(R.string.sure_message_start_pick_order_alert)
//                positiveButton(android.R.string.yes) {
        pickViewModel.updateStatus(
            savePref.companyId,
            UpdatePickStatusRequest(selected.map { it.id }, "ready_picking")
        )
//                }
//                negativeButton(android.R.string.no) {  }
//            }.show()
    }

}
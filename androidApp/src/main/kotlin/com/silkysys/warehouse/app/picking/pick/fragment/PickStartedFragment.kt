package com.silkysys.warehouse.app.picking.pick.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.silkysys.warehouse.app.picking.pick.StartedStatus
import com.silkysys.warehouse.app.picking.startPick.activity.PickItemsActivity
import com.silkysys.warehouse.app.ui.compose_ui.pick.PickStartedContent
import com.silkysys.warehouse.app.ui.utility.SavePref
import com.silkysys.warehouse.app.ui.utility.extension.showErrorAlert
import com.silkysys.warehouse.sharedcode.domain.picking.pick.entity.PickEntity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PickStartedFragment() : Fragment() {
    // Properties
    private var started by mutableStateOf<List<PickEntity.Fulfillments.Started>>(emptyList())
    private var isEmptyVisible by mutableStateOf(false)
    private var showShimmer by mutableStateOf(false)
    private val pickViewModel: PickViewModel by viewModel()
    private val savePref: SavePref by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Call Methods
        pickViewModel.getStartedList(savePref.companyId)
        observeGetStartedList()
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                PickStartedContent(
                    started = started,
                    isEmptyVisible = isEmptyVisible,
                    isLoading = showShimmer,
                    onItemClick = ::onItemClick,
                    onRefresh = { pickViewModel.getStartedList(savePref.companyId) },
                )
            }
        }
    }

    private fun onItemClick(model: PickEntity.Fulfillments.Started) {
        val intent = Intent(activity, PickItemsActivity::class.java)
        intent.putExtra("startedList", model)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun observeGetStartedList() {
        pickViewModel.getStartedStatus.flowWithLifecycle(
            viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED
        )
            .onEach { state -> handleGetStarted(state) }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleGetStarted(state: StartedStatus) {
        when (state) {
            is StartedStatus.Init -> Unit
            is StartedStatus.SuccessGetStarted -> handleSuccessGetStarted(state.pickEntity)
            is StartedStatus.ErrorGetStarted -> {
                setLoading(false)
                activity?.showErrorAlert(state.pickResponse.message)
            }
            is StartedStatus.SetGetStartedLoading -> setLoading(state.loading)
            is StartedStatus.ShowGetStartedToast -> {
                setLoading(false)
                ErrorHandler.handleException(state.message, requireContext())
            }
        }
    }

    private fun handleSuccessGetStarted(pickEntity: PickEntity) {
        setLoading(false)
        started = pickEntity.fulfillments.started
        bindPickViewModel()
    }

    private fun bindPickViewModel() {
        updateEmptyContainer(started.isEmpty())
    }

    private fun updateEmptyContainer(visible: Boolean) {
        isEmptyVisible = visible
    }

    private fun setLoading(loading: Boolean) {
        showShimmer = loading
    }

}
package com.silkysys.warehouse.app.picking.pick.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.silkysys.warehouse.app.error_handle.ErrorHandler
import com.silkysys.warehouse.app.picking.pick.PickViewModel
import com.silkysys.warehouse.app.picking.pick.UpdateStatus
import com.silkysys.warehouse.app.picking.pick.WaitingStatus
import com.silkysys.warehouse.app.picking.pick.activity.PickActivity
import com.silkysys.warehouse.app.picking.pick.adapter.PickWaitingAdapter
import com.silkysys.warehouse.app.ui.utility.SavePref
import com.silkysys.warehouse.app.ui.utility.extension.showErrorAlert
import com.silkysys.warehouse.sharedcode.data.picking.updatePickStatus.dto.UpdatePickStatusRequest
import com.silkysys.warehouse.databinding.FragmentPickWaitingBinding
import com.silkysys.warehouse.sharedcode.domain.picking.pick.entity.PickEntity
import com.silkysys.warehouse.sharedcode.domain.picking.updatePickStatus.entity.UpdatePickStatusEntity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PickWaitingFragment() : Fragment(), PickWaitingAdapter.OnItemCheckListener {
    private lateinit var binding: FragmentPickWaitingBinding
    private lateinit var adapter: PickWaitingAdapter
    private lateinit var waiting: List<PickEntity.Fulfillments.Waiting>
    private val savePref: SavePref by inject()
    private val pickViewModel: PickViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPickWaitingBinding.inflate(inflater, container, false)
        setupClickListener()
        pickViewModel.getWaitingList(savePref.companyId)
        observeGetWaitingList()
        observeAcceptWaitingList()
        return binding.root
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
        waiting = pickEntity.fulfillments.waiting.toMutableList()
        adapter = PickWaitingAdapter(waiting as MutableList<PickEntity.Fulfillments.Waiting>, this)
        bindViewModelObserver()
        setupRecyclerView()
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
        if (loading) {
            binding.shimmerLayout.startShimmer()
            binding.shimmerLayout.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.INVISIBLE
            binding.swipeRefreshLayout.isRefreshing = true
        } else {
            binding.shimmerLayout.stopShimmer()
            binding.shimmerLayout.visibility = View.INVISIBLE
            binding.recyclerView.visibility = View.VISIBLE
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            val linearLayoutManager = LinearLayoutManager(context)
            layoutManager = linearLayoutManager
            adapter = this@PickWaitingFragment.adapter
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            pickViewModel.getWaitingList(savePref.companyId)
        }
    }

    private fun bindViewModelObserver() {
        updateEmptyContainer(waiting.isEmpty())
        adapter.clearItems()
        adapter.addAllItem(waiting)
    }

    private fun updateEmptyContainer(visible: Boolean) {
        binding.emptyContainer.visibility = if (visible) View.VISIBLE else View.GONE
        binding.swipeRefreshLayout.visibility = if (visible) View.GONE else View.VISIBLE
    }

    private fun setupClickListener() {
        binding.startContainer.setOnClickListener {
//             alert {
//                message = getString(R.string.sure_message_start_pick_order_alert)
//                positiveButton(android.R.string.yes) {
            pickViewModel.updateStatus(
                savePref.companyId,
                UpdatePickStatusRequest(adapter.getSelectedList().map { it.id }, "ready_picking")
            )
//                }
//                negativeButton(android.R.string.no) {  }
//            }.show()
        }
        binding.refreshButton.setOnClickListener {
            pickViewModel.getWaitingList(savePref.companyId)
        }
    }

    override fun onItemCheck(selectedList: MutableList<PickEntity.Fulfillments.Waiting>) {
        binding.startCountTextView.text = selectedList.size.toString()
    }

}

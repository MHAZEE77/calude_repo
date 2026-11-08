package com.silkysys.warehouse.app.picking.pick.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.silkysys.warehouse.app.error_handle.ErrorHandler
import com.silkysys.warehouse.app.picking.pick.PickViewModel
import com.silkysys.warehouse.app.picking.pick.StartedStatus
import com.silkysys.warehouse.app.picking.pick.adapter.PickStartedAdapter
import com.silkysys.warehouse.app.picking.startPick.activity.PickItemsActivity
import com.silkysys.warehouse.app.ui.utility.SavePref
import com.silkysys.warehouse.app.ui.utility.extension.showErrorAlert
import com.silkysys.warehouse.databinding.FragmentPickStartedBinding
import com.silkysys.warehouse.sharedcode.domain.picking.pick.entity.PickEntity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PickStartedFragment() : Fragment() {
    // Properties
    private lateinit var mOnClickListener: View.OnClickListener
    private lateinit var binding: FragmentPickStartedBinding
    private lateinit var adapter: PickStartedAdapter
    private lateinit var started: List<PickEntity.Fulfillments.Started>
    private val pickViewModel: PickViewModel by viewModel()
    private val savePref: SavePref by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPickStartedBinding.inflate(inflater, container, false)
        // Call Methods
        pickViewModel.getStartedList(savePref.companyId)
        setupRecyclerView()
//        bindPickViewModel()
        setupOnClickListener()
        observeGetStartedList()
        return binding.root
    }

    private fun setupRecyclerView() {
        mOnClickListener = View.OnClickListener { view ->
            val model = view.tag as PickEntity.Fulfillments.Started
            val intent = Intent(activity, PickItemsActivity::class.java)
            intent.putExtra("startedList", model)
            startActivity(intent)
            requireActivity().finish()
        }
        adapter = PickStartedAdapter(mOnClickListener)
        binding.recyclerView.apply {
            val linearLayoutManager = LinearLayoutManager(context)
            layoutManager = linearLayoutManager
            adapter = this@PickStartedFragment.adapter
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            pickViewModel.getStartedList(savePref.companyId)
        }
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
        adapter.clearItems()
        adapter.addAllItem(started)
    }

    private fun updateEmptyContainer(visible: Boolean) {
        binding.emptyContainer.visibility = if (visible) View.VISIBLE else View.GONE
        binding.swipeRefreshLayout.visibility = if (visible) View.GONE else View.VISIBLE
    }

    private fun setupOnClickListener() {
        binding.refreshButton.setOnClickListener {
            pickViewModel.getStartedList(savePref.companyId)
        }
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

}

package com.silkysys.warehouse.app.picking.pick

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.data.picking.pick.dto.pickWaitingAndStartedResponse.PickWaitingAndStartedResponse
import com.silkysys.warehouse.sharedcode.data.picking.updatePickStatus.dto.UpdatePickStatusRequest
import com.silkysys.warehouse.sharedcode.data.picking.updatePickStatus.dto.UpdatePickStatusResponse
import com.silkysys.warehouse.sharedcode.domain.picking.pick.entity.PickEntity
import com.silkysys.warehouse.sharedcode.domain.picking.pick.useCase.PickWaitingAndStartedUseCase
import com.silkysys.warehouse.sharedcode.domain.picking.updatePickStatus.entity.UpdatePickStatusEntity
import com.silkysys.warehouse.sharedcode.domain.picking.updatePickStatus.useCase.UpdatePickStatusUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch


class PickViewModel (
    private val pickUseCase: PickWaitingAndStartedUseCase,
    private val updatePickStatusUseCase: UpdatePickStatusUseCase
) : ViewModel() {

    private val _getWaitingStatus = MutableStateFlow<WaitingStatus>(WaitingStatus.Init)
    val getWaitingStatus: StateFlow<WaitingStatus> get() = _getWaitingStatus

    private val _getStartedStatus = MutableStateFlow<StartedStatus>(StartedStatus.Init)
    val getStartedStatus: StateFlow<StartedStatus> get() = _getStartedStatus

    private val _updateStatus = MutableStateFlow<UpdateStatus>(UpdateStatus.Init)
    val updateStatus: StateFlow<UpdateStatus> get() = _updateStatus

    private fun setUpdateLoading(loading: Boolean) {
        _updateStatus.value = UpdateStatus.SetLoading(loading)
    }

    private fun showUpdateToast(message: Throwable) {
        _updateStatus.value = UpdateStatus.ShowToast(message)
    }

    private fun setGetWaitingLoading(loading: Boolean) {
        _getWaitingStatus.value = WaitingStatus.SetGetWaitingLoading(loading)
    }

    private fun showGetWaitingToast(message: Throwable) {
        _getWaitingStatus.value = WaitingStatus.ShowGetWaitingToast(message)
    }

    private fun setGetStartedLoading(loading: Boolean) {
        _getStartedStatus.value = StartedStatus.SetGetStartedLoading(loading)
    }

    private fun showGetStartedToast(message: Throwable) {
        _getStartedStatus.value = StartedStatus.ShowGetStartedToast(message)
    }

    fun getWaitingList(companyId: Int) {
        viewModelScope.launch {
            pickUseCase.getPickWaitingAndStarted(companyId).onStart {
                setGetWaitingLoading(true)
            }.catch { exception ->
                setGetWaitingLoading(false)
                showGetWaitingToast(exception)
            }.collect { result ->
                setGetWaitingLoading(false)
                when (result) {
                    is BaseResult.Success -> _getWaitingStatus.value =
                        WaitingStatus.SuccessGetWaiting(result.data)

                    is BaseResult.Error -> _getWaitingStatus.value =
                        WaitingStatus.ErrorGetWaiting(result.rawResponse)
                }
            }
        }
    }

    fun getStartedList(companyId: Int) {
        viewModelScope.launch {
            pickUseCase.getPickWaitingAndStarted(companyId).onStart {
                setGetStartedLoading(true)
            }.catch { exception ->
                setGetStartedLoading(false)
                showGetStartedToast(exception)
            }.collect { result ->
                setGetStartedLoading(false)
                when (result) {
                    is BaseResult.Success -> _getStartedStatus.value =
                        StartedStatus.SuccessGetStarted(result.data)

                    is BaseResult.Error -> _getStartedStatus.value =
                        StartedStatus.ErrorGetStarted(result.rawResponse)
                }
            }
        }
    }

    fun updateStatus(companyId: Int, updatePickStatusRequest: UpdatePickStatusRequest) {
        viewModelScope.launch {
            updatePickStatusUseCase.updatePickStatus(companyId, updatePickStatusRequest).onStart {
                setUpdateLoading(true)
            }.catch { exception ->
                setUpdateLoading(false)
                showUpdateToast(exception)
            }.collect { result ->
                setUpdateLoading(false)
                when (result) {
                    is BaseResult.Success ->
                        _updateStatus.value = UpdateStatus.SuccessUpdate(result.data)

                    is BaseResult.Error ->
                        _updateStatus.value = UpdateStatus.ErrorUpdate(result.rawResponse)
                }
            }
        }
    }
}

sealed class WaitingStatus {
    object Init : WaitingStatus()
    data class SuccessGetWaiting(val pickEntity: PickEntity) : WaitingStatus()
    data class ErrorGetWaiting(val pickResponse: WrappedResponse<PickWaitingAndStartedResponse>) :
        WaitingStatus()

    data class ShowGetWaitingToast(val message: Throwable) : WaitingStatus()
    data class SetGetWaitingLoading(val loading: Boolean) : WaitingStatus()
}

sealed class StartedStatus {
    object Init : StartedStatus()
    data class SuccessGetStarted(val pickEntity: PickEntity) : StartedStatus()
    data class ErrorGetStarted(val pickResponse: WrappedResponse<PickWaitingAndStartedResponse>) :
        StartedStatus()

    data class ShowGetStartedToast(val message: Throwable) : StartedStatus()
    data class SetGetStartedLoading(val loading: Boolean) : StartedStatus()
}

sealed class UpdateStatus() {
    object Init : UpdateStatus()
    data class SuccessUpdate(val updatePickStatusEntity: UpdatePickStatusEntity) : UpdateStatus()
    data class ErrorUpdate(val updatePickStatusResponse: WrappedResponse<UpdatePickStatusResponse>) :
        UpdateStatus()

    data class ShowToast(val message: Throwable) : UpdateStatus()
    data class SetLoading(val loading: Boolean) : UpdateStatus()
}

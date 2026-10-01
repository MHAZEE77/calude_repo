package com.silkysys.warehouse.app.putAway.putAway

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.data.putAwayModule.putAway.dto.putAwayResponse.PutAwayScanResponse
import com.silkysys.warehouse.sharedcode.domain.putAwayModule.putAway.entity.PutAwayScanEntity
import com.silkysys.warehouse.sharedcode.domain.putAwayModule.putAway.useCase.PutAwayUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch


class PutAwayViewModel (private val putAwayUseCase: PutAwayUseCase): ViewModel() {

    private val _putAwayStatus = MutableStateFlow<PutAwayStatus>(PutAwayStatus.Init)
    val putAwayStatus: StateFlow<PutAwayStatus> get() = _putAwayStatus

    private fun setLoading(loading: Boolean) {
        _putAwayStatus.value = PutAwayStatus.SetLoading(loading)
    }

    private fun showToast(throwable: Throwable) {
        _putAwayStatus.value = PutAwayStatus.ShowToast(throwable)
    }

    fun putAwayScan(barcode: String) {
        viewModelScope.launch {
            putAwayUseCase.putAwayScan( barcode).onStart {
                setLoading(true)
            }.catch { exception ->
                setLoading(false)
                showToast(exception)
            }.collect { result ->
                setLoading(false)
                when (result) {
                    is BaseResult.Success ->
                        _putAwayStatus.value =
                            PutAwayStatus.SuccessPutAway(result.data)

                    is BaseResult.Error ->
                        _putAwayStatus.value =
                            PutAwayStatus.ErrorPutAway(result.rawResponse)
                }
            }
        }
    }
}
sealed class PutAwayStatus{
    object Init: PutAwayStatus()
    data class SuccessPutAway(val putAwayScanEntity: PutAwayScanEntity): PutAwayStatus()
    data class ErrorPutAway(val putAwayScanResponse: WrappedResponse<PutAwayScanResponse>):
        PutAwayStatus()
    data class SetLoading(val loading: Boolean): PutAwayStatus()
    data class ShowToast(val throwable: Throwable): PutAwayStatus()
}

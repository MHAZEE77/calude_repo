package com.silkysys.warehouse.app.addStock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.domain.addstock.AddStockRequest
import com.silkysys.warehouse.sharedcode.domain.addstock.AddStockUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch


class AddStockViewModel (private val addStockUseCase: AddStockUseCase): ViewModel() {

    private val _addStockStatus = MutableStateFlow<AddStockStatus>(AddStockStatus.Init)
    val addStockStatus: StateFlow<AddStockStatus> get() = _addStockStatus

    private fun setLoading(loading: Boolean) {
        _addStockStatus.value = AddStockStatus.SetLoading(loading)
    }

    private fun showToast(message: Throwable) {
        _addStockStatus.value = AddStockStatus.ShowToast(message)
    }
    fun resetStatus() {
        _addStockStatus.value = AddStockStatus.Init
    }

    fun addStock(addStockRequest: AddStockRequest) {
        viewModelScope.launch {
            addStockUseCase.addStock(addStockRequest).onStart {
                setLoading(true)
            }.catch { exception ->
                setLoading(false)
                showToast(exception)
            }.collect { result ->
                setLoading(false)
                when (result) {
                    is BaseResult.Success ->
                        _addStockStatus.value =
                            AddStockStatus.SuccessAddStock(result.data)
                    is BaseResult.Error ->
                        _addStockStatus.value =
                            AddStockStatus.ErrorAddStock(result.rawResponse)
                }
            }
        }
    }
}

sealed class AddStockStatus{
    object Init: AddStockStatus()
    data class SuccessAddStock(val wrappedResponse: WrappedResponse<Unit>) : AddStockStatus()
    data class ErrorAddStock(val errorResponse: WrappedResponse<Unit>) : AddStockStatus()
    data class SetLoading(val loading: Boolean): AddStockStatus()
    data class ShowToast(val message: Throwable): AddStockStatus()
}

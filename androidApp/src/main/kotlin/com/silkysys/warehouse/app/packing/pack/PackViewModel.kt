package com.silkysys.warehouse.app.packing.pack

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.data.packing.packList.dto.PackResponse
import com.silkysys.warehouse.sharedcode.domain.packing.packList.entity.PackListEntity
import com.silkysys.warehouse.sharedcode.domain.packing.packList.useCase.PackListUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class PackViewModel (private val packListUseCase: PackListUseCase): ViewModel() {
    private val _packListState = MutableStateFlow<PackListState>(PackListState.Init)
    val packListState: StateFlow<PackListState> get() = _packListState

    private fun setLoading(loading: Boolean) {
        _packListState.value = PackListState.SetPackListLoading(loading)
    }

    private fun showToast(message: Throwable) {
        _packListState.value = PackListState.ShowGetPackListToast(message)
    }
    fun getPackList(companyId: Int, status: String) {
        viewModelScope.launch {
            packListUseCase.getPackList(companyId, status).onStart {
                setLoading(true)
            }.catch { exception ->
                setLoading(false)
                showToast(exception)
            }.collect { result ->
                setLoading(false)
                when (result) {
                    is BaseResult.Success ->
                        _packListState.value = PackListState.SuccessPackList(result.data)
                    is BaseResult.Error ->
                        _packListState.value = PackListState.ErrorPackList(result.rawResponse)
                }
            }
        }
    }
}
sealed class PackListState(){
    object Init: PackListState()
    data class SuccessPackList(val packListEntity: PackListEntity): PackListState()
    data class ErrorPackList(val response: WrappedResponse<PackResponse>): PackListState()
    data class ShowGetPackListToast(val message: Throwable): PackListState()
    data class SetPackListLoading(val loading: Boolean): PackListState()
}

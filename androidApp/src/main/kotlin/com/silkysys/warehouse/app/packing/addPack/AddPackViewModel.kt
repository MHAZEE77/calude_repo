package com.silkysys.warehouse.app.packing.addPack

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.data.packing.addPack.dto.AddPackRequest
import com.silkysys.warehouse.sharedcode.data.packing.addPack.dto.AddPackResponse
import com.silkysys.warehouse.sharedcode.data.packing.packItems.dto.PackItemsResponse
import com.silkysys.warehouse.sharedcode.domain.packing.addPackItem.entity.AddPackEntity
import com.silkysys.warehouse.sharedcode.domain.packing.addPackItem.useCase.AddPackUseCase
import com.silkysys.warehouse.sharedcode.domain.packing.packItems.entity.PackItemsEntity
import com.silkysys.warehouse.sharedcode.domain.packing.packItems.useCase.PackItemsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch


class AddPackViewModel (
    private val packItemsUseCase: PackItemsUseCase,
    private val addPackItemsUseCase: AddPackUseCase
    ): ViewModel(){

    private val _itemsStatus= MutableStateFlow<PackItemsStatus>(PackItemsStatus.Init)
    val itemsStatus: StateFlow<PackItemsStatus> get() = _itemsStatus

    private fun setItemsLoading(loading: Boolean){
        _itemsStatus.value= PackItemsStatus.SetItemsLoading(loading)
    }
    private fun showItemsToast(message: Throwable){
        _itemsStatus.value= PackItemsStatus.ShowItemsToast(message)
    }
    fun getPackItems(comPanyId: Int, fulfillmentId: Int){
        viewModelScope.launch {
            packItemsUseCase.getPackItems(comPanyId,fulfillmentId).onStart {
                setItemsLoading(true)
            }.catch {exception->
                setItemsLoading(false)
                showItemsToast(exception)
            }.collect {result ->
                setItemsLoading(false)
                when(result){
                    is BaseResult.Success -> _itemsStatus.value= PackItemsStatus.SuccessGetItems(result.data)
                    is BaseResult.Error -> _itemsStatus.value= PackItemsStatus.ErrorGetItems(result.rawResponse)
                }
            }
        }
    }
    private val _addPackStatus= MutableStateFlow<AddPackStatus>(AddPackStatus.Init)
    val addPackStatus : StateFlow<AddPackStatus> get() = _addPackStatus

    private fun setAddPackLoading(loading: Boolean){
        _addPackStatus.value = AddPackStatus.SetAddPackLoading(loading)
    }
    private fun showAddPAckToast(message: Throwable){
        _addPackStatus.value= AddPackStatus.ShowAddToast(message)
    }
    fun addPackItem(companyId: Int, fulffillmentId: Int,addPackRequest: AddPackRequest){
        viewModelScope.launch {
            addPackItemsUseCase.addPackItem(companyId,fulffillmentId,addPackRequest).onStart {
                setAddPackLoading(true)
            }.catch { exception ->
                setAddPackLoading(false)
                showAddPAckToast(exception)
            }.collect {result ->
                setAddPackLoading(false)
                when(result) {
                is BaseResult.Success -> _addPackStatus.value = AddPackStatus.SuccessAddPack(result.data)
                is BaseResult.Error -> _addPackStatus.value = AddPackStatus.ErrorAddPAck(result.rawResponse)
                }
            }
        }
      }
    }

sealed class PackItemsStatus{
    object Init: PackItemsStatus()
    data class SuccessGetItems(val packItemsEntity: PackItemsEntity): PackItemsStatus()
    data class ErrorGetItems(val response: WrappedResponse<PackItemsResponse>): PackItemsStatus()
    data class ShowItemsToast(val message: Throwable): PackItemsStatus()
    data class SetItemsLoading(val loading: Boolean): PackItemsStatus()
}
sealed class AddPackStatus{
    object Init: AddPackStatus()
    data class SuccessAddPack(val addPackEntity: AddPackEntity): AddPackStatus()
    data class ErrorAddPAck(val addPackResponse: WrappedResponse<AddPackResponse>): AddPackStatus()
    data class ShowAddToast(val message: Throwable): AddPackStatus()
    data class SetAddPackLoading(val loading: Boolean): AddPackStatus()
}

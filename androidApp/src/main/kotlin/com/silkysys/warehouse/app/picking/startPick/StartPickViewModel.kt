package com.silkysys.warehouse.app.picking.startPick

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.data.picking.startPick.dto.StartPickResponse
import com.silkysys.warehouse.data.picking.startPickUpdateDialogQuantity.dto.UpdateDialogQuantityResponse
import com.silkysys.warehouse.data.picking.startPickUpdateDialogQuantity.dto.UpdateQuantityRequest
import com.silkysys.warehouse.data.picking.startPickUpdateQuantity.dto.UpdateQuantityResponse
import com.silkysys.warehouse.sharedcode.data.startedItems.dto.StartedItemsResponse
import com.silkysys.warehouse.sharedcode.domain.picking.startPick.entity.StartPickEntity
import com.silkysys.warehouse.sharedcode.domain.picking.startPick.useCase.StartPickUseCase
import com.silkysys.warehouse.sharedcode.domain.picking.startPickUpdateDialogQuantity.entity.UpdateDialogQuantityEntity
import com.silkysys.warehouse.sharedcode.domain.picking.startPickUpdateDialogQuantity.useCase.UpdateDialogQuantityUseCase
import com.silkysys.warehouse.sharedcode.domain.picking.startPickUpdateQuantity.entity.UpdateQuantityEntity
import com.silkysys.warehouse.sharedcode.domain.picking.startPickUpdateQuantity.useCase.UpdateQuantityUseCase
import com.silkysys.warehouse.sharedcode.domain.picking.startedItems.useCase.StartedItemsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch


class StartPickViewModel (
    private val startPickUseCase: StartPickUseCase,
    private val startedItemsUseCase: StartedItemsUseCase,
    private val updateQuantityUseCase: UpdateQuantityUseCase,
    private val updateDialogQuantityUseCase: UpdateDialogQuantityUseCase
) : ViewModel(){

    private val _startStatus= MutableStateFlow<StartStatus>(StartStatus.Init)
    val startStatus: StateFlow<StartStatus> get() = _startStatus

    private val _getStartedStatus= MutableStateFlow<GetStartedListStatus>(GetStartedListStatus.Init)
    val getStartedStatus: StateFlow<GetStartedListStatus> get() = _getStartedStatus

    private val _quantityStatus= MutableStateFlow<QuantityStatus>(QuantityStatus.Init)
    val quantityStatus get() = _quantityStatus

    private val _dialogQuantityStatus= MutableStateFlow<DialogQuantityStatus>(DialogQuantityStatus.Init)
    val dialogQuantityStatus get() = _dialogQuantityStatus

    private fun setLoading(loading: Boolean){
        _startStatus.value= StartStatus.SetLoading(loading)
    }
    private fun showToast(message: Throwable){
        _startStatus.value = StartStatus.ShowToast(message)
    }
    private fun setUpdateLoading(loading: Boolean){
        _quantityStatus.value= QuantityStatus.SetLoading(loading)
    }
    private fun showUpdateToast(message: Throwable){
        _quantityStatus.value= QuantityStatus.ShowToast(message)
    }
    private fun setDialogLoading(loading: Boolean){
        _dialogQuantityStatus.value= DialogQuantityStatus.SetDialogLoading(loading)
    }
    private fun showDialogToast(message: Throwable){
        _dialogQuantityStatus.value= DialogQuantityStatus.ShowDialogToast(message)
    }
    fun startPick(companyId: Int, fulfillmentId: Int){
        viewModelScope.launch {
         startPickUseCase.startPick(companyId,fulfillmentId).onStart {
             setLoading(true)
         }.catch { exception ->
             setLoading(false)
             showToast(exception)
         }.collect { result ->
             setLoading(false)
             when(result){
             is BaseResult.Success -> _startStatus.value= StartStatus.SuccessStart(result.data)
             is BaseResult.Error -> _startStatus.value = StartStatus.ErrorStart(result.rawResponse)
                 }
            }
        }
    }

    private fun setGetListLoading(loading: Boolean){
        _getStartedStatus.value= GetStartedListStatus.SetStartedListLoading(loading)
    }
    private fun showGetStartedListToast(message: Throwable){
        _getStartedStatus.value= GetStartedListStatus.ShowStartedListToast(message)
    }
    fun getStartedListStarted(companyId: Int, fulfillmentId: Int){
        viewModelScope.launch {
            startedItemsUseCase.getStartedList(companyId,fulfillmentId).onStart {
                setGetListLoading(true)
            }.catch { exception ->
                setGetListLoading(false)
                showGetStartedListToast(exception)
            }.collect {result ->
                setGetListLoading(false)
                when(result){
                    is BaseResult.Success -> _getStartedStatus.value=
                        GetStartedListStatus.SuccessGetStartedList(result.data)
                    is BaseResult.Error -> _getStartedStatus.value=
                        GetStartedListStatus.ErrorGetStartedList(result.rawResponse)
                }
            }
        }
    }
    fun updateQuantity(companyId: Int, fulfillmentId: Int,itemCode: String){
        viewModelScope.launch {
                updateQuantityUseCase.updateQuantity(companyId,fulfillmentId,itemCode)
            .onStart {
                setUpdateLoading(true)
            }.catch {exception ->
                setUpdateLoading(false)
                showUpdateToast(exception)
            }.collect {result ->
                        setUpdateLoading(false)
                        when(result){
                    is BaseResult.Success -> _quantityStatus.value =
                        QuantityStatus.SuccessSendQuantity(result.data)
                    is BaseResult.Error -> _quantityStatus.value =
                        QuantityStatus.ErrorSendQuantity(result.rawResponse)
                }
            }
        }
    }
    fun updateDialogQuantity(companyId: Int, fulfillmentId: Int,updateQuantityRequest: UpdateQuantityRequest){
        viewModelScope.launch {
            updateDialogQuantityUseCase.updateDialogQuantity(companyId,fulfillmentId,updateQuantityRequest)
                .onStart {
                    setDialogLoading(true)
                }.catch {exception ->
                    setDialogLoading(false)
                    showDialogToast(exception)
                }.collect {result ->
                    setDialogLoading(false)
                    when(result){
                        is BaseResult.Success -> _dialogQuantityStatus.value =
                            DialogQuantityStatus.SuccessDialogQuantity(result.data)
                        is BaseResult.Error -> _dialogQuantityStatus.value =
                            DialogQuantityStatus.ErrorDialogQuantity(result.rawResponse)
                    }
                }
        }
    }
}
sealed class StartStatus{
    object Init: StartStatus()
    data class SuccessStart(val startPickEntity: StartPickEntity): StartStatus()
    data class ErrorStart(val startPickResponse: WrappedResponse<StartPickResponse>): StartStatus()
    data class SetLoading(val loading: Boolean): StartStatus()
    data class ShowToast(val message: Throwable): StartStatus()
}

sealed class GetStartedListStatus{
    object Init: GetStartedListStatus()
    data class SuccessGetStartedList(val startedItemsEntity: StartPickEntity): GetStartedListStatus()
    data class ErrorGetStartedList(val startPickResponse: WrappedResponse<StartedItemsResponse>):
        GetStartedListStatus()
    data class ShowStartedListToast(val message: Throwable): GetStartedListStatus()
    data class SetStartedListLoading(val loading: Boolean): GetStartedListStatus()
}

sealed class QuantityStatus{
    object Init: QuantityStatus()
    data class SuccessSendQuantity(val updateQuantityEntity: UpdateQuantityEntity): QuantityStatus()
    data class ErrorSendQuantity(val updateQuantityResponse: WrappedResponse<UpdateQuantityResponse>):QuantityStatus()
    data class ShowToast(val message: Throwable): QuantityStatus()
    data class SetLoading(val loading: Boolean): QuantityStatus()
}
sealed class DialogQuantityStatus{
    object Init: DialogQuantityStatus()
    data class SuccessDialogQuantity(val updateQuantityEntity: UpdateDialogQuantityEntity): DialogQuantityStatus()
    data class ErrorDialogQuantity(val updateQuantityResponse: WrappedResponse<UpdateDialogQuantityResponse>):DialogQuantityStatus()
    data class ShowDialogToast(val message: Throwable): DialogQuantityStatus()
    data class SetDialogLoading(val loading: Boolean): DialogQuantityStatus()
}

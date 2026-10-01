package com.silkysys.warehouse.app.putAway.putAwayProductDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.data.putAwayModule.deletePinLocation.dto.DeleteLocationRequest
import com.silkysys.warehouse.sharedcode.data.putAwayModule.deletePinLocation.dto.DeleteLocationResponse
import com.silkysys.warehouse.sharedcode.data.putAwayModule.putAwayProductDetails.dto.PutAwayProductDetailsRequest
import com.silkysys.warehouse.sharedcode.data.putAwayModule.putAwayProductDetails.dto.PutAwayProductDetailsResponse
import com.silkysys.warehouse.sharedcode.domain.putAwayModule.deletePinLocation.entity.DeleteLocationEntity
import com.silkysys.warehouse.sharedcode.domain.putAwayModule.deletePinLocation.useCase.DeleteLocationUseCase
import com.silkysys.warehouse.sharedcode.domain.putAwayModule.putAwayProductDetails.entity.PutAwayProductDetailsEntity
import com.silkysys.warehouse.sharedcode.domain.putAwayModule.putAwayProductDetails.useCase.PutAwayProductDetailsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch


class PutAwayProductDetailsViewModel (
    private val putAwayProductDetailsUseCase: PutAwayProductDetailsUseCase,
    private val deleteLocationUseCase: DeleteLocationUseCase
    ) :ViewModel() {

    private val _deleteStatus= MutableStateFlow<DeleteStatus>(DeleteStatus.Init)
    val deleteStatus: StateFlow<DeleteStatus> get() = _deleteStatus

        private val _assignStatus =
            MutableStateFlow<PutAwayProductDetailsStatus>(PutAwayProductDetailsStatus.Init)
        val assignStatus :StateFlow<PutAwayProductDetailsStatus> get() = _assignStatus

    private fun setLoading(loading: Boolean){
        _assignStatus.value = PutAwayProductDetailsStatus.SetLoading(loading)
    }
    private fun showToast(message: Throwable){
        _assignStatus.value = PutAwayProductDetailsStatus.ShowToast(message)
    }
    private fun setDeleteLoading(loading: Boolean){
        _deleteStatus.value = DeleteStatus.SetDeleteLoading(loading)
    }
    private fun showDeleteToast(message: Throwable){
        _deleteStatus.value = DeleteStatus.ShowDeleteToast(message)
    }
    fun assignProductToBinLocation(companyId: Int,putAwayProductDetailsRequest: PutAwayProductDetailsRequest){
        viewModelScope.launch {
            putAwayProductDetailsUseCase.assignProductToBinLocation(companyId,putAwayProductDetailsRequest)
                .onStart {
                    setLoading(true)
                }.catch {exception ->
                    setLoading(false)
                    showToast(exception)
                }.collect { result ->
                    setLoading(false)
                    when(result){
                    is BaseResult.Success ->
                        _assignStatus.value =
                            PutAwayProductDetailsStatus.SuccessAssign(result.data)
                    is BaseResult.Error ->
                        _assignStatus.value =
                            PutAwayProductDetailsStatus.ErrorAssign(result.rawResponse)
                }
             }
        }
    }
    fun deleteLocation(companyId: Int,deleteLocationRequest: DeleteLocationRequest){
        viewModelScope.launch {
            deleteLocationUseCase.deleteLocation(companyId,deleteLocationRequest)
                .onStart {
                    setDeleteLoading(true)
                }.catch {exception ->
                    setLoading(false)
                    showDeleteToast(exception)
                }.collect { result ->
                    setLoading(false)
                    when(result){
                     is BaseResult.Success -> _deleteStatus.value =
                         DeleteStatus.SuccessDelete(result.data)
                     is BaseResult.Error -> _deleteStatus.value =
                         DeleteStatus.ErrorDeleteLocation(result.rawResponse)
                    }
                }
        }
    }
}
sealed class PutAwayProductDetailsStatus{
    object Init: PutAwayProductDetailsStatus()
    data class SuccessAssign(val putAwayProductDetailsEntity: PutAwayProductDetailsEntity)
        : PutAwayProductDetailsStatus()
    data class ErrorAssign(val putAwayProductDetailsResponse: WrappedResponse<PutAwayProductDetailsResponse>)
        : PutAwayProductDetailsStatus()
    data class SetLoading(val loading: Boolean): PutAwayProductDetailsStatus()
    data class ShowToast(val message: Throwable): PutAwayProductDetailsStatus()
}
sealed class DeleteStatus{
    object Init: DeleteStatus()
    data class SuccessDelete(val deleteLocationEntity: DeleteLocationEntity)
        :DeleteStatus()
    data class ErrorDeleteLocation(val deleteLocationResponse: WrappedResponse<DeleteLocationResponse>)
        :DeleteStatus()
    data class ShowDeleteToast(val message: Throwable):DeleteStatus()
    data class SetDeleteLoading(val loading: Boolean): DeleteStatus()
}

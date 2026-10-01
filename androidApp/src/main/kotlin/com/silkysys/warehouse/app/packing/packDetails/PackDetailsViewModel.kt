package com.silkysys.warehouse.app.packing.packDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.data.packing.finishPack.dto.FinishPackResponse
import com.silkysys.warehouse.sharedcode.data.packing.packDetails.dto.PackDetailsResponse
import com.silkysys.warehouse.sharedcode.domain.packing.finishPack.entity.FinishPackEntity
import com.silkysys.warehouse.sharedcode.domain.packing.finishPack.useCase.FinishPackUseCase
import com.silkysys.warehouse.sharedcode.domain.packing.packDetails.entity.PackDetailsEntity
import com.silkysys.warehouse.sharedcode.domain.packing.packDetails.useCase.PackDetailsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

/**
 *
 * Created by: Mahmoud Ibrahim on 5/4/2023
 * Email: mahmoudibrahiem57@gmail.com
 *
 **/

class PackDetailsViewModel (
    private val packDetailsUseCase: PackDetailsUseCase,
    private val finishPackUseCase: FinishPackUseCase
    ): ViewModel() {

    private val _packStatus= MutableStateFlow<PackStatus>(PackStatus.Init)
    val packStatus: StateFlow<PackStatus> get() = _packStatus

    private val _finishStatus= MutableStateFlow<FinishStatus>(FinishStatus.Init)
    val finishStatus: StateFlow<FinishStatus> get() = _finishStatus

    private fun setLoading(loading: Boolean){
        _packStatus.value = PackStatus.SetLoading(loading)
    }
    private fun showToast(message: Throwable){
        _packStatus.value= PackStatus.ShowToast(message)
    }
    fun getPackDetails(companyId: Int,fulfillmentId: Int){
        viewModelScope.launch {
            packDetailsUseCase.getPAckDetails(companyId,fulfillmentId).onStart {
                setLoading(true)
            }.catch {exception->
                setLoading(false)
                showToast(exception)
            }.collect { result->
                 setLoading(false)
                when(result){
                    is BaseResult.Success -> _packStatus.value= PackStatus.SuccessPackDetails(result.data)
                    is BaseResult.Error -> _packStatus.value= PackStatus.ErrorPackDetails(result.rawResponse)
                }
            }
        }
    }

    private fun setFinishLoading(loading: Boolean){
        _finishStatus.value= FinishStatus.SetFinishLoading(loading)
    }

    private fun showFinishToast(message: Throwable){
        _finishStatus.value= FinishStatus.ShowFinishToast(message)
    }
    fun finishPack(companyId: Int,fulfillmentId: Int){
    viewModelScope.launch {
        finishPackUseCase.finishPack(companyId,fulfillmentId).onStart {
            setLoading(true)
        }.catch {exception->
            setLoading(false)
            showFinishToast(exception)
        }.collect {result->
            setLoading(false)
            when(result){
                is BaseResult.Success -> _finishStatus.value= FinishStatus.SuccessFinis(result.data)
                is BaseResult.Error -> _finishStatus.value= FinishStatus.ErrorFinish(result.rawResponse)
                }
            }
        }
    }
}
sealed class PackStatus{
    object Init: PackStatus()
    data class SuccessPackDetails(val packDetailsEntity: PackDetailsEntity): PackStatus()
    data class ErrorPackDetails(val packDetailsResponse: WrappedResponse<PackDetailsResponse>): PackStatus()
    data class ShowToast(val message: Throwable): PackStatus()
    data class SetLoading(val loading: Boolean): PackStatus()
}
sealed class FinishStatus{
    object Init: FinishStatus()
    data class SuccessFinis(val finishPackEntity: FinishPackEntity): FinishStatus()
    data class ErrorFinish(val finishPackResponse: WrappedResponse<FinishPackResponse>): FinishStatus()
    data class ShowFinishToast(val message: Throwable): FinishStatus()
    data class SetFinishLoading(val loading: Boolean): FinishStatus()
}

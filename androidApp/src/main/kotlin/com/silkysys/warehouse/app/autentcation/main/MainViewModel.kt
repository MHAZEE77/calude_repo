package com.silkysys.warehouse.app.autentcation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.silkysys.warehouse.domain.autentcation.logOut.entity.LogOutEntity
import com.silkysys.warehouse.sharedcode.data.autentcation.logOut.dto.LogOutResponse
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.domain.addstock.ProductInfoEntity
import com.silkysys.warehouse.sharedcode.domain.addstock.ProductInfoResponse
import com.silkysys.warehouse.sharedcode.domain.autentcation.logOut.useCase.LogOutUseCase
import com.silkysys.warehouse.sharedcode.domain.autentcation.warehouses.useCase.WareHousesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch


class MainViewModel (
    private val wareHousesUseCase: WareHousesUseCase,
    private val logOutUseCase: LogOutUseCase
) : ViewModel() {

    // state for logOut
    private val _logOutState = MutableStateFlow<LogOutStatus>(LogOutStatus.Init)
    val logOutStatus: StateFlow<LogOutStatus> get() = _logOutState

    private val _productInfo = MutableStateFlow<ProductInfoStatus>(ProductInfoStatus.Init)
    val productInfo: StateFlow<ProductInfoStatus> get() = _productInfo

    private fun showProductInfoToast(exception: Throwable) {
        _productInfo.value = ProductInfoStatus.ShowToast(exception)
    }
    private fun setProductLoading(loading: Boolean) {
        _productInfo.value = ProductInfoStatus.IsLoading(loading)
    }
    fun resetProductInfo(){
        _productInfo.value = ProductInfoStatus.Init
    }

    private fun showLogOutToast(message: String) {
        _logOutState.value = LogOutStatus.ShowLogOutToast(message)
    }
    private fun setLogOutLoading(loading: Boolean) {
        _logOutState.value = LogOutStatus.IsLoading(loading)
    }

    fun logOut(companyId: Int) {
        viewModelScope.launch {
            logOutUseCase.logOut(companyId).onStart {
                setLogOutLoading(true)
            }.catch { exception ->
                setLogOutLoading(false)
                showLogOutToast(exception.message.toString())
            }.collect { baseResult ->
                setLogOutLoading(false)
                when (baseResult) {
                    is BaseResult.Success ->
                        _logOutState.value = LogOutStatus.SuccessLogOut(baseResult.data)

                    is BaseResult.Error ->
                        _logOutState.value = LogOutStatus.ErrorLogOut(baseResult.rawResponse)
                }
            }
        }
    }

    fun getProductInfo(code : String) {
        viewModelScope.launch {
            wareHousesUseCase.getProductInfo(code).onStart {
                setProductLoading(true)
            }.catch { exception ->
                setProductLoading(false)
                showProductInfoToast(exception)
            }.collect { baseResult ->
                setProductLoading(false)
                when (baseResult) {
                    is BaseResult.Success -> _productInfo.value =
                        ProductInfoStatus.SuccessProduct(baseResult.data)

                    is BaseResult.Error -> _productInfo.value =
                        ProductInfoStatus.ErrorProduct(baseResult.rawResponse)
                }
            }
        }
    }
}

sealed class ProductInfoStatus() {
    object Init : ProductInfoStatus()
    data class IsLoading(val loading: Boolean) : ProductInfoStatus()
    data class ShowToast(val exception: Throwable) : ProductInfoStatus()
    data class SuccessProduct(val productInfoEntity: ProductInfoEntity) : ProductInfoStatus()
    data class ErrorProduct(val productInfoResponse: WrappedResponse<ProductInfoResponse>) :
        ProductInfoStatus()
}

sealed class LogOutStatus() {
    object Init : LogOutStatus()
    data class SuccessLogOut(val logOutEntity: LogOutEntity) : LogOutStatus()
    data class ErrorLogOut(val logOutResponse: WrappedResponse<LogOutResponse>) : LogOutStatus()
    data class IsLoading(val loading: Boolean) : LogOutStatus()
    data class ShowLogOutToast(val message: String) : LogOutStatus()
}

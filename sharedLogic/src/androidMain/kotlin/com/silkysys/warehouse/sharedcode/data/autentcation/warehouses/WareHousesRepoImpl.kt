package com.silkysys.warehouse.sharedcode.data.autentcation.warehouses


import com.silkysys.warehouse.sharedcode.data.remote.ApiService
import com.silkysys.warehouse.sharedcode.Utility.parseBodyOrDefault
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.domain.addstock.ProductInfoEntity
import com.silkysys.warehouse.sharedcode.domain.addstock.ProductInfoResponse
import com.silkysys.warehouse.sharedcode.domain.autentcation.warehouses.WareHousesRepo
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow



class WareHousesRepoImpl  (private val apiService: ApiService) : WareHousesRepo {

    override suspend fun getProductInfo(code: String):
            Flow<BaseResult<ProductInfoEntity, WrappedResponse<ProductInfoResponse>>> {
        return flow {
            val response = apiService.getProductInfo(code)
            response.parseBodyOrDefault<WrappedResponse<ProductInfoResponse>>().let { parsed ->
                val data = parsed.data
                if (response.status.isSuccess() && data != null) {
                    emit(BaseResult.Success(data))
                } else {
                    emit(BaseResult.Error(parsed))
                }
            }
        }
    }
}






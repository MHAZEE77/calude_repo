package com.silkysys.warehouse.sharedcode.domain.autentcation.warehouses

import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.domain.addstock.ProductInfoEntity
import com.silkysys.warehouse.sharedcode.domain.addstock.ProductInfoResponse
import kotlinx.coroutines.flow.Flow


interface WareHousesRepo {

    suspend fun getProductInfo(code: String):
            Flow<BaseResult<ProductInfoEntity, WrappedResponse<ProductInfoResponse>>>

}

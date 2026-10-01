package com.silkysys.warehouse.sharedcode.domain.autentcation.warehouses.useCase

import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.domain.addstock.ProductInfoEntity
import com.silkysys.warehouse.sharedcode.domain.addstock.ProductInfoResponse
import com.silkysys.warehouse.sharedcode.domain.autentcation.warehouses.WareHousesRepo
import kotlinx.coroutines.flow.Flow


class WareHousesUseCase(private val wareHousesRepo: WareHousesRepo) {

    suspend fun getProductInfo(code: String):
            Flow<BaseResult<ProductInfoEntity, WrappedResponse<ProductInfoResponse>>> {
        return wareHousesRepo.getProductInfo(code)
    }

}

package com.silkysys.warehouse.sharedcode.data.putAwayModule.putAway

import android.annotation.SuppressLint
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.data.putAwayModule.putAway.dto.putAwayResponse.PutAwayScanResponse
import com.silkysys.warehouse.sharedcode.data.remote.ApiService
import com.silkysys.warehouse.sharedcode.Utility.parseBodyOrDefault
import com.silkysys.warehouse.sharedcode.domain.putAwayModule.putAway.PutAwayRepo
import com.silkysys.warehouse.sharedcode.domain.putAwayModule.putAway.entity.PutAwayScanEntity
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Created by: Mahmoud Ibrahim on 3/19/2023
 * Email: mahmoudibrahiem57@gmail.com
 **/
class PutAwayRepoImpl (private val apiService: ApiService) : PutAwayRepo {
    @SuppressLint("SuspiciousIndentation")
    override suspend fun putAwayScan(
        itemBarcode: String
    ): Flow<BaseResult<PutAwayScanEntity, WrappedResponse<PutAwayScanResponse>>> {
        return flow {
            val response = apiService.putAwayScan(itemBarcode)

            response.parseBodyOrDefault<WrappedResponse<PutAwayScanResponse>>().let { parsed ->

                val product = parsed.data?.item
                if (response.status.isSuccess() && product != null) {
                    val binLocations = product.binLocations.map { binLocation ->
                        PutAwayScanEntity.Item.BinLocations(
                            binLocation.id,
                            binLocation.company_id,
                            binLocation.warehouse_id,
                            binLocation.code
                        )
                    }

                    emit(
                        BaseResult.Success(
                            PutAwayScanEntity(
                                PutAwayScanEntity.Item(
                                    product.item_img,
                                    product.barcode, product.companyId,
                                    product.createdAt, product.defaultWarehouse,
                                    product.id, product.itemId,
                                    product.itemName, product.itemQuantity, product.openingStock,
                                    product.openingStockValue, product.reorderLevel,
                                    product.returnable, product.sku,
                                    PutAwayScanEntity.Item.Category(product.item_category.name),
                                    binLocations.toMutableList()
                                )
                            )
                        )
                    )
                } else {
                    emit(BaseResult.Error(parsed))
                }
            }
        }
    }
}

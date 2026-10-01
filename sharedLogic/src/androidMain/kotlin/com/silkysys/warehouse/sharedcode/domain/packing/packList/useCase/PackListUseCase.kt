package com.silkysys.warehouse.sharedcode.domain.packing.packList.useCase

import com.silkysys.warehouse.sharedcode.data.packing.packList.dto.PackResponse
import com.silkysys.warehouse.sharedcode.domain.packing.packList.PackListRepo
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.domain.packing.packList.entity.PackListEntity
import kotlinx.coroutines.flow.Flow

/**
 *
 * Created by: Mahmoud Ibrahim on 5/1/2023
 * Email: mahmoudibrahiem57@gmail.com
 *
 **/
class PackListUseCase  (private val packListRepo: PackListRepo) {
    suspend fun getPackList(companyId: Int, status: String):
            Flow<BaseResult<PackListEntity, WrappedResponse<PackResponse>>> {
        return packListRepo.getPackList(companyId,status)
    }
}

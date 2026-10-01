package com.silkysys.warehouse.sharedcode.domain.putAwayModule.putAwayProductDetails

import com.silkysys.warehouse.sharedcode.data.base.WrappedResponse
import com.silkysys.warehouse.sharedcode.data.putAwayModule.putAwayProductDetails.dto.PutAwayProductDetailsRequest
import com.silkysys.warehouse.sharedcode.data.putAwayModule.putAwayProductDetails.dto.PutAwayProductDetailsResponse
import com.silkysys.warehouse.sharedcode.data.base.BaseResult
import com.silkysys.warehouse.sharedcode.domain.putAwayModule.putAwayProductDetails.entity.PutAwayProductDetailsEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by: Mahmoud Ibrahim on 3/21/2023
 * Email: mahmoudibrahiem57@gmail.com
 **/
interface PutAwayProductDetailsRepo {
    suspend fun assignProductToBinLocation(companyId: Int,putAwayProductDetailsRequest: PutAwayProductDetailsRequest)
    :Flow<BaseResult<PutAwayProductDetailsEntity, WrappedResponse<PutAwayProductDetailsResponse>>>
}

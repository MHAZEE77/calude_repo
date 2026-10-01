package com.silkysys.warehouse.sharedcode.domain.putAwayModule.putAwayProductDetails.entity

import android.os.Parcelable
import com.silkysys.warehouse.sharedcode.domain.putAwayModule.putAway.entity.PutAwayScanEntity
import kotlinx.parcelize.Parcelize


/**
 * Created by: Mahmoud Ibrahim on 3/21/2023
 * Email: mahmoudibrahiem57@gmail.com
 **/
@Parcelize
data class PutAwayProductDetailsEntity (
    val binLocations: MutableList<PutAwayScanEntity.Item.BinLocations> ): Parcelable

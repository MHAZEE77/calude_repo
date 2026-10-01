package com.silkysys.warehouse.sharedcode.domain.putAwayModule.deletePinLocation.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 *
 * Created by: Mahmoud Ibrahim on 4/18/2023
 * Email: mahmoudibrahiem57@gmail.com
 *
 **/
@Parcelize
data class DeleteLocationEntity(
    val binLocations: MutableList<BinLocations>
    ): Parcelable {
    @Parcelize
    data class BinLocations(
        val id: Int,
        val company_id: Int,
        val warehouse_id: Int,
        val code: String
    ) : Parcelable
}

package com.silkysys.warehouse.domain.entity

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.*

@Serializable
@Parcelize
class PickOrder (
    var id: Int,
//    var user_id: Int?,
//    var is_manually_assigned: Int,
    var is_scanned_required: Int,
    var is_scanned_required_qty: Int,
    var location_id: Int,
    var rack: String?,
//    var sort: Int?,
    var picked_qty_sum: Int,
    var quantity_sum: Int,
    var pick_items_count: Int,
//    var sort_type: String?,
    var pick_order_time: String?,
    var created_at: String,
//    var started_at: String?,
    var status: String
): Parcelable {
    object Status {
        const val pending = "pending"
        const val started = "started"
        const val picked = "picked"
        const val cancelled = "cancelled"
    }

    val pickOrderTimeDate: Date?
      @SuppressLint("SimpleDateFormat")
      get() = pick_order_time?.let { value ->
          runCatching { SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(value) }.getOrNull()
      }
}

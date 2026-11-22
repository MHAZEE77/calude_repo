package com.silkysys.warehouse.app.ui.compose_ui.pick

import com.silkysys.warehouse.sharedcode.domain.picking.pick.entity.PickEntity

internal val previewStartedItems = listOf(
    PickEntity.Fulfillments.Started(
        created_at = "", id = 1001, name = "SO-2001", total_items = 12,
        total_items_picked_quantity = 4, total_items_quantity = 12, status = "picking",
    ),
    PickEntity.Fulfillments.Started(
        created_at = "", id = 1002, name = "SO-2002", total_items = 8,
        total_items_picked_quantity = 0, total_items_quantity = 8, status = "ready_picking",
    ),
    PickEntity.Fulfillments.Started(
        created_at = "", id = 1003, name = "SO-2003", total_items = 3,
        total_items_picked_quantity = 1, total_items_quantity = 3, status = "other",
    ),
)

internal val previewWaitingItems = listOf(
    PickEntity.Fulfillments.Waiting(
        created_at = "", id = 2001, name = "SO-3001", total_items = 5,
        total_items_picked_quantity = 0, total_items_quantity = 0, status = "",
    ),
    PickEntity.Fulfillments.Waiting(
        created_at = "", id = 2002, name = "SO-3002", total_items = 9,
        total_items_picked_quantity = 0, total_items_quantity = 0, status = "",
    ),
    PickEntity.Fulfillments.Waiting(
        created_at = "", id = 2003, name = "SO-3003", total_items = 14,
        total_items_picked_quantity = 0, total_items_quantity = 0, status = "",
    ),
)
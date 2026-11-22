package com.silkysys.warehouse.app.ui.compose_ui.pick

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.silkysys.warehouse.R
import com.silkysys.warehouse.sharedcode.domain.picking.pick.entity.PickEntity


@Composable
fun PickStartedItem(
    item: PickEntity.Fulfillments.Started,
    onClick: (PickEntity.Fulfillments.Started) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Same status styling as PickStartedAdapter.onBindViewHolder
    val statusText: String
    val statusColor: Color
    when (item.status) {
        "picking" -> {
            statusText = stringResource(R.string.picking_label)
            statusColor = colorResource(R.color.warning)
        }
        "ready_picking" -> {
            statusText = stringResource(R.string.ready_picking)
            statusColor = colorResource(R.color.colorAccent)
        }
        else -> {
            statusText = item.status
            statusColor = colorResource(R.color.colorBlack)
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(5.dp) // layout_margin
            .clip(RoundedCornerShape(5.dp))
            .itemsBorder()
            .clickable(role = Role.Button) { onClick(item) }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ItemCell("#" + item.id, weight = 0.7f)
        ItemCell(item.name, weight = 1.1f)
        ItemCell(
            item.total_items_picked_quantity.toString() + "/" + item.total_items_quantity.toString(),
            weight = 1.1f,
        )
        ItemCell(statusText, weight = 1.1f, color = statusColor, modifier = Modifier.padding(start = 4.dp))
    }
}

@Composable
private fun RowScope.ItemCell(
    text: String,
    weight: Float,
    modifier: Modifier = Modifier,
    color: Color = colorResource(R.color.colorBlack),
) {
    BasicText(
        text = text,
        modifier = modifier.weight(weight),
        style = TextStyle(
            color = color,
            fontSize = 14.sp,
            fontFamily = PickRegularFont,
            textAlign = TextAlign.Center,
        ),
    )
}

@Preview(showBackground = true, name = "Started items (all statuses)")
@Composable
private fun PickStartedItemPreview() {
    Column {
        previewStartedItems.forEach { PickStartedItem(item = it, onClick = {}) }
    }
}
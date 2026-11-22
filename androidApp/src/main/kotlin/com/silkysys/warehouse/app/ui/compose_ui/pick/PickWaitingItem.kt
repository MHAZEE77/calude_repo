package com.silkysys.warehouse.app.ui.compose_ui.pick

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.silkysys.warehouse.R
import com.silkysys.warehouse.sharedcode.domain.picking.pick.entity.PickEntity

import androidx.compose.runtime.getValue

import androidx.compose.runtime.setValue

@Composable
fun PickWaitingItem(
    item: PickEntity.Fulfillments.Waiting,
    selected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp) // layout_margin
            .background(colorResource(R.color.colorBackgroundLight)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PickCheckBox(
            checked = selected,
            onCheckedChange = { onToggle() },
            modifier = Modifier.padding(start = 16.dp),
        )
        // cardView (MaterialCardView, 5dp corners, no elevation)
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(5.dp))
                .semantics { contentDescription = "Order ${item.name}, ${item.total_items} items" }
                .itemsBorder()
                .clickable(onClick = onToggle)
                .padding(start = 8.dp, top = 16.dp, end = 8.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ItemCell("#" + item.id, weight = 0.4f)
            ItemCell(item.name, weight = 1.6f)
            ItemCell(item.total_items.toString(), weight = 1f)
        }
    }
}

@Composable
private fun RowScope.ItemCell(text: String, weight: Float) {
    BasicText(
        text = text,
        modifier = Modifier.weight(weight),
        style = TextStyle(
            color = PickTextColorPrimary,
            fontSize = 16.sp, // TextAppearance.AppCompat.Subhead
            textAlign = TextAlign.Center,
        ),
    )
}

@Preview(showBackground = true, name = "Waiting item")
@Composable
private fun PickWaitingItemPreview() {
    val item = PickEntity.Fulfillments.Waiting(
        created_at = "",
        id = 1024,
        name = "SO-2045",
        total_items = 12,
        total_items_picked_quantity = 0,
        total_items_quantity = 0,
        status = "",
    )
    var selected by remember { mutableStateOf(false) }

    Column {
        // Toggles when tapped in Interactive Mode
        PickWaitingItem(item = item, selected = selected, onToggle = { selected = !selected })
        // Always-selected state for a static check
        PickWaitingItem(item = item, selected = true, onToggle = {})
    }
}


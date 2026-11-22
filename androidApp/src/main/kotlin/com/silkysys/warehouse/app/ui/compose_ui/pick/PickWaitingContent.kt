package com.silkysys.warehouse.app.ui.compose_ui.pick

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.silkysys.warehouse.R
import com.silkysys.warehouse.sharedcode.domain.picking.pick.entity.PickEntity
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickWaitingContent(
    waiting: List<PickEntity.Fulfillments.Waiting>,
    selected: List<PickEntity.Fulfillments.Waiting>,
    isEmptyVisible: Boolean,
    isLoading: Boolean,
    onToggleAll: () -> Unit,
    onToggleItem: (PickEntity.Fulfillments.Waiting) -> Unit,
    onStart: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.colorBackgroundLight)),
    ) {
        if (isEmptyVisible) {
            PickEmptyContainer(onRefresh = onRefresh)
        } else {
            PullToRefreshBox(
                isRefreshing = isLoading,
                onRefresh = onRefresh,
                modifier = Modifier.fillMaxSize(),
            ) {
                // recyclerView + start bar were INVISIBLE while loading
                Column(modifier = Modifier.fillMaxSize().alpha(if (isLoading) 0f else 1f)) {
                    LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        item {
                            PickWaitingHeaderItem(
                                allSelected = waiting.isNotEmpty() && selected.size == waiting.size,
                                onToggleAll = onToggleAll,
                            )
                        }
                        items(waiting, key = { it.id }) { item ->
                            PickWaitingItem(
                                item = item,
                                selected = selected.any { it.id == item.id },
                                onToggle = { onToggleItem(item) },
                            )
                        }
                    }
                    StartBar(count = selected.size, onClick = onStart)
                }
            }
        }

        if (isLoading) StartedPlaceHolderList(rows = 14)
    }
}

/** The CardView (16dp margin, 4dp corners) holding `startContainer`: "START" + selected-count badge. */
@Composable
private fun StartBar(count: Int, onClick: () -> Unit) {
    val shape = RoundedCornerShape(4.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(elevation = 2.dp, shape = shape)
            .clip(shape)
            .background(colorResource(R.color.colorPrimary))
            .height(48.dp)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // TextAppearance.AppCompat.Widget.Button.Inverse (14sp, medium, all caps)
            BasicText(
                text = stringResource(R.string.start).uppercase(),
                modifier = Modifier.padding(start = 8.dp),
                style = TextStyle(
                    color = colorResource(R.color.colorWhite),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                ),
            )
            // startCountTextView inside the round MaterialCardView (26dp, white 1dp stroke)
            Box(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(26.dp)
                    .border(1.dp, colorResource(R.color.colorWhite), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                BasicText(
                    text = count.toString(),
                    style = TextStyle(
                        color = colorResource(R.color.colorWhite),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Waiting: list (interactive)")
@Composable
private fun PickWaitingContentListPreview() {
    val selected = remember { mutableStateListOf(previewWaitingItems[0]) }
    PickWaitingContent(
        waiting = previewWaitingItems,
        selected = selected,
        isEmptyVisible = false,
        isLoading = false,
        onToggleAll = {
            if (selected.size == previewWaitingItems.size) selected.clear()
            else { selected.clear(); selected.addAll(previewWaitingItems) }
        },
        onToggleItem = { if (!selected.remove(it)) selected.add(it) },
        onStart = {},
        onRefresh = {},
    )
}

@Preview(showBackground = true, showSystemUi = true, name = "Waiting: loading")
@Composable
private fun PickWaitingContentLoadingPreview() {
    PickWaitingContent(
        waiting = emptyList(),
        selected = emptyList(),
        isEmptyVisible = false,
        isLoading = true,
        onToggleAll = {},
        onToggleItem = {},
        onStart = {},
        onRefresh = {},
    )
}

@Preview(showBackground = true, showSystemUi = true, name = "Waiting: empty")
@Composable
private fun PickWaitingContentEmptyPreview() {
    PickWaitingContent(
        waiting = emptyList(),
        selected = emptyList(),
        isEmptyVisible = true,
        isLoading = false,
        onToggleAll = {},
        onToggleItem = {},
        onStart = {},
        onRefresh = {},
    )
}
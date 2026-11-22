package com.silkysys.warehouse.app.ui.compose_ui.pick

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.silkysys.warehouse.R
import com.silkysys.warehouse.sharedcode.domain.picking.pick.entity.PickEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickStartedContent(
    started: List<PickEntity.Fulfillments.Started>,
    isEmptyVisible: Boolean,
    isLoading: Boolean,
    onItemClick: (PickEntity.Fulfillments.Started) -> Unit,
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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp) // recyclerView margins
                        .alpha(if (isLoading) 0f else 1f), // recyclerView was INVISIBLE while loading
                ) {
                    item { PickStartedHeaderItem() }
                    items(started) { model -> PickStartedItem(item = model, onClick = onItemClick) }
                }
            }
        }

        if (isLoading) StartedPlaceHolderList(rows = 16)
    }
}


@Preview(showBackground = true, showSystemUi = true, name = "Started: list")
@Composable
private fun PickStartedContentListPreview() {
    PickStartedContent(
        started = previewStartedItems,
        isEmptyVisible = false,
        isLoading = false,
        onItemClick = {},
        onRefresh = {},
    )
}

@Preview(showBackground = true, showSystemUi = true, name = "Started: loading")
@Composable
private fun PickStartedContentLoadingPreview() {
    PickStartedContent(
        started = emptyList(),
        isEmptyVisible = false,
        isLoading = true,
        onItemClick = {},
        onRefresh = {},
    )
}

@Preview(showBackground = true, showSystemUi = true, name = "Started: empty")
@Composable
private fun PickStartedContentEmptyPreview() {
    PickStartedContent(
        started = emptyList(),
        isEmptyVisible = true,
        isLoading = false,
        onItemClick = {},
        onRefresh = {},
    )
}
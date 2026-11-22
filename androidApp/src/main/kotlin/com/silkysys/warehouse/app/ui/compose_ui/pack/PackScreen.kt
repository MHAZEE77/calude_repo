package com.silkysys.warehouse.app.ui.compose_ui.pack

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.silkysys.warehouse.R
import com.silkysys.warehouse.sharedcode.domain.packing.packList.entity.PackListEntity

private val BoldMediumFont = FontFamily(Font(R.font.bold_medium))

@Composable
fun PackScreen(
    packList: List<PackListEntity.Fulfillments>,
    isEmptyVisible: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onBack: () -> Unit,
    onItemClick: (PackListEntity.Fulfillments) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Old shape: android:angle="270" (top -> bottom), startColor = gradient_end, endColor = gradient_start
    val gradient = Brush.verticalGradient(
        colors = listOf(
            colorResource(R.color.gradient_end),
            colorResource(R.color.gradient_start),
        ),
    )

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // header: AppBarLayout (fitsSystemWindows -> statusBarsPadding)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(gradient)
                    .statusBarsPadding(),
            ) {
                // toolbar (actionBarSize)
                Box(modifier = Modifier.fillMaxWidth().height(56.dp)) {
                    // iv_back
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .size(48.dp)
                            .clickable(role = Role.Button, onClick = onBack),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.back_icon),
                            contentDescription = stringResource(R.string.back),
                            modifier = Modifier.size(24.dp),
                        )
                    }
                    BasicText(
                        text = stringResource(R.string.pack_list),
                        modifier = Modifier.align(Alignment.Center),
                        style = TextStyle(
                            color = colorResource(R.color.colorLight),
                            fontSize = 24.sp,
                            fontFamily = BoldMediumFont,
                        ),
                    )
                }

                SearchField(value = searchQuery, onValueChange = onSearchQueryChange)
            }

            // rvPackList (4dp side margins)
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
            ) {
                items(packList) { item -> PackItem(item = item, onClick = onItemClick) }
            }
        }

        // linEmptyContainer (layout_centerInParent)
        if (isEmptyVisible) {
            Column(
                modifier = Modifier.align(Alignment.Center).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(R.drawable.empty_ic),
                    contentDescription = null, // decorative, matches importantForAccessibility="no"
                    modifier = Modifier.fillMaxWidth().height(280.dp),
                    contentScale = ContentScale.Fit,
                )
                BasicText(
                    text = stringResource(R.string.pack_list_is_empty),
                    modifier = Modifier.fillMaxWidth(),
                    style = TextStyle(
                        color = Color(0xDE000000),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                    ),
                )
            }
        }
    }
}

/**
 * The `searchEditText`: 56dp high, 4dp margin, `@drawable/rectangle_bg` (white box, 5dp corners,
 * inset 4dp), search icon at the start, single line.
 */
@Composable
private fun SearchField(value: String, onValueChange: (String) -> Unit) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp) // layout_margin
            .height(56.dp)
            .padding(4.dp) // inset of the white box inside rectangle_bg
            .background(Color.White, RoundedCornerShape(5.dp))
            .padding(start = 6.dp), // paddingStart 10dp from the view edge (4dp already used by the inset)
        singleLine = true,
        textStyle = TextStyle(color = Color(0xDE000000), fontSize = 18.sp),
        cursorBrush = SolidColor(colorResource(R.color.colorPrimary)),
        decorationBox = { innerTextField ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                // drawableStart = ic_search (tinted textColorDarker), drawablePadding 8dp
                Image(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(Modifier.width(8.dp))
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        BasicText(
                            text = stringResource(R.string.search_for_filter_result),
                            style = TextStyle(
                                color = colorResource(R.color.textColorDarker),
                                fontSize = 18.sp,
                            ),
                        )
                    }
                    innerTextField()
                }
            }
        },
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PackScreenEmptyPreview() {
    PackScreen(
        packList = emptyList(),
        isEmptyVisible = true,
        searchQuery = "",
        onSearchQueryChange = {},
        onBack = {},
        onItemClick = {},
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PackScreenPreview() {
    PackScreen(
        packList = listOf(
            previewPackItem(1, "WH/PACK/00001"),
            previewPackItem(2, "WH/PACK/00002"),
            previewPackItem(3, "WH/PACK/00003"),
        ),
        isEmptyVisible = false,
        searchQuery = "",
        onSearchQueryChange = {},
        onBack = {},
        onItemClick = {},
    )
}

private fun previewPackItem(id: Int, name: String) = PackListEntity.Fulfillments(
    id = id,
    name = name,
    status = "packing",
    created_at = "2026-07-12T09:30:00+03:00",
    document = PackListEntity.Fulfillments.Document(document_name = "Sales Order"),
    warehouse = PackListEntity.Fulfillments.Warehouse(name = "Main Warehouse"),
)


package com.silkysys.warehouse.app.ui.compose_ui.pick

import android.view.View
import android.widget.ImageView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.load
import coil.size.Scale
import com.silkysys.warehouse.R
import com.silkysys.warehouse.sharedcode.domain.picking.startPick.entity.StartPickEntity

private typealias PickItem = StartPickEntity.Fulfillment.Item

/**
 * Compose replacement for `activity_pick_items.xml`, `pick_items_row.xml` and the
 * RecyclerView/PickItemsAdapter pair.
 *
 * @param showStart true while the "Start" button covers the list (order not started yet).
 */
@Composable
fun PickItemsScreen(
    title: String,
    items: List<PickItem>,
    showStart: Boolean,
    onBackClick: () -> Unit,
    onScanClick: () -> Unit,
    onStartClick: () -> Unit,
    onItemClick: (PickItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by rememberSaveable { mutableStateOf("") }
    val visibleItems = filterPickItems(items, query)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.colorBackgroundLight)),
    ) {
        PickItemsHeader(
            title = title,
            query = query,
            onQueryChange = { query = it },
            onBackClick = onBackClick,
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            if (showStart) {
                // startPickOrderContainer (only the Start button was visible in the XML)
                PickStartContainer(onStartClick = onStartClick)
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 4.dp),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 16.dp),
                ) {
                    items(visibleItems) { item ->
                        PickItemRow(item = item, onClick = onItemClick)
                    }
                }

                // civ_scan
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(20.dp)
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(colorResource(R.color.colorAccent))
                        .clickable(role = Role.Button, onClick = onScanClick),
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_light_barcode),
                        contentDescription = stringResource(R.string.scan),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        colorFilter = ColorFilter.tint(Color.White),
                    )
                }
            }
        }
    }
}

/**
 * Same matching the old PickItemsAdapter.filter used: barcode contains the query, or the
 * name contains it as typed, or the lower-cased name contains it. Empty query = everything.
 */
private fun filterPickItems(items: List<PickItem>, query: String): List<PickItem> {
    if (query.isEmpty()) return items
    return items.filter { item ->
        item.barcode?.contains(query) == true ||
                item.name.contains(query) ||
                item.name.lowercase().contains(query)
    }
}

/** AppBarLayout (gradient) + Toolbar (back + title) + search field. */
@Composable
private fun PickItemsHeader(
    title: String,
    query: String,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
) {
    val gradient = Brush.verticalGradient(
        colors = listOf(
            colorResource(R.color.gradient_end),
            colorResource(R.color.gradient_start),
        ),
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(gradient)
            .statusBarsPadding(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(48.dp)
                    .clickable(role = Role.Button, onClick = onBackClick),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.back_icon),
                    contentDescription = stringResource(R.string.back),
                )
            }

            BasicText(
                text = title,
                modifier = Modifier.align(Alignment.Center),
                style = TextStyle(
                    color = colorResource(R.color.colorLight),
                    fontSize = 24.sp,
                    fontFamily = PickBoldMediumFont,
                ),
            )
        }

        // search row: margin 6dp (8dp start) + padding 4dp
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, top = 10.dp, end = 10.dp, bottom = 10.dp),
        ) {
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .background(Color.White, RoundedCornerShape(5.dp)),
                singleLine = true,
                textStyle = TextStyle(
                    color = PickTextColorPrimary,
                    fontSize = 16.sp,
                    fontFamily = PickRegularFont,
                ),
                cursorBrush = SolidColor(colorResource(R.color.colorPrimary)),
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_search),
                            contentDescription = null,
                        )
                        Spacer(Modifier.width(12.dp))
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            if (query.isEmpty()) {
                                BasicText(
                                    text = stringResource(R.string.enter_item_or_scan_code),
                                    style = TextStyle(
                                        color = colorResource(R.color.textColor),
                                        fontSize = 16.sp,
                                        fontFamily = PickRegularFont,
                                    ),
                                )
                            }
                            innerTextField()
                        }
                    }
                },
            )
        }
    }
}

@Composable
private fun PickStartContainer(onStartClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.colorBackgroundLight)),
        contentAlignment = Alignment.Center,
    ) {
        // startButton: button_style + round_edges (colorPrimary, 10dp), 56dp high
        Box(
            modifier = Modifier
                .defaultMinSize(minWidth = 88.dp)
                .height(56.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(colorResource(R.color.colorPrimary))
                .clickable(role = Role.Button, onClick = onStartClick)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            BasicText(
                text = stringResource(R.string.start),
                style = TextStyle(
                    color = colorResource(R.color.colorWhite),
                    fontSize = 14.sp,
                    fontFamily = PickRegularFont,
                ),
            )
        }
    }
}

/** `pick_items_row.xml` + the bind code of PickItemsAdapter. */
@Composable
private fun PickItemRow(
    item: PickItem,
    onClick: (PickItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val remain = item.quantity - item.picked_quantity
    val subtitle = item.description.orEmpty().let { description ->
        if (description.length < 25) description else description.take(22).plus(" ...")
    }
    val summary = "${item.name}. Remaining $remain, picked ${item.picked_quantity}. ${item.status}"
    val shape = RoundedCornerShape(4.dp)
    val labelColor = colorResource(R.color.textColorDarker)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .shadow(1.dp, shape)
            .clip(shape)
            .background(Color.White)
            .clickable(role = Role.Button) { onClick(item) }
            .semantics(mergeDescendants = true) { contentDescription = summary },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 12.dp),
        ) {
            PickItemImage(
                url = item.image,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .size(80.dp)
                    .clip(shape),
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
            ) {
                BasicText(
                    text = item.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        color = PickTextColorPrimary,
                        fontSize = 16.sp,
                        fontFamily = PickBoldMediumFont,
                    ),
                )
                BasicText(
                    text = subtitle,
                    style = TextStyle(
                        color = labelColor,
                        fontSize = 14.sp,
                        fontFamily = PickRegularFont,
                    ),
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    QuantityChip(
                        label = stringResource(R.string.remain),
                        value = remain.toString(),
                        background = colorResource(R.color.lightBackground),
                        modifier = Modifier.weight(1f),
                    )
                    QuantityChip(
                        label = stringResource(R.string.picked),
                        value = item.picked_quantity.toString(),
                        background = colorResource(R.color.accentLight),
                        modifier = Modifier.weight(1f),
                    )
                }

                Row(modifier = Modifier.padding(top = 2.dp)) {
                    BasicText(
                        text = stringResource(R.string.status_label),
                        style = TextStyle(
                            color = labelColor,
                            fontSize = 14.sp,
                            fontFamily = PickRegularFont,
                        ),
                    )
                    BasicText(
                        text = item.status,
                        modifier = Modifier.padding(start = 4.dp),
                        style = TextStyle(
                            color = colorResource(R.color.textGreen),
                            fontSize = 14.sp,
                            fontFamily = PickRegularFont,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun QuantityChip(
    label: String,
    value: String,
    background: Color,
    modifier: Modifier = Modifier,
) {
    val color = colorResource(R.color.textColorDarker)
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(background)
            .padding(4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicText(
            text = label,
            style = TextStyle(color = color, fontSize = 14.sp, fontFamily = PickRegularFont),
        )
        BasicText(
            text = value,
            modifier = Modifier.padding(start = 2.dp),
            style = TextStyle(color = color, fontSize = 14.sp, fontFamily = PickRegularFont),
        )
    }
}

/** ImageView hosted with AndroidView so the coil request stays exactly as it was. */
@Composable
internal fun PickItemImage(url: String, modifier: Modifier = Modifier) {
    if (LocalInspectionMode.current) {
        // coil can't load in Studio previews, show the placeholder instead
        Image(
            painter = painterResource(R.drawable.image_placeholder),
            contentDescription = null,
            modifier = modifier,
            contentScale = ContentScale.Crop,
        )
        return
    }
    AndroidView(
        modifier = modifier,
        factory = { context ->
            ImageView(context).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
                importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
            }
        },
        update = { imageView ->
            imageView.load(url) {
                scale(Scale.FIT)
                placeholder(R.drawable.image_placeholder)
                error(R.drawable.image_placeholder)
            }
        },
    )
}


private val previewPickItems = listOf(
    PickItem(
        description = "Ergonomic wireless mouse with a very long description",
        id = 1, name = "Wireless Mouse", picked_quantity = 2, quantity = 10,
        image = "", status = "picking", barcode = "1001",
    ),
    PickItem(
        description = null,
        id = 2, name = "USB-C Cable", picked_quantity = 5, quantity = 5,
        image = "", status = "picked", barcode = null,
    ),
    PickItem(
        description = "Mechanical keyboard",
        id = 3, name = "Keyboard", picked_quantity = 0, quantity = 3,
        image = "", status = "ready_picking", barcode = "1003",
    ),
)

@Preview(showBackground = true, showSystemUi = true, name = "Pick items - list")
@Composable
private fun PickItemsScreenPreview() {
    PickItemsScreen(
        title = "SO-1024",
        items = previewPickItems,
        showStart = false,
        onBackClick = {},
        onScanClick = {},
        onStartClick = {},
        onItemClick = {},
    )
}

@Preview(showBackground = true, showSystemUi = true, name = "Pick items - start")
@Composable
private fun PickItemsScreenStartPreview() {
    PickItemsScreen(
        title = "Picking",
        items = emptyList(),
        showStart = true,
        onBackClick = {},
        onScanClick = {},
        onStartClick = {},
        onItemClick = {},
    )
}

@Preview(showBackground = true, name = "Pick item row")
@Composable
private fun PickItemRowPreview() {
    PickItemRow(item = previewPickItems.first(), onClick = {})
}

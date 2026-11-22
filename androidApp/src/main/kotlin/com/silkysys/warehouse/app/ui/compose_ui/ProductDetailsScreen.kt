package com.silkysys.warehouse.app.ui.compose_ui.product_details

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.silkysys.warehouse.app.ui.compose_ui.pick.PickItemImage
import com.silkysys.warehouse.sharedcode.domain.putAwayModule.putAway.entity.PutAwayScanEntity

private val RegularFont = FontFamily(Font(R.font.regular))

private val TextColorPrimary = Color(0xDE000000)

private val CardShape = RoundedCornerShape(20.dp) // @drawable/item_card_bg

@Composable
fun ProductDetailsScreen(
    product: PutAwayScanEntity.Item,
    racks: List<PutAwayScanEntity.Item.BinLocations>,
    onBackClick: () -> Unit,
    onAddLocationClick: () -> Unit,
    onDeleteRackClick: (PutAwayScanEntity.Item.BinLocations) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.colorBackgroundLight))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(4.dp), // root layout_margin
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            // rel_header
            Box(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                        .clickable(role = Role.Button, onClick = onBackClick)
                        .padding(20.dp),
                ) {
                    Image(
                        painter = painterResource(R.drawable.back_icon),
                        contentDescription = stringResource(R.string.back),
                    )
                }
                BasicText(
                    text = stringResource(R.string.product_details),
                    modifier = Modifier.align(Alignment.Center),
                    style = TextStyle(
                        color = TextColorPrimary,
                        fontSize = 20.sp,
                    ),
                )
            }

            ProductCard(product)

            LocationsCard(
                racks = racks,
                onDeleteRackClick = onDeleteRackClick,
                modifier = Modifier.padding(top = 24.dp),
            )
        }

        // bt_addLocation (button_style: colorPrimary background, white 14sp text)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 48.dp)
                .background(colorResource(R.color.colorPrimary))
                .clickable(role = Role.Button, onClick = onAddLocationClick),
            contentAlignment = Alignment.Center,
        ) {
            BasicText(
                text = stringResource(R.string.add_location),
                style = TextStyle(
                    color = colorResource(R.color.colorWhite),
                    fontSize = 14.sp,
                    fontFamily = RegularFont,
                ),
            )
        }
    }
}

/** lin_item: image + name / category / SKU. */
@Composable
private fun ProductCard(product: PutAwayScanEntity.Item) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, CardShape)
            .padding(vertical = 16.dp),
    ) {
        PickItemImage(
            url = product.item_img,
            modifier = Modifier
                .padding(start = 8.dp)
                .size(width = 120.dp, height = 150.dp),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
        ) {
            BasicText(
                text = product.item_name,
                style = TextStyle(color = TextColorPrimary, fontSize = 20.sp),
            )
            BasicText(
                text = product.category?.name.orEmpty(),
                modifier = Modifier.padding(top = 16.dp),
                style = TextStyle(color = TextColorPrimary, fontSize = 16.sp),
            )
            BasicText(
                text = "SKU: " + product.sku,
                modifier = Modifier.padding(top = 4.dp),
                style = TextStyle(color = TextColorPrimary, fontSize = 16.sp),
            )
        }
    }
}

/** The "Pin Locations" card: title, then either "No Location Founded" or the rack list. */
@Composable
private fun LocationsCard(
    racks: List<PutAwayScanEntity.Item.BinLocations>,
    onDeleteRackClick: (PutAwayScanEntity.Item.BinLocations) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, CardShape),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_pin_location),
                contentDescription = null,
            )
            Spacer(Modifier.width(8.dp))
            BasicText(
                text = stringResource(R.string.location),
                style = TextStyle(color = TextColorPrimary, fontSize = 20.sp),
            )
        }

        if (racks.isEmpty()) {
            // tv_noLocationFounded
            BasicText(
                text = stringResource(R.string.no_location_founded),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                style = TextStyle(
                    color = TextColorPrimary,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                ),
            )
        } else {
            // lin_locations + listView (200dp high, scrolls inside)
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 34.dp)
                    .padding(16.dp)
                    .height(200.dp),
            ) {
                items(racks) { rack ->
                    RackRow(rack = rack, onDeleteClick = { onDeleteRackClick(rack) })
                }
            }
        }
    }
}

@Composable
private fun RackRow(
    rack: PutAwayScanEntity.Item.BinLocations,
    onDeleteClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
    ) {
        BasicText(
            text = rack.code,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            style = TextStyle(color = TextColorPrimary, fontSize = 18.sp),
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp)
                .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                .clickable(role = Role.Button, onClick = onDeleteClick),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_delete),
                contentDescription = stringResource(R.string.delete_item),
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .height(1.dp)
                .background(colorResource(R.color.colorBackgroundDark)),
        )
    }
}


private val previewProduct = PutAwayScanEntity.Item(
    item_img = "",
    barcode = "123456789",
    company_id = 1,
    created_at = "",
    default_warehouse = 1,
    id = 1,
    item_id = 1,
    item_name = "Wireless Mouse",
    item_quantity = "24",
    opening_stock = 0,
    opening_stock_value = 0,
    reorder_level = 5,
    returnable = 1,
    sku = "WM-001",
    category = PutAwayScanEntity.Item.Category(name = "Electronics"),
    bin_locations = null,
)

private val previewRacks = listOf(
    PutAwayScanEntity.Item.BinLocations(1, 1, 1, "Rack: 1, Row: 2, Position: 1"),
    PutAwayScanEntity.Item.BinLocations(2, 1, 1, "Rack: 3, Row: 1, Position: 4"),
    PutAwayScanEntity.Item.BinLocations(3, 1, 1, "Rack: 5, Row: 2, Position: 2"),
)

@Preview(showBackground = true, showSystemUi = true, name = "Product details - with locations")
@Composable
private fun ProductDetailsScreenPreview() {
    ProductDetailsScreen(
        product = previewProduct,
        racks = previewRacks,
        onBackClick = {},
        onAddLocationClick = {},
        onDeleteRackClick = {},
    )
}

@Preview(showBackground = true, showSystemUi = true, name = "Product details - no locations")
@Composable
private fun ProductDetailsScreenEmptyPreview() {
    ProductDetailsScreen(
        product = previewProduct,
        racks = emptyList(),
        onBackClick = {},
        onAddLocationClick = {},
        onDeleteRackClick = {},
    )
}
package com.silkysys.warehouse.app.ui.compose_ui.packdetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.silkysys.warehouse.sharedcode.domain.packing.packDetails.entity.PackDetailsEntity

private val BoldMediumFont = FontFamily(Font(R.font.bold_medium))
private val RegularFont = FontFamily(Font(R.font.regular))

/**
 * Compose replacement for `activity_pack_details.xml`:
 * gradient app bar (back + the pack name), the list of [PackDetailsItem]s, the "You didn't
 * packed any item." state, and either the round "add" button (while the pack can still be
 * packed) or the "Finish" button.
 */
@Composable
fun PackDetailsScreen(
    packName: String?,
    packListItems: List<PackDetailsEntity.PackList>,
    isEmptyVisible: Boolean,
    canPack: Boolean,
    onBack: () -> Unit,
    onAddPack: () -> Unit,
    onFinish: () -> Unit,
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
            // pd_header (fitsSystemWindows -> statusBarsPadding)
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
                    // tv_detailsItem: the pack name
                    BasicText(
                        text = packName.orEmpty(),
                        modifier = Modifier.align(Alignment.Center),
                        style = TextStyle(
                            color = colorResource(R.color.colorLight),
                            fontSize = 24.sp,
                            fontFamily = BoldMediumFont,
                        ),
                    )
                }
            }

            // rv_packDetailsRecycler (4dp side margins); hidden while the empty state shows
            if (!isEmptyVisible) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    // keeps the last card clear of the add / finish buttons that float over the list
                    contentPadding = PaddingValues(bottom = 96.dp),
                ) {
                    items(packListItems) { item -> PackDetailsItem(item = item) }
                }
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
                    text = stringResource(R.string.you_didn_t_pack_ant_item),
                    modifier = Modifier.fillMaxWidth(),
                    style = TextStyle(
                        color = Color(0xDE000000),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                    ),
                )
            }
        }

        if (canPack) {
            // civ_addPack: 48dp round button (colorAccent), 25dp margin, bottom end
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(25.dp)
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(colorResource(R.color.colorAccent))
                    .clickable(role = Role.Button, onClick = onAddPack),
            ) {
                Image(
                    painter = painterResource(R.drawable.add_ic), // white "+" (vector tint)
                    contentDescription = stringResource(R.string.add),
                    modifier = Modifier.fillMaxSize(),
                )
            }
        } else {
            // bt_finish (button_style: min height 48dp, 16dp margins, round_edges)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp)
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(colorResource(R.color.colorPrimary))
                    .clickable(role = Role.Button, onClick = onFinish),
                contentAlignment = Alignment.Center,
            ) {
                BasicText(
                    text = stringResource(R.string.finish),
                    style = TextStyle(
                        color = colorResource(R.color.colorWhite),
                        fontSize = 14.sp,
                        fontFamily = RegularFont,
                    ),
                )
            }
        }
    }
}

private fun previewPackList(id: Int, name: String) = PackDetailsEntity.PackList(
    id = id,
    height = 30,
    width = 20,
    length = 40,
    weight = 5,
    isFragile = id % 2,
    quantity = 3,
    name = name,
    uom = "box",
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PackDetailsScreenPreview() {
    PackDetailsScreen(
        packName = "pack-1",
        packListItems = listOf(previewPackList(1, "pack-1"), previewPackList(2, "pack-2")),
        isEmptyVisible = false,
        canPack = true,
        onBack = {},
        onAddPack = {},
        onFinish = {},
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PackDetailsScreenEmptyPreview() {
    PackDetailsScreen(
        packName = "pack-1",
        packListItems = emptyList(),
        isEmptyVisible = true,
        canPack = false,
        onBack = {},
        onAddPack = {},
        onFinish = {},
    )
}

package com.silkysys.warehouse.app.ui.compose_ui.pack_details

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
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

private val TextColorPrimary = Color(0xDE000000)


@Composable
fun PackDetailsItem(
    item: PackDetailsEntity.PackList,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(5.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp) // CardView layout_marginTop
            // @drawable/items_border: white, 5dp corners, 1dp #F0F0F0 stroke
            .background(Color.White, shape)
            .border(1.dp, Color(0xFFF0F0F0), shape)
            .padding(8.dp),
    ) {
        Column(modifier = Modifier.padding(start = 24.dp)) {
            BasicText(
                text = item.name,
                style = TextStyle(
                    color = colorResource(R.color.colorBlack),
                    fontSize = 16.sp,
                    fontFamily = BoldMediumFont,
                ),
            )
            LabelValueRow(stringResource(R.string.height), item.height.toString())
            LabelValueRow(stringResource(R.string.length), item.length.toString())
            BasicText(
                text = stringResource(R.string.uom),
                modifier = Modifier.padding(top = 4.dp),
                style = TextStyle(
                    color = colorResource(R.color.colorBlack),
                    fontSize = 16.sp,
                    fontFamily = BoldMediumFont,
                ),
            )
            Chip(text = item.uom, modifier = Modifier.padding(top = 4.dp))
        }

        Column(modifier = Modifier.padding(start = 50.dp, end = 24.dp)) {
            Row {
                BasicText(
                    text = stringResource(R.string.quantity),
                    style = TextStyle(
                        color = colorResource(R.color.colorAccent),
                        fontSize = 16.sp,
                        fontFamily = BoldMediumFont,
                    ),
                )
                BasicText(
                    text = item.quantity.toString(),
                    modifier = Modifier.padding(start = 8.dp),
                    style = TextStyle(
                        color = colorResource(R.color.colorBlack),
                        fontSize = 16.sp,
                        fontFamily = BoldMediumFont,
                    ),
                )
            }
            LabelValueRow(stringResource(R.string.width), item.width.toString())
            LabelValueRow(stringResource(R.string.weight), item.weight.toString())
            BasicText(
                text = stringResource(R.string.is_fragile),
                modifier = Modifier.padding(top = 4.dp),
                style = TextStyle(
                    color = colorResource(R.color.colorBlack),
                    fontSize = 16.sp,
                    fontFamily = BoldMediumFont,
                ),
            )
            Chip(
                text = stringResource(if (item.isFragile == 1) R.string.yes else R.string.no),
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

/** A blue label (sub_title_style, colorPrimary) followed by its value, 4dp below the previous row. */
@Composable
private fun LabelValueRow(label: String, value: String) {
    Row(modifier = Modifier.padding(top = 4.dp)) {
        BasicText(
            text = label,
            style = TextStyle(
                color = colorResource(R.color.colorPrimary),
                fontSize = 14.sp,
                fontFamily = RegularFont,
            ),
        )
        BasicText(
            text = value,
            modifier = Modifier.padding(start = 16.dp),
            style = TextStyle(
                color = colorResource(R.color.colorBlack),
                fontSize = 14.sp,
                fontFamily = RegularFont,
            ),
        )
    }
}

/** `@drawable/uom_bg`: pill, colorPrimary at 10% alpha, at least 53x24dp, text centred. */
@Composable
private fun Chip(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 53.dp, minHeight = 24.dp)
            .background(colorResource(R.color.colorPrimary).copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            text = text,
            style = TextStyle(
                color = TextColorPrimary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
            ),
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun PackDetailsItemPreview() {
    PackDetailsItem(
        item = PackDetailsEntity.PackList(
            id = 1,
            height = 30,
            width = 20,
            length = 40,
            weight = 5,
            isFragile = 1,
            quantity = 3,
            name = "pack-1",
            uom = "box",
        ),
    )
}

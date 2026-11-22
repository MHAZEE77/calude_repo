package com.silkysys.warehouse.app.ui.compose_ui.pick

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.silkysys.warehouse.R

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview

/*
 * Small pieces shared by the pick screens (not tied to a single layout file).
 */

internal val PickRegularFont = FontFamily(Font(R.font.regular))
internal val PickBoldMediumFont = FontFamily(Font(R.font.bold_medium))

/** `?android:textColorPrimary` of the old light theme (what TextAppearance.AppCompat.Subhead used). */
internal val PickTextColorPrimary = Color(0xDE000000)

/** `@drawable/items_border`: white fill, 5dp corners, 1dp #F0F0F0 stroke. */
internal fun Modifier.itemsBorder(): Modifier {
    val shape = RoundedCornerShape(5.dp)
    return this
        .background(Color.White, shape)
        .border(1.dp, Color(0xFFF0F0F0), shape)
}

/**
 * Replacement for the `CheckBox` (30dp high, tinted colorPrimary when checked).
 * Foundation-only so it keeps the exact size the XML had.
 */
@Composable
internal fun PickCheckBox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val checkedColor = colorResource(R.color.colorPrimary)
    val uncheckedColor = Color(0x99000000)
    Box(
        modifier = modifier
            .size(width = 32.dp, height = 30.dp)
            .toggleable(value = checked, role = Role.Checkbox, onValueChange = onCheckedChange),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(18.dp)) {
            val strokeWidth = 2.dp.toPx()
            val radius = CornerRadius(2.dp.toPx())
            if (checked) {
                drawRoundRect(color = checkedColor, cornerRadius = radius)
                val tick = Path().apply {
                    moveTo(size.width * 0.22f, size.height * 0.52f)
                    lineTo(size.width * 0.42f, size.height * 0.72f)
                    lineTo(size.width * 0.78f, size.height * 0.30f)
                }
                drawPath(
                    path = tick,
                    color = Color.White,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round),
                )
            } else {
                drawRoundRect(
                    color = uncheckedColor,
                    topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                    size = Size(size.width - strokeWidth, size.height - strokeWidth),
                    cornerRadius = radius,
                    style = Stroke(width = strokeWidth),
                )
            }
        }
    }
}

/**
 * The `emptyContainer` of both fragment layouts. In the XML its image and two texts are
 * `visibility="gone"`, so only the Refresh button was ever shown - kept as is.
 */
@Composable
internal fun PickEmptyContainer(onRefresh: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.colorBackgroundLight)),
        contentAlignment = Alignment.Center,
    ) {
        // refreshButton: 56dp layout height, MaterialButton keeps 6dp top/bottom insets
        Box(
            modifier = Modifier
                .defaultMinSize(minWidth = 64.dp)
                .height(56.dp)
                .padding(vertical = 6.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(colorResource(R.color.colorPrimary))
                .clickable(role = Role.Button, onClick = onRefresh)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            BasicText(
                text = stringResource(R.string.refresh).uppercase(),
                style = TextStyle(
                    color = colorResource(R.color.colorWhite),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                ),
            )
        }
    }
}


@Preview(showBackground = true, name = "CheckBox")
@Composable
private fun PickCheckBoxPreview() {
    var checked by remember { mutableStateOf(true) }
    Row(modifier = Modifier.padding(16.dp)) {
        PickCheckBox(checked = false, onCheckedChange = {})
        Spacer(Modifier.width(16.dp))
        PickCheckBox(checked = true, onCheckedChange = {})
        Spacer(Modifier.width(16.dp))
        PickCheckBox(checked = checked, onCheckedChange = { checked = it }) // interactive
    }
}

@Preview(showBackground = true, name = "Items border")
@Composable
private fun ItemsBorderPreview() {
    Column(modifier = Modifier.padding(16.dp)) {
        BasicText(
            text = "Bordered card",
            modifier = Modifier.itemsBorder().padding(16.dp),
            style = TextStyle(color = PickTextColorPrimary, fontSize = 16.sp),
        )
    }
}

@Preview(showBackground = true, name = "Empty container")
@Composable
private fun PickEmptyContainerPreview() {
    PickEmptyContainer(onRefresh = {})
}
package com.silkysys.warehouse.app.ui.compose_ui.addpack

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.silkysys.warehouse.R

/*
 * Small pieces shared by the add-pack layouts.
 */

internal val AddPackBoldMediumFont = FontFamily(Font(R.font.bold_medium))
internal val AddPackRegularFont = FontFamily(Font(R.font.regular))

/** `?android:textColorPrimary` of the old light theme (plain TextViews used it). */
internal val AddPackTextColorPrimary = Color(0xDE000000)

/** `@drawable/items_border`: white, 5dp corners, 1dp #F0F0F0 stroke. */
internal fun Modifier.itemsBorder(): Modifier {
    val shape = RoundedCornerShape(5.dp)
    return this
        .background(Color.White, shape)
        .border(1.dp, Color(0xFFF0F0F0), shape)
}

/**
 * `@drawable/darker_rectangle` (edit_text_add_pack): #F5F5F5 box, 5dp corners, with a 2dp
 * [accent] line along the bottom. [accent] turns red for an invalid value.
 */
internal fun Modifier.darkerRectangle(accent: Color): Modifier =
    this
        .clip(RoundedCornerShape(5.dp))
        .background(Color(0xFFF5F5F5))
        .drawBehind {
            val line = 2.dp.toPx()
            drawRect(
                color = accent,
                topLeft = Offset(0f, size.height - line),
                size = Size(size.width, line),
            )
        }

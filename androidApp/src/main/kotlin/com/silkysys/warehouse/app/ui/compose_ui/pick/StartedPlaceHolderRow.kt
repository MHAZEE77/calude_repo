package com.silkysys.warehouse.app.ui.compose_ui.pick

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.silkysys.warehouse.R

@Composable
private fun rememberShimmerBrush(): Brush {
    val density = LocalDensity.current.density
    val transition = rememberInfiniteTransition(label = "pickShimmer")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 1200, easing = LinearEasing)),
        label = "pickShimmerProgress",
    )
    val highlight = colorResource(R.color.placeholder_color)
    val base = highlight.copy(alpha = 0.3f)
    val band = 300f * density
    val startX = (progress * 1000f - 300f) * density
    return Brush.linearGradient(
        colors = listOf(base, highlight, base),
        start = Offset(startX, 0f),
        end = Offset(startX + band, 0f),
    )
}

@Composable
fun StartedPlaceHolderRow(brush: Brush, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // idView
        Box(modifier = Modifier.size(width = 50.dp, height = 28.dp).background(brush))
        // orderView
        Box(modifier = Modifier.padding(start = 16.dp).weight(1f).height(28.dp).background(brush))
        // productView
        Box(modifier = Modifier.padding(start = 16.dp).width(50.dp).height(28.dp).background(brush))
        // totalView
        Box(modifier = Modifier.padding(start = 16.dp).width(60.dp).height(28.dp).background(brush))
    }
}

/** The `shimmerLayout` overlay: several placeholder rows stacked from the top. */
@Composable
fun StartedPlaceHolderList(rows: Int, modifier: Modifier = Modifier) {
    val brush = rememberShimmerBrush()
    Column(modifier = modifier.fillMaxSize().clipToBounds()) {
        repeat(rows) { StartedPlaceHolderRow(brush) }
    }
}


@Preview(showBackground = true, name = "Single row")
@Composable
private fun StartedPlaceHolderRowPreview() {
    StartedPlaceHolderRow(brush = rememberShimmerBrush())
}

@Preview(showBackground = true, showSystemUi = true, name = "Placeholder list")
@Composable
private fun StartedPlaceHolderListPreview() {
    StartedPlaceHolderList(rows = 8)
}
package com.silkysys.warehouse.app.ui.compose_ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.silkysys.warehouse.R

private val LogoWidth = 73.dp
private val LogoHeight = 132.dp
private val TextWidth = 180.dp
private val TextHeight = 26.756756.dp
private val LogoTextSpacing = 30.dp

/**
 * Compose replacement for the old `activity_launcher.xml`.
 * The logo is centred on screen and the wordmark hangs below it, exactly like the
 * previous RelativeLayout (so the logo does not shift when the text is added).
 */
@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    // Old shape: android:angle="270" (top -> bottom), startColor = gradient_end, endColor = gradient_start
    val gradient = Brush.verticalGradient(
        colors = listOf(
            colorResource(R.color.gradient_end),
            colorResource(R.color.gradient_start),
        ),
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(gradient),
    ) {
        Image(
            painter = painterResource(R.drawable.silky_logo),
            contentDescription = null, // decorative, matches importantForAccessibility="no"
            modifier = Modifier
                .align(Alignment.Center)
                .size(LogoWidth, LogoHeight),
        )
        Image(
            painter = painterResource(R.drawable.silky_text),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = LogoHeight / 2 + LogoTextSpacing + TextHeight / 2)
                .size(TextWidth, TextHeight),
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SplashScreenPreview() {
    SplashScreen()
}

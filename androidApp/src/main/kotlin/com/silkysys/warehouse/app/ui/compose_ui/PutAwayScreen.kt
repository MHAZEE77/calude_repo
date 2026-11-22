package com.silkysys.warehouse.app.ui.compose_ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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

private val BoldMediumFont = FontFamily(Font(R.font.bold_medium))
private val RegularFont = FontFamily(Font(R.font.regular))

@Composable
fun PutAwayScreen(
    onBack: () -> Unit,
    onStartScan: () -> Unit,
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
        // AppBarLayout (fitsSystemWindows -> statusBarsPadding)
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
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
                    text = stringResource(R.string.put_away_menu),
                    modifier = Modifier.align(Alignment.Center),
                    style = TextStyle(
                        color = colorResource(R.color.colorLight),
                        fontSize = 24.sp,
                        fontFamily = BoldMediumFont,
                    ),
                )
            }
        }

        // scanLinearLayout (constrained to all four sides of the parent -> centred on the screen)
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(16.dp) // layout_margin
                .padding(16.dp), // padding
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_qr),
                contentDescription = null, // decorative, matches importantForAccessibility="no"
                modifier = Modifier.size(100.dp),
            )

            // title_style
            BasicText(
                text = stringResource(R.string.put_away_menu),
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                style = TextStyle(
                    color = colorResource(R.color.colorBlack),
                    fontSize = 16.sp,
                    fontFamily = BoldMediumFont,
                    textAlign = TextAlign.Center,
                ),
            )

            // sub_title_style
            BasicText(
                text = stringResource(R.string.scan_product_and_view_details),
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                style = TextStyle(
                    color = colorResource(R.color.colorBlack),
                    fontSize = 14.sp,
                    fontFamily = RegularFont,
                    textAlign = TextAlign.Center,
                ),
            )

            // bt_startScan (button_style: 16dp side margins; background round_edges)
            Box(
                modifier = Modifier
                    .padding(start = 16.dp, top = 24.dp, end = 16.dp)
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(colorResource(R.color.colorPrimary))
                    .clickable(role = Role.Button, onClick = onStartScan),
                contentAlignment = Alignment.Center,
            ) {
                BasicText(
                    text = stringResource(R.string.start_scan),
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PutAwayScreenPreview() {
    PutAwayScreen(onBack = {}, onStartScan = {})
}

package com.silkysys.warehouse.app.ui.compose_ui.pick

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.compose.AndroidFragment
import com.silkysys.warehouse.R
import com.silkysys.warehouse.app.picking.pick.fragment.PickStartedFragment
import com.silkysys.warehouse.app.picking.pick.fragment.PickWaitingFragment
import kotlinx.coroutines.launch

private val BoldMediumFont = FontFamily(Font(R.font.bold_medium))
private val TabUnselectedColor = Color(0xFF8EA0B9) // adjust to your tab_color_selector_bg / tab_layout_style

@Composable
fun PickScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val gradient = Brush.verticalGradient(
        colors = listOf(
            colorResource(R.color.gradient_end),
            colorResource(R.color.gradient_start),
        ),
    )
    val titles = listOf(
        stringResource(R.string.pick_started),
        stringResource(R.string.pick_waiting),
    )
    val pagerState = rememberPagerState(pageCount = { titles.size })
    val scope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxSize()) {

        // AppBarLayout + Toolbar (fitsSystemWindows -> statusBarsPadding)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradient)
                .statusBarsPadding()
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
                text = stringResource(R.string.picking),
                modifier = Modifier.align(Alignment.Center),
                style = TextStyle(
                    color = colorResource(R.color.colorLight),
                    fontSize = 24.sp,
                    fontFamily = BoldMediumFont,
                ),
            )
        }

        // TabLayout (fixed mode)
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = colorResource(R.color.colorBackgroundLight),
            indicator = { positions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(positions[pagerState.currentPage]),
                    height = 2.dp,
                    color = colorResource(R.color.colorPrimary),
                )
            },
            divider = {},
        ) {
            titles.forEachIndexed { index, title ->
                val selected = pagerState.currentPage == index
                Tab(
                    selected = selected,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    text = {
                        BasicText(
                            text = title,
                            style = TextStyle(
                                color = if (selected) colorResource(R.color.colorPrimary)
                                else TabUnselectedColor,
                                fontSize = 14.sp,
                                fontFamily = BoldMediumFont,
                            ),
                        )
                    },
                )
            }
        }

        // ViewPager (paddingStart/End = 10dp)
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
        ) { page ->
            if (LocalInspectionMode.current) {
                // Fragments can't render in Studio previews, so show a stand-in
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    BasicText("Fragment page $page (not rendered in preview)")
                }
            } else {
                when (page) {
                    0 -> AndroidFragment<PickStartedFragment>(modifier = Modifier.fillMaxSize())
                    else -> AndroidFragment<PickWaitingFragment>(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Pick screen")
@Composable
private fun PickScreenPreview() {
    PickScreen(onBackClick = {})
}
package com.silkysys.warehouse.app.ui.compose_ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
import com.silkysys.warehouse.app.autentcation.main.adapter.GridViewModal
import kotlinx.coroutines.launch

/** The entries of the old `@menu/menu_item` NavigationView menu. */
enum class MainDrawerItem { Home, Profile, ChangeLanguage, Logout }

private val BoldMediumFont = FontFamily(Font(R.font.bold_medium))

// Normal state of the old @color/nav_item_colors selector
private val NavItemColor = Color(0xFF8EA0B9)


@Composable
fun MainScreen(
    services: List<GridViewModal>,
    buildTypeLabel: String?,
    onServiceClick: (Int) -> Unit,
    onDrawerItemClick: (MainDrawerItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val gradient = Brush.verticalGradient(
        colors = listOf(
            colorResource(R.color.gradient_end),
            colorResource(R.color.gradient_start),
        ),
    )
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // NEW: close the drawer on system back
    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    Column(modifier = modifier.fillMaxSize()) {

        // NEW: Box wraps the app bar so the click-catcher can sit on top of it
        Box {
            // app_bar_01 (unchanged)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(gradient)
                    .statusBarsPadding(),
            ) {
                // toolbar (unchanged)
                Box(modifier = Modifier.fillMaxWidth().height(56.dp)) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .size(width = 56.dp, height = 56.dp)
                            .clickable(role = Role.Button) { scope.launch { drawerState.open() } },
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_menu),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                        )
                    }

                    Image(
                        painter = painterResource(R.drawable.silky_logo),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(width = 73.dp, height = 40.dp),
                    )

                    if (buildTypeLabel != null) {
                        BasicText(
                            text = buildTypeLabel,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 30.dp),
                            style = TextStyle(
                                color = colorResource(R.color.red),
                                fontSize = 14.sp,
                                fontFamily = BoldMediumFont,
                            ),
                        )
                    }
                }

                BasicText(
                    text = stringResource(R.string.chose_service),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp)
                        .padding(10.dp),
                    style = TextStyle(
                        color = colorResource(R.color.colorLight),
                        fontSize = 20.sp,
                        fontFamily = BoldMediumFont,
                    ),
                )
            }

            // NEW: must be the LAST child of the Box so it draws above the app bar
            if (drawerState.isOpen) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { scope.launch { drawerState.close() } },
                )
            }
        }

        ModalNavigationDrawer(
            modifier = Modifier.weight(1f),
            drawerState = drawerState,
            scrimColor = Color(0x99000000),
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.fillMaxWidth(0.7f),
                    drawerContainerColor = colorResource(R.color.colorWhite),
                    windowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
                ) {
                    Spacer(Modifier.height(20.dp)) // NEW: space above "Home"

                    DrawerEntry(
                        label = stringResource(R.string.home),
                        icon = R.drawable.home_icon,
                        selected = true,
                    ) { scope.launch { drawerState.close() }; onDrawerItemClick(MainDrawerItem.Home) }
                    DrawerEntry(
                        label = stringResource(R.string.nav_profile),
                        icon = R.drawable.profile_icon,
                    ) { scope.launch { drawerState.close() }; onDrawerItemClick(MainDrawerItem.Profile) }
                    DrawerEntry(
                        label = stringResource(R.string.nav_change_language),
                        icon = R.drawable.change_lang_icon,
                    ) { scope.launch { drawerState.close() }; onDrawerItemClick(MainDrawerItem.ChangeLanguage) }
                    DrawerEntry(
                        label = stringResource(R.string.log_out),
                        icon = R.drawable.ic_logout_menu,
                    ) { scope.launch { drawerState.close() }; onDrawerItemClick(MainDrawerItem.Logout) }
                }
            },
        ) {
            // grid (unchanged)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                itemsIndexed(services) { index, service ->
                    ServiceCard(service = service, onClick = { onServiceClick(index) })
                }
            }
        }
    }
}

/** One NavigationView menu row (icons keep their own colours, like `itemIconTintList = null`). */
@Composable
private fun DrawerEntry(
    label: String,
    icon: Int,
    selected: Boolean = false,
    onClick: () -> Unit,
) {
    NavigationDrawerItem(
        label = {
            BasicText(
                text = label,
                style = TextStyle(
                    color = if (selected) colorResource(R.color.colorPrimary) else NavItemColor,
                    fontSize = 14.sp,
                ),
            )
        },
        selected = selected,
        onClick = onClick,
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
        icon = {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
        },
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = colorResource(R.color.colorPrimary).copy(alpha = 0.12f),
            unselectedContainerColor = Color.Transparent,
        ),
    )
}

/**
 * Replaces `grid_item.xml`: CardView (8dp corners, 10dp elevation, 8dp margin,
 * cardUseCompatPadding) with a 70dp icon and a gradient_start title bar.
 */
@Composable
private fun ServiceCard(service: GridViewModal, onClick: () -> Unit) {
    val shape = RoundedCornerShape(8.dp)
    Box(
        modifier = Modifier
            .padding(8.dp) // layout_margin
            .clickable(role = Role.Button, onClick = onClick)
            // compat padding that CardView reserves for its shadow (~elevation, 1.5x vertically)
            .padding(horizontal = 12.dp, vertical = 17.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 10.dp, shape = shape)
                .clip(shape)
                .background(Color.White),
        ) {
            Image(
                painter = painterResource(service.serviceImg),
                contentDescription = null, // decorative, matches importantForAccessibility="no"
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(5.dp)
                    .size(70.dp)
                    .padding(4.dp),
            )
            BasicText(
                text = service.serviceName,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(R.color.gradient_start))
                    .padding(4.dp),
                style = TextStyle(
                    color = colorResource(R.color.colorLight),
                    fontSize = 18.sp,
                    fontFamily = BoldMediumFont,
                    textAlign = TextAlign.Center,
                ),
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MainScreenPreview() {
    MainScreen(
        services = listOf(
            GridViewModal("Pick", R.drawable.pick),
            GridViewModal("Pack", R.drawable.pack),
            GridViewModal("Put Away", R.drawable.inventory),
            GridViewModal("Add Stock", R.drawable.add_stock),
        ),
        buildTypeLabel = "DEMO",
        onServiceClick = {},
        onDrawerItemClick = {},
    )
}

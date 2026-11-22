package com.silkysys.warehouse.app.ui.compose_ui

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toArgb
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
import androidx.compose.ui.viewinterop.AndroidView
import coil.load
import coil.size.Scale
import com.silkysys.warehouse.R
import de.hdodenhof.circleimageview.CircleImageView

private val BoldMediumFont = FontFamily(Font(R.font.bold_medium))
private val RegularFont = FontFamily(Font(R.font.regular))


@Composable
fun ProfileScreen(
    name: String?,
    email: String?,
    imageUrl: String?,
    onBack: () -> Unit,
    onChangePassword: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val gradient = Brush.verticalGradient(
        colors = listOf(
            colorResource(R.color.gradient_end),
            colorResource(R.color.gradient_start),
        ),
    )
    var personalExpanded by remember { mutableStateOf(true) }
    var warehouseExpanded by remember { mutableStateOf(true) }
    val notActive = stringResource(R.string.not_active)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // app_bar_01 (fitsSystemWindows -> statusBarsPadding)
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
                BasicText(
                    text = stringResource(R.string.Profile),
                    modifier = Modifier.align(Alignment.Center),
                    style = TextStyle(
                        color = colorResource(R.color.colorLight),
                        fontSize = 24.sp,
                        fontFamily = BoldMediumFont,
                    ),
                )
            }
        }

        // userImage: 150dp round photo, 2dp colorPrimary border
        UserImage(imageUrl = imageUrl, modifier = Modifier.padding(top = 20.dp))

        // Personal Info card
        InfoCard {
            SectionHeader(
                title = stringResource(R.string.personal_info),
                icon = R.drawable.profile_icon,
                iconTint = colorResource(R.color.colorPrimary), // drawableTint
                expanded = personalExpanded,
                onToggle = { personalExpanded = !personalExpanded },
            )
            CollapsibleContent(visible = personalExpanded) {
                Row {
                    Column(modifier = Modifier.padding(start = 5.dp)) {
                        InfoLabel(stringResource(R.string.name))
                        InfoLabel(stringResource(R.string.email))
                    }
                    Column(modifier = Modifier.padding(start = 50.dp)) {
                        InfoValue(name ?: notActive)
                        InfoValue(email ?: notActive)
                    }
                }
            }
        }

        // Warehouse Info card
        InfoCard {
            SectionHeader(
                title = stringResource(R.string.warehouse_info),
                icon = R.drawable.warehouse_icon,
                iconTint = null,
                expanded = warehouseExpanded,
                onToggle = { warehouseExpanded = !warehouseExpanded },
            )
            CollapsibleContent(visible = warehouseExpanded) {
                Row(modifier = Modifier.padding(8.dp)) {
                    Column {
                        InfoLabel(stringResource(R.string.branch_name))
                    }
                    Column(modifier = Modifier.padding(start = 50.dp)) {
                        // tv_brachName: the activity never fills it, so it stays "N/A"
                        InfoValue(notActive)
                    }
                }
            }
        }

        // tv_changePassword (button_style: 16dp side/top margins, min height 48dp, round_edges)
        Box(
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(colorResource(R.color.colorPrimary))
                .clickable(role = Role.Button, onClick = onChangePassword),
            contentAlignment = Alignment.Center,
        ) {
            BasicText(
                text = stringResource(R.string.change_password),
                style = TextStyle(
                    color = colorResource(R.color.colorWhite),
                    fontSize = 14.sp,
                    fontFamily = RegularFont,
                ),
            )
        }
    }
}

/** CircleImageView hosted with AndroidView so the coil request stays exactly as before. */
@Composable
private fun UserImage(imageUrl: String?, modifier: Modifier = Modifier) {
    val borderColor = colorResource(R.color.colorPrimary).toArgb()
    AndroidView(
        modifier = modifier.size(150.dp),
        factory = { context ->
            CircleImageView(context).apply {
                borderWidth = (2 * context.resources.displayMetrics.density).toInt()
                this.borderColor = borderColor
                contentDescription = context.getString(R.string.user_profile_image)
                setImageResource(R.drawable.image_placeholder)
            }
        },
        update = { imageView ->
            if (!imageUrl.isNullOrEmpty()) {
                imageView.load(imageUrl) {
                    scale(Scale.FIT)
                    placeholder(R.drawable.image_placeholder)
                    error(R.drawable.image_placeholder)
                }
            }
        },
    )
}

/** `@drawable/items_border` card: 16dp margin, 8dp padding, white, 5dp corners, 1dp #F0F0F0 stroke. */
@Composable
private fun InfoCard(content: @Composable () -> Unit) {
    val shape = RoundedCornerShape(5.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.White, shape)
            .border(1.dp, Color(0xFFF0F0F0), shape)
            .padding(8.dp),
    ) { content() }
}

/** Title with its start icon on the left, expand/collapse icon (48dp) on the right. */
@Composable
private fun SectionHeader(
    title: String,
    @DrawableRes icon: Int,
    iconTint: Color?,
    expanded: Boolean,
    onToggle: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                colorFilter = iconTint?.let { ColorFilter.tint(it) },
            )
            Spacer(Modifier.width(10.dp)) // drawablePadding
            BasicText(
                text = title,
                style = TextStyle(color = colorResource(R.color.colorPrimary), fontSize = 16.sp),
            )
        }
        // iv_hideShowPersonalInfo / iv_hideShowWarehouseInfo: hide_icon when open, show_icon when collapsed
        Box(
            modifier = Modifier
                .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                .clickable(role = Role.Button, onClick = onToggle)
                .padding(10.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(if (expanded) R.drawable.hide_icon else R.drawable.show_icon),
                contentDescription = stringResource(R.string.expand_or_collapse),
            )
        }
    }
}

/**
 * Old behaviour: expanding slid the content in from below over 200ms (TranslateAnimation),
 * collapsing was instant (duration 0, then GONE).
 */
@Composable
private fun CollapsibleContent(visible: Boolean, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(animationSpec = tween(200), initialOffsetY = { it }),
        exit = ExitTransition.None,
    ) { content() }
}

/** title_style label (16sp, bold_medium, black), 10dp top margin. */
@Composable
private fun InfoLabel(text: String) {
    BasicText(
        text = text,
        modifier = Modifier.padding(top = 10.dp),
        style = TextStyle(
            color = colorResource(R.color.colorBlack),
            fontSize = 16.sp,
            fontFamily = BoldMediumFont,
        ),
    )
}

/** sub_title_gray_style value (14sp, regular, textColorDarker), 10dp top margin. */
@Composable
private fun InfoValue(text: String) {
    BasicText(
        text = text,
        modifier = Modifier.padding(top = 10.dp),
        style = TextStyle(
            color = colorResource(R.color.textColorDarker),
            fontSize = 14.sp,
            fontFamily = RegularFont,
        ),
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen(
        name = "Sample User",
        email = "user@example.com",
        imageUrl = null,
        onBack = {},
        onChangePassword = {},
    )
}

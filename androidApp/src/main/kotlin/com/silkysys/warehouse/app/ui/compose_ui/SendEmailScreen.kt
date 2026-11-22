package com.silkysys.warehouse.app.ui.compose_ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
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
import com.silkysys.warehouse.R

private val BoldMediumFont = FontFamily(Font(R.font.bold_medium))
private val RegularFont = FontFamily(Font(R.font.regular))

@Composable
fun SendEmailScreen(
    email: String,
    onEmailChange: (String) -> Unit,
    onSend: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Old shape: android:angle="270" (top -> bottom), startColor = gradient_end, endColor = gradient_start
    val gradient = Brush.verticalGradient(
        colors = listOf(
            colorResource(R.color.gradient_end),
            colorResource(R.color.gradient_start),
        ),
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding(),
    ) {
        // app_bar_022 (fitsSystemWindows -> statusBarsPadding)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradient)
                .statusBarsPadding(),
        ) {
            // toolbar (actionBarSize)
            Box(modifier = Modifier.fillMaxWidth().height(56.dp)) {
                // iv_backarrow
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
                    text = stringResource(R.string.update_password),
                    modifier = Modifier.align(Alignment.Center),
                    style = TextStyle(
                        color = colorResource(R.color.colorLight),
                        fontSize = 24.sp,
                        fontFamily = BoldMediumFont,
                    ),
                )
            }
        }

        // content: 20dp side margins, 50dp below the app bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 50.dp, end = 20.dp),
        ) {
            // logo: 100dp high, 150dp wide box (wrap_content), fitCenter
            Image(
                painter = painterResource(R.drawable.login_logo),
                contentDescription = null, // decorative, matches importantForAccessibility="no"
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 25.dp)
                    .size(width = 150.dp, height = 100.dp),
                contentScale = ContentScale.Fit,
            )

            // sub_title_style, gray_01, 14sp
            BasicText(
                text = stringResource(R.string.enter_your_email_address_to_send_you_new_password),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 20.dp),
                style = TextStyle(
                    color = colorResource(R.color.gray_01),
                    fontSize = 14.sp,
                    fontFamily = RegularFont,
                ),
            )

            // "Email" label: sub_title_style at 12sp
            BasicText(
                text = stringResource(R.string.email),
                modifier = Modifier.padding(start = 16.dp, top = 20.dp),
                style = TextStyle(
                    color = colorResource(R.color.colorBlack),
                    fontSize = 12.sp,
                    fontFamily = RegularFont,
                ),
            )

            // et_email: edit_text_auth_style (16dp side margins, 8dp padding, gray_stroke_no_fill), 12sp
            val shape = RoundedCornerShape(8.dp)
            BasicTextField(
                value = email,
                onValueChange = onEmailChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 5.dp, end = 16.dp),
                textStyle = TextStyle(
                    color = colorResource(R.color.gray_01),
                    fontSize = 12.sp,
                    fontFamily = RegularFont,
                ),
                cursorBrush = SolidColor(colorResource(R.color.colorPrimary)),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, shape)
                            .border(1.dp, Color(0xFFD9DFE8), shape)
                            .padding(8.dp),
                    ) {
                        if (email.isEmpty()) {
                            BasicText(
                                text = stringResource(R.string.user_email),
                                style = TextStyle(
                                    color = Color(0x61000000), // default hint colour
                                    fontSize = 12.sp,
                                    fontFamily = RegularFont,
                                ),
                            )
                        }
                        innerTextField()
                    }
                },
            )

            // bt_sendEmail (button_style: 16dp side margins, min height 48dp; marginTop 20dp)
            Box(
                modifier = Modifier
                    .padding(start = 16.dp, top = 20.dp, end = 16.dp)
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(colorResource(R.color.colorPrimary))
                    .clickable(role = Role.Button, onClick = onSend),
                contentAlignment = Alignment.Center,
            ) {
                BasicText(
                    text = stringResource(R.string.send),
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
private fun SendEmailScreenPreview() {
    SendEmailScreen(email = "", onEmailChange = {}, onSend = {}, onBack = {})
}

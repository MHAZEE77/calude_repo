package com.silkysys.warehouse.app.ui.compose_ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.silkysys.warehouse.R

private val BoldMediumFont = FontFamily(Font(R.font.bold_medium))
private val RegularFont = FontFamily(Font(R.font.regular))

@Composable
fun ForgotPasswordScreen(
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    onCreate: () -> Unit,
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

    Column(modifier = modifier.fillMaxSize()) {
        // app_bar_24 (fitsSystemWindows -> statusBarsPadding)
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

        // ScrollView: paddingTop 50dp, paddingBottom 100dp, clipToPadding=false
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(top = 50.dp, bottom = 100.dp),
        ) {
            // logo: 100dp high, wrap_content wide (150dp), default fitCenter
            Image(
                painter = painterResource(R.drawable.login_logo),
                contentDescription = null, // decorative, matches importantForAccessibility="no"
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(width = 150.dp, height = 100.dp),
                contentScale = ContentScale.Fit,
            )

            // textView14: sub_title_gray_style
            BasicText(
                text = stringResource(R.string.create_new_password),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(start = 16.dp, top = 32.dp, end = 16.dp),
                style = TextStyle(
                    color = colorResource(R.color.textColorDarker),
                    fontSize = 14.sp,
                    fontFamily = RegularFont,
                ),
            )

            // password rules: sub_title_gray_style at 12sp, red
            BasicText(
                text = stringResource(R.string.password_criteria),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(start = 16.dp, top = 32.dp, end = 16.dp),
                style = TextStyle(
                    color = colorResource(R.color.red),
                    fontSize = 12.sp,
                    fontFamily = RegularFont,
                ),
            )

            PasswordField(
                value = password,
                onValueChange = onPasswordChange,
                hint = stringResource(R.string.password_hint),
                modifier = Modifier.padding(top = 32.dp),
            )
            PasswordField(
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                hint = stringResource(R.string.confirm_password),
                modifier = Modifier.padding(top = 8.dp),
            )

            // createButton (button_style: 16dp side/top margins, min height 48dp, round_edges)
            Box(
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(colorResource(R.color.colorPrimary))
                    .clickable(role = Role.Button, onClick = onCreate),
                contentAlignment = Alignment.Center,
            ) {
                BasicText(
                    text = stringResource(R.string.create),
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

/**
 * Replaces the custom `PasswordEditText` (edit_text_auth_style): masked input with a
 * show/hide icon at the end (ic_visibility_on while hidden, ic_visibility_off while visible),
 * `actionDone`, 16dp side margins.
 */
@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier,
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val shape = RoundedCornerShape(8.dp)

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        singleLine = true,
        textStyle = TextStyle(
            color = colorResource(R.color.colorBlack),
            fontSize = 14.sp,
            fontFamily = RegularFont,
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        cursorBrush = SolidColor(colorResource(R.color.colorPrimary)),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .background(Color.White, shape)
                    .border(1.dp, Color(0xFFD9DFE8), shape)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        BasicText(
                            text = hint,
                            style = TextStyle(
                                color = Color(0x61000000), // default hint colour
                                fontSize = 14.sp,
                                fontFamily = RegularFont,
                            ),
                        )
                    }
                    innerTextField()
                }
                Box(
                    modifier = Modifier
                        .clickable(role = Role.Button) { passwordVisible = !passwordVisible }
                        .padding(start = 8.dp), // drawablePadding
                ) {
                    Image(
                        painter = painterResource(
                            if (passwordVisible) R.drawable.ic_visibility_off else R.drawable.ic_visibility_on,
                        ),
                        contentDescription = stringResource(
                            if (passwordVisible) R.string.hide_password else R.string.show_password,
                        ),
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        },
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ForgotPasswordScreenPreview() {
    ForgotPasswordScreen(
        password = "",
        onPasswordChange = {},
        confirmPassword = "",
        onConfirmPasswordChange = {},
        onCreate = {},
        onBack = {},
    )
}

package com.silkysys.warehouse.app.ui.compose_ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.silkysys.warehouse.R

enum class LoginField { Email, Password }

/** UI state for [LoginScreen]. Owned by LoginActivity so its existing logic can read/write it. */
@Stable
class LoginFormState {
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var rememberMe by mutableStateOf(false)
    var emailError by mutableStateOf<String?>(null)
    var passwordError by mutableStateOf<String?>(null)
    var isSignInEnabled by mutableStateOf(true)

    /** Set to ask the screen to move focus to a field (replaces `editText.requestFocus()`). */
    var focusRequest by mutableStateOf<LoginField?>(null)
}

private val RegularFont = FontFamily(Font(R.font.regular))

private val CardStroke = Color(0xFFD9DFE8)


@Composable
fun LoginScreen(
    form: LoginFormState,
    onSignIn: () -> Unit,
    onForgotPassword: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Old shape: android:angle="270" (top -> bottom), startColor = gradient_end, endColor = gradient_start
    val gradient = Brush.verticalGradient(
        colors = listOf(
            colorResource(R.color.gradient_end),
            colorResource(R.color.gradient_start),
        ),
    )
    val emailFocus = remember { FocusRequester() }
    val passwordFocus = remember { FocusRequester() }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(form.focusRequest) {
        val target = form.focusRequest ?: return@LaunchedEffect
        when (target) {
            LoginField.Email -> emailFocus.requestFocus()
            LoginField.Password -> passwordFocus.requestFocus()
        }
        form.focusRequest = null
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(gradient),
    ) {
        // ScrollView(fillViewport) + LinearLayout(gravity = center_vertical)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, top = 32.dp, end = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            // lnContainer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .border(1.dp, CardStroke, RoundedCornerShape(8.dp))
                    .padding(20.dp),
            ) {
                BasicText(
                    text = stringResource(R.string.enter_credentials),
                    style = TextStyle(
                        color = colorResource(R.color.gray_01),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                )

                FieldLabel(stringResource(R.string.email))
                LoginTextField(
                    value = form.email,
                    onValueChange = {
                        form.email = it
                        form.emailError = null
                    },
                    hint = stringResource(R.string.email_hint),
                    textColor = colorResource(R.color.gray_01),
                    errorMessage = form.emailError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next,
                    ),
                    modifier = Modifier
                        .focusRequester(emailFocus)
                        .semantics { contentType = ContentType.EmailAddress + ContentType.Username },
                )

                FieldLabel(stringResource(R.string.password))
                LoginTextField(
                    value = form.password,
                    onValueChange = {
                        form.password = it
                        form.passwordError = null
                    },
                    hint = stringResource(R.string.password_hint),
                    textColor = colorResource(R.color.colorBlack),
                    errorMessage = form.passwordError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(onDone = { onSignIn() }),
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    modifier = Modifier.focusRequester(passwordFocus),
                    trailing = {
                        // Same icons / content descriptions as the old PasswordEditText
                        Box(
                            modifier = Modifier
                                .clickable(role = Role.Button) { passwordVisible = !passwordVisible }
                                .padding(start = 8.dp, top = 4.dp, bottom = 4.dp),
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
                    },
                )

                // cb_remeberMe
                Row(
                    modifier = Modifier
                        .height(48.dp)
                        .toggleable(
                            value = form.rememberMe,
                            role = Role.Checkbox,
                            onValueChange = { form.rememberMe = it },
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CheckMark(checked = form.rememberMe, color = colorResource(R.color.colorPrimary))
                    Spacer(Modifier.width(12.dp))
                    BasicText(
                        text = stringResource(R.string.remember_email),
                        style = TextStyle(
                            color = colorResource(R.color.placeholder_color),
                            fontSize = 14.sp,
                            fontFamily = RegularFont,
                        ),
                    )
                }

                // signInButton
                Box(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 10.dp)
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(colorResource(R.color.colorPrimary))
                        .clickable(enabled = form.isSignInEnabled, role = Role.Button, onClick = onSignIn),
                    contentAlignment = Alignment.Center,
                ) {
                    BasicText(
                        text = stringResource(R.string.sign_in),
                        style = TextStyle(
                            color = colorResource(R.color.colorWhite),
                            fontSize = 14.sp,
                            fontFamily = RegularFont,
                        ),
                    )
                }

                // tv_forgot_password
                BasicText(
                    text = stringResource(R.string.forgot_password),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clickable(role = Role.Button, onClick = onForgotPassword),
                    style = TextStyle(
                        color = colorResource(R.color.light_blue),
                        fontSize = 14.sp,
                        fontFamily = RegularFont,
                        textAlign = TextAlign.Center,
                    ),
                )
            }

            // Logo row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .height(60.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.silky_logo),
                    contentDescription = null, // decorative, matches importantForAccessibility="no"
                    modifier = Modifier.width(30.dp),
                )
                Image(
                    painter = painterResource(R.drawable.silky_text),
                    contentDescription = stringResource(R.string.silky_systems),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 10.dp)
                        .size(width = 180.dp, height = 48.dp),
                )
            }
        }
    }
}

/** The "Email" / "Password" TextViews above each field. */
@Composable
private fun FieldLabel(text: String) {
    BasicText(
        text = text,
        modifier = Modifier.padding(start = 16.dp, top = 20.dp),
        style = TextStyle(
            color = colorResource(R.color.colorBlack),
            fontSize = 14.sp,
            fontFamily = RegularFont,
        ),
    )
}

/** Replaces `edit_text_auth_style` EditText (white rounded box, 1dp stroke, 8dp padding). */
@Composable
private fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    textColor: Color,
    errorMessage: String?,
    keyboardOptions: KeyboardOptions,
    modifier: Modifier = Modifier,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailing: (@Composable () -> Unit)? = null,
) {
    val shape = RoundedCornerShape(8.dp)
    val borderColor = if (errorMessage != null) colorResource(R.color.red) else CardStroke

    Column(modifier = Modifier.padding(start = 16.dp, top = 5.dp, end = 16.dp)) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier
                .fillMaxWidth()
                .semantics { if (errorMessage != null) error(errorMessage) },
            singleLine = true,
            textStyle = TextStyle(color = textColor, fontSize = 14.sp, fontFamily = RegularFont),
            cursorBrush = SolidColor(colorResource(R.color.colorPrimary)),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .background(Color.White, shape)
                        .border(1.dp, borderColor, shape)
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            BasicText(
                                text = hint,
                                style = TextStyle(
                                    color = colorResource(R.color.placeholder_color),
                                    fontSize = 14.sp,
                                    fontFamily = RegularFont,
                                ),
                            )
                        }
                        innerTextField()
                    }
                    trailing?.invoke()
                }
            },
        )
        if (errorMessage != null) {
            BasicText(
                text = errorMessage,
                modifier = Modifier.padding(top = 4.dp),
                style = TextStyle(
                    color = colorResource(R.color.red),
                    fontSize = 12.sp,
                    fontFamily = RegularFont,
                ),
            )
        }
    }
}

/** CheckBox tinted with colorPrimary (foundation-only, so no material dependency is needed). */
@Composable
private fun CheckMark(checked: Boolean, color: Color) {
    Canvas(modifier = Modifier.size(18.dp)) {
        val strokeWidth = 2.dp.toPx()
        val radius = CornerRadius(2.dp.toPx())
        if (checked) {
            drawRoundRect(color = color, cornerRadius = radius)
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
                color = color,
                topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                size = Size(size.width - strokeWidth, size.height - strokeWidth),
                cornerRadius = radius,
                style = Stroke(width = strokeWidth),
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    LoginScreen(form = LoginFormState(), onSignIn = {}, onForgotPassword = {})
}

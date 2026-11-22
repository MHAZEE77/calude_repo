package com.silkysys.warehouse.app.ui.compose_ui.barcode

import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.runtime.remember
import com.silkysys.warehouse.R

@Composable
fun BarcodeScanningScreen(
    previewView: PreviewView?,
    barcodeText: String,
    onBarcodeTextChange: (String) -> Unit,
    barcodeError: String?,
    isManualEntryMode: Boolean,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val barcodeFocus = remember { FocusRequester() }

    // showManualEntryMode(): binding.etBarcode.requestFocus()
    LaunchedEffect(isManualEntryMode) {
        if (isManualEntryMode) barcodeFocus.requestFocus()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .imePadding(),
    ) {
        // previewView
        if (!isManualEntryMode && previewView != null) {
            AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
        }

        // instructionText (50dp above the frame) + scanningFrame (250dp, centred on the screen)
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.BottomCenter,
            ) {
                BasicText(
                    text = stringResource(
                        if (isManualEntryMode) R.string.camera_permission_manual_entry
                        else R.string.position_barcode_within_frame,
                    ),
                    modifier = Modifier.padding(bottom = 50.dp),
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                    ),
                )
            }
            if (!isManualEntryMode) {
                // @drawable/scanning_overlay: transparent, 5dp #00FF00 stroke, 8dp corners
                Box(
                    modifier = Modifier
                        .size(250.dp)
                        .border(5.dp, Color(0xFF00FF00), RoundedCornerShape(8.dp)),
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }

        // bottom bar: etBarcode + barcodeOk
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(colorResource(R.color.colorBlack))
                .padding(10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BarcodeField(
                    value = barcodeText,
                    onValueChange = onBarcodeTextChange,
                    errorMessage = barcodeError,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 10.dp)
                        .focusRequester(barcodeFocus),
                )
                // barcodeOk: default Button (min 88x48dp, 16dp side padding) on @drawable/round_edges
                Box(
                    modifier = Modifier
                        .widthIn(min = 88.dp)
                        .heightIn(min = 48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(colorResource(R.color.colorPrimary))
                        .clickable(role = Role.Button, onClick = onSubmit)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    BasicText(
                        text = stringResource(R.string.ok).uppercase(),
                        style = TextStyle(
                            color = colorResource(R.color.colorWhite),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                    )
                }
            }
            if (barcodeError != null) {
                BasicText(
                    text = barcodeError,
                    modifier = Modifier.padding(top = 4.dp),
                    style = TextStyle(color = colorResource(R.color.red), fontSize = 12.sp),
                )
            }
        }

        // backButton: 50dp round (colorPrimary), 10dp margin, 24dp back icon centred
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp)
                .size(50.dp)
                .clip(CircleShape)
                .background(colorResource(R.color.colorPrimary))
                .clickable(role = Role.Button, onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.back_icon),
                contentDescription = stringResource(R.string.back),
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

/** `etBarcode`: `@drawable/gray_stroke_no_fill` (white, 1dp #D9DFE8 stroke, 8dp corners, 8dp padding). */
@Composable
private fun BarcodeField(
    value: String,
    onValueChange: (String) -> Unit,
    errorMessage: String?,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(8.dp)
    val borderColor = if (errorMessage != null) colorResource(R.color.red) else Color(0xFFD9DFE8)
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.semantics { if (errorMessage != null) error(errorMessage) },
        singleLine = true,
        textStyle = TextStyle(color = Color(0xDE000000), fontSize = 18.sp),
        cursorBrush = SolidColor(colorResource(R.color.colorPrimary)),
        // inputType="textVisiblePassword" (no suggestions, text stays visible), imeOptions="actionDone"
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape)
                    .border(1.dp, borderColor, shape)
                    .padding(8.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (value.isEmpty()) {
                    BasicText(
                        text = stringResource(R.string.enter_barcode),
                        style = TextStyle(color = Color(0x61000000), fontSize = 18.sp),
                    )
                }
                innerTextField()
            }
        },
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun BarcodeScanningScreenPreview() {
    BarcodeScanningScreen(
        previewView = null,
        barcodeText = "",
        onBarcodeTextChange = {},
        barcodeError = null,
        isManualEntryMode = false,
        onSubmit = {},
        onBack = {},
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun BarcodeScanningScreenManualEntryPreview() {
    BarcodeScanningScreen(
        previewView = null,
        barcodeText = "123",
        onBarcodeTextChange = {},
        barcodeError = null,
        isManualEntryMode = true,
        onSubmit = {},
        onBack = {},
    )
}
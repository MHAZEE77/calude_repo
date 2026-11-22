package com.silkysys.warehouse.app.ui.compose_ui.addstock

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.silkysys.warehouse.R

@Composable
fun BarcodeDialogContent(
    onBarcodeEntered: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var text by remember { mutableStateOf("") }

    // editText.requestFocus() on an input type "null" field: focused, but no soft keyboard
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.hide()
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
        contentAlignment = Alignment.Center,
    ) {
        // lbl1 is centred; the icon sits 20dp to its left
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                // @drawable/small_qr: ic_qr at 30dp with 4dp insets
                Image(
                    painter = painterResource(R.drawable.ic_qr),
                    contentDescription = null, // decorative, matches importantForAccessibility="no"
                    modifier = Modifier.padding(end = 20.dp).padding(4.dp).size(30.dp),
                )
            }
            BasicText(
                text = stringResource(R.string.scan_product),
                style = TextStyle(
                    color = Color(0xDE000000),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        // TextInputLayout / editText (visibility="invisible"): not drawn, but focusable
        BasicTextField(
            value = text,
            onValueChange = {
                text = it
                if (it.isNotEmpty()) onBarcodeEntered(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .alpha(0f)
                .focusRequester(focusRequester),
            singleLine = true,
        )
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
private fun BarcodeDialogContentPreview() {
    BarcodeDialogContent(onBarcodeEntered = {})
}

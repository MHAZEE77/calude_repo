package com.silkysys.warehouse.app.ui.compose_ui.dialog_boxes

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.silkysys.warehouse.R

@Composable
fun ErrorAlertDialog(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    StatusAlertDialog(
        title = title,
        message = message,
        confirmText = confirmText,
        onConfirm = onConfirm,
        iconRes = R.drawable.ic_cancel,
        iconTint = null,
        modifier = modifier,
    )
}

@Composable
fun SuccessAlertDialog(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    StatusAlertDialog(
        title = title,
        message = message,
        confirmText = confirmText,
        onConfirm = onConfirm,
        iconRes = R.drawable.ic_tick,
        iconTint = colorResource(R.color.success),
        modifier = modifier,
    )
}

@Composable
private fun StatusAlertDialog(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    @DrawableRes iconRes: Int,
    iconTint: Color?,
    modifier: Modifier = Modifier,
) {
    val buttonColor = colorResource(R.color.colorPrimary)

    AlertDialog(
        onDismissRequest = {},
        modifier = modifier,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
        ),
        shape = RoundedCornerShape(4.dp),
        containerColor = Color.White,
        title = {
            Text(
                text = title,
                color = colorResource(R.color.colorBlack),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
            )
        },
        text = { StatusAlertContent(message = message, iconRes = iconRes, iconTint = iconTint) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = confirmText, color = buttonColor)
            }
        },
    )
}

/** 100dp icon (16dp margin) + centered 18sp message (16dp margin). */
@Composable
internal fun StatusAlertContent(
    message: String,
    @DrawableRes iconRes: Int,
    iconTint: Color?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = null, // decorative, same as importantForAccessibility="no"
            modifier = Modifier
                .padding(16.dp)
                .size(100.dp),
            colorFilter = iconTint?.let { ColorFilter.tint(it) },
        )
        Text(
            text = message,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = colorResource(R.color.primaryText),
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true, name = "Error alert body")
@Composable
private fun ErrorAlertContentPreview() {
    StatusAlertContent(
        message = "Invalid credentials, please try again",
        iconRes = R.drawable.ic_cancel,
        iconTint = null,
    )
}

@Preview(showBackground = true, name = "Success alert body")
@Composable
private fun SuccessAlertContentPreview() {
    StatusAlertContent(
        message = "Your password has been updated",
        iconRes = R.drawable.ic_tick,
        iconTint = colorResource(R.color.success),
    )
}

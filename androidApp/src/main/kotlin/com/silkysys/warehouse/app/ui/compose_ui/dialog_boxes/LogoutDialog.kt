package com.silkysys.warehouse.app.ui.compose_ui.dialog_boxes

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.silkysys.warehouse.R

/**
 * Compose version of the `alert { ... }` / `CustomAlertDialog` helper:
 * optional [title], [message], a positive button and an optional negative button.
 *
 * - [cancelable] = false (like `isCancelable = false`) means back press and tapping outside do nothing.
 * - Show it by calling it from composition while your "show" state is true, and hide it in
 *   [onConfirm] / [onDismiss] by flipping that state.
 */
@Composable
fun LogoutDialog(
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    dismissText: String? = null,
    cancelable: Boolean = true,
) {
    val buttonColor = colorResource(R.color.colorPrimary)

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        properties = DialogProperties(
            dismissOnBackPress = cancelable,
            dismissOnClickOutside = cancelable,
        ),
        shape = RoundedCornerShape(4.dp),
        containerColor = Color.White,
        title = title?.let {
            {
                Text(
                    text = it,
                    color = colorResource(R.color.colorBlack),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        },
        text = {
            Text(text = message, color = Color(0xDE000000), fontSize = 16.sp)
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = confirmText, color = buttonColor)
            }
        },
        dismissButton = dismissText?.let {
            {
                TextButton(onClick = onDismiss) {
                    Text(text = it, color = buttonColor)
                }
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun AppAlertDialogPreview() {
    LogoutDialog(
        title = "Logout",
        message = "Are you sure to log out?",
        confirmText = "OK",
        dismissText = "Cancel",
        cancelable = false,
        onConfirm = {},
        onDismiss = {},
    )
}

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.silkysys.warehouse.R

@Composable
fun AppAlertDialog(
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
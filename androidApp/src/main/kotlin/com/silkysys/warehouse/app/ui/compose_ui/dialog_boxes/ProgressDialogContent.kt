package com.silkysys.warehouse.app.ui.compose_ui.dialog_boxes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.silkysys.warehouse.R

@Composable
fun ProgressDialogContent(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .padding(24.dp)
            .semantics { liveRegion = LiveRegionMode.Polite },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // ProgressBar (indeterminate, tinted with the theme accent)
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = colorResource(R.color.colorAccent),
            strokeWidth = 4.dp,
        )
        Spacer(modifier = Modifier.width(15.dp))
        Text(
            text = stringResource(R.string.request_loading_dialog),
            color = Color(0xDE000000),
            fontSize = 16.sp,
        )
    }
}

@Composable
fun AppProgressDialog(widthFraction: Float = 0.85f) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false, // lets the content choose its own width
        ),
    ) {
        ProgressDialogContent(
            modifier = Modifier
                .fillMaxWidth(widthFraction)
                .widthIn(max = 560.dp)
                .background(Color.White, RoundedCornerShape(4.dp)),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProgressDialogContentPreview() {
    ProgressDialogContent()
}
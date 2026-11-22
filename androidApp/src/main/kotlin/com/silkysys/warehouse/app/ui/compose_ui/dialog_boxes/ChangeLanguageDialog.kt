package com.silkysys.warehouse.app.ui.compose_ui.dialog_boxes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.silkysys.warehouse.R


@Composable
fun ChangeLanguageDialog(
    title: String,
    languages: List<String>,
    initialSelectedIndex: Int,
    confirmText: String,
    dismissText: String,
    onConfirm: (selectedIndex: Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // selectIndex of the old dialog
    var selectedIndex by remember { mutableIntStateOf(initialSelectedIndex) }
    val primary = colorResource(R.color.colorPrimary)

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
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
        text = {
            Column(modifier = Modifier.fillMaxWidth().selectableGroup()) {
                languages.forEachIndexed { index, language ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .selectable(
                                selected = index == selectedIndex,
                                role = Role.RadioButton,
                                onClick = { selectedIndex = index },
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = index == selectedIndex,
                            onClick = null, // the whole row is the click target
                            colors = RadioButtonDefaults.colors(
                                selectedColor = primary,
                                unselectedColor = Color(0x99000000),
                            ),
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = language, color = Color(0xDE000000), fontSize = 16.sp)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedIndex) }) {
                Text(text = confirmText, color = primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = dismissText, color = primary)
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun ChangeLanguageDialogPreview() {
    ChangeLanguageDialog(
        title = "Change Language",
        languages = listOf("English", "العربية"),
        initialSelectedIndex = 0,
        confirmText = "Change",
        dismissText = "Cancel",
        onConfirm = {},
        onDismiss = {},
    )
}

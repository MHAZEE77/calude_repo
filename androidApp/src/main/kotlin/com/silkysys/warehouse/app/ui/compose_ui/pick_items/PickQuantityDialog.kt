package com.silkysys.warehouse.app.ui.compose_ui.pick_items

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.silkysys.warehouse.R
import com.silkysys.warehouse.app.ui.compose_ui.pick.PickBoldMediumFont
import com.silkysys.warehouse.app.ui.compose_ui.pick.PickCheckBox
import com.silkysys.warehouse.app.ui.compose_ui.pick.PickItemImage
import com.silkysys.warehouse.app.ui.compose_ui.pick.PickRegularFont
import com.silkysys.warehouse.app.ui.compose_ui.pick.PickTextColorPrimary
import com.silkysys.warehouse.sharedcode.domain.picking.startPick.entity.StartPickEntity

/**
 * Compose replacement for the `pick_pop_up.xml` dialog that PickItemsActivity used to build
 * by hand with findViewById.
 *
 * @param orderName title of the dialog (the pick order's name).
 * @param onSubmit called with (quantity to pick now, issue checked, note). The note is only
 *   meaningful when the issue box is checked, the caller decides what to send otherwise.
 */
@Composable
fun PickQuantityDialog(
    orderName: String,
    item: StartPickEntity.Fulfillment.Item,
    onDismiss: () -> Unit,
    onSubmit: (quantity: Int, hasIssue: Boolean, note: String) -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        PickQuantityDialogContent(orderName = orderName, item = item, onSubmit = onSubmit)
    }
}

/** The card inside the dialog, split out so it can be previewed (Dialog windows don't preview well). */
@Composable
internal fun PickQuantityDialogContent(
    orderName: String,
    item: StartPickEntity.Fulfillment.Item,
    onSubmit: (quantity: Int, hasIssue: Boolean, note: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val remain = item.quantity - item.picked_quantity
    var quantity by remember(item) { mutableIntStateOf(1) }
    var hasIssue by remember(item) { mutableStateOf(false) }
    var note by remember(item) { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(Color.White, RoundedCornerShape(10.dp))
            .padding(14.dp),
    ) {
        BasicText(
            text = orderName,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            style = TextStyle(
                color = PickTextColorPrimary,
                fontSize = 20.sp,
                fontFamily = PickBoldMediumFont,
                textAlign = TextAlign.Center,
            ),
        )

        Row {
            PickItemImage(
                url = item.image,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .size(80.dp)
                    .clip(RoundedCornerShape(4.dp)),
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
            ) {
                BasicText(
                    text = item.name,
                    style = TextStyle(
                        color = PickTextColorPrimary,
                        fontSize = 18.sp,
                        fontFamily = PickBoldMediumFont,
                    ),
                )
                BasicText(
                    text = item.description ?: "Item description",
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        color = colorResource(R.color.textColor),
                        fontSize = 16.sp,
                        fontFamily = PickRegularFont,
                    ),
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    DialogChip(
                        label = stringResource(R.string.remain),
                        value = remain.toString(),
                        background = colorResource(R.color.colorBackgroundLight),
                        modifier = Modifier.weight(1f),
                    )
                    DialogChip(
                        label = stringResource(R.string.picked),
                        value = item.picked_quantity.toString(),
                        background = colorResource(R.color.accentLight),
                        modifier = Modifier.weight(1f),
                    )
                }

                Row(modifier = Modifier.padding(top = 10.dp)) {
                    BasicText(
                        text = stringResource(R.string.status_label),
                        style = TextStyle(
                            color = colorResource(R.color.textColorDarker),
                            fontSize = 14.sp,
                            fontFamily = PickRegularFont,
                        ),
                    )
                    BasicText(
                        text = item.status,
                        modifier = Modifier.padding(start = 4.dp),
                        style = TextStyle(
                            color = colorResource(R.color.textGreen),
                            fontSize = 14.sp,
                            fontFamily = PickRegularFont,
                        ),
                    )
                }
            }
        }

        // iv_decrease / tv_quantity / iv_increase
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clickable(role = Role.Button) {
                        if (quantity > 1) quantity -= 1
                    },
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.decrease_icon),
                    contentDescription = stringResource(R.string.decrease_quantity),
                    modifier = Modifier.size(44.dp),
                )
            }
            BasicText(
                text = quantity.toString(),
                modifier = Modifier.padding(horizontal = 20.dp),
                style = TextStyle(
                    color = PickTextColorPrimary,
                    fontSize = 24.sp,
                    fontFamily = PickRegularFont,
                ),
            )
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clickable(role = Role.Button) {
                        if (quantity != remain) quantity += 1
                    },
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.increase_icon),
                    contentDescription = stringResource(R.string.increase_quantity),
                    modifier = Modifier.size(44.dp),
                )
            }
        }

        // cb_issue
        Row(
            modifier = Modifier.padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PickCheckBox(checked = hasIssue, onCheckedChange = { hasIssue = it })
            BasicText(
                text = stringResource(R.string.issue),
                modifier = Modifier.clickable { hasIssue = !hasIssue },
                style = TextStyle(
                    color = PickTextColorPrimary,
                    fontSize = 14.sp,
                    fontFamily = PickRegularFont,
                ),
            )
        }

        // usernameEditText / et_note (only while "Issue" is checked)
        if (hasIssue) {
            NoteField(
                value = note,
                onValueChange = { note = it },
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        // bt_submitQuantity
        Box(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
                .heightIn(min = 64.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(colorResource(R.color.colorPrimary))
                .clickable(role = Role.Button) { onSubmit(quantity, hasIssue, note) },
            contentAlignment = Alignment.Center,
        ) {
            BasicText(
                text = stringResource(R.string.submit),
                style = TextStyle(
                    color = colorResource(R.color.colorWhite),
                    fontSize = 16.sp,
                    fontFamily = PickRegularFont,
                ),
            )
        }
    }
}

@Composable
private fun DialogChip(
    label: String,
    value: String,
    background: Color,
    modifier: Modifier = Modifier,
) {
    val color = PickTextColorPrimary
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(background)
            .padding(8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicText(
            text = label,
            style = TextStyle(color = color, fontSize = 14.sp),
        )
        BasicText(
            text = value,
            modifier = Modifier.padding(start = 8.dp),
            style = TextStyle(color = color, fontSize = 14.sp),
        )
    }
}

/** Outlined "Note" field. */
@Composable
private fun NoteField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val outline = colorResource(R.color.colorPrimary)
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        textStyle = TextStyle(
            color = PickTextColorPrimary,
            fontSize = 16.sp,
            fontFamily = PickRegularFont,
        ),
        cursorBrush = SolidColor(outline),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .border(1.dp, outline, RoundedCornerShape(4.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (value.isEmpty()) {
                    BasicText(
                        text = stringResource(R.string.note),
                        style = TextStyle(
                            color = colorResource(R.color.textColor),
                            fontSize = 16.sp,
                            fontFamily = PickRegularFont,
                        ),
                    )
                }
                innerTextField()
            }
        },
    )
}


@Preview(showBackground = true, name = "Quantity dialog")
@Composable
private fun PickQuantityDialogContentPreview() {
    PickQuantityDialogContent(
        orderName = "SO-1024",
        item = StartPickEntity.Fulfillment.Item(
            description = "Ergonomic wireless mouse",
            id = 1, name = "Wireless Mouse", picked_quantity = 2, quantity = 10,
            image = "", status = "picking", barcode = "1001",
        ),
        onSubmit = { _, _, _ -> },
    )
}

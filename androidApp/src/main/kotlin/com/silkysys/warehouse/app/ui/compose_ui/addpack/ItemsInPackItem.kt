package com.silkysys.warehouse.app.ui.compose_ui.addpack

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.silkysys.warehouse.R
import com.silkysys.warehouse.sharedcode.domain.packing.packItems.entity.PackItemsEntity

/**
 * Compose replacement for `items_in_pack.xml` + the bind code of ItemsInPackAdapter:
 * item name (cut to 15 chars) | allowable quantity (centred) | [-] packed quantity [+].
 * An item with allowable quantity 0 is disabled. An invalid quantity (more than allowed)
 * turns the field red (it used to show `Invalid Quantity` as the EditText error).
 */
@Composable
fun ItemsInPackItem(
    item: PackItemsEntity.PackItems,
    quantityText: String,
    isInvalid: Boolean,
    onQuantityChange: (String) -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val enabled = item.allowedQuantity != 0
    val invalidMessage = "Invalid Quantity"

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp) // CardView layout_marginTop
            .itemsBorder()
            .padding(4.dp),
    ) {
        // tv_itemName (sub_title_style, maxLength 15)
        BasicText(
            text = item.name.take(15),
            modifier = Modifier.align(Alignment.CenterStart),
            style = TextStyle(
                color = colorResource(R.color.colorBlack),
                fontSize = 14.sp,
                fontFamily = AddPackRegularFont,
            ),
        )
        // tv_allowable (centred)
        BasicText(
            text = item.allowedQuantity.toString(),
            modifier = Modifier.align(Alignment.Center),
            style = TextStyle(
                color = colorResource(R.color.colorBlack),
                fontSize = 14.sp,
                fontFamily = AddPackRegularFont,
                textAlign = TextAlign.Center,
            ),
        )
        // decrease / packed quantity / increase
        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.Bottom,
        ) {
            Image(
                painter = painterResource(R.drawable.decrease_icon),
                contentDescription = stringResource(R.string.decrease_quantity),
                modifier = Modifier
                    .size(35.dp)
                    .clickable(enabled = enabled, role = Role.Button, onClick = onDecrease),
            )
            BasicTextField(
                value = quantityText,
                onValueChange = { onQuantityChange(it.filter { c -> c.isDigit() }) },
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(35.dp)
                    .alpha(if (enabled) 1f else 0.38f)
                    .semantics { if (isInvalid) error(invalidMessage) },
                enabled = enabled,
                singleLine = true,
                textStyle = TextStyle(
                    color = if (isInvalid) colorResource(R.color.red) else colorResource(R.color.colorBlack),
                    fontSize = 16.sp,
                    fontFamily = AddPackRegularFont,
                    textAlign = TextAlign.Center,
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                cursorBrush = SolidColor(colorResource(R.color.colorPrimary)),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .size(35.dp)
                            .darkerRectangle(
                                if (isInvalid) colorResource(R.color.red) else colorResource(R.color.colorPrimary),
                            )
                            .padding(horizontal = 4.dp),
                        contentAlignment = Alignment.Center,
                    ) { innerTextField() }
                },
            )
            Image(
                painter = painterResource(R.drawable.increase_icon),
                contentDescription = stringResource(R.string.increase_quantity),
                modifier = Modifier
                    .size(35.dp)
                    .clickable(enabled = enabled, role = Role.Button, onClick = onIncrease),
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun ItemsInPackItemPreview() {
    ItemsInPackItem(
        item = PackItemsEntity.PackItems(id = 1, name = "Sample product name", allowedQuantity = 5),
        quantityText = "2",
        isInvalid = false,
        onQuantityChange = {},
        onIncrease = {},
        onDecrease = {},
    )
}

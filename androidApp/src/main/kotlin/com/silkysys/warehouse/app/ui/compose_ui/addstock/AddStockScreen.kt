package com.silkysys.warehouse.app.ui.compose_ui.addstock

import android.view.View
import android.widget.ImageView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.load
import coil.size.Scale
import com.silkysys.warehouse.R

private val BoldMediumFont = FontFamily(Font(R.font.bold_medium))
private val RegularFont = FontFamily(Font(R.font.regular))
private val FieldStroke = Color(0xFFD9DFE8)

/** UI state of [AddStockScreen]; owned by AddStockActivity so its existing logic can read/write it. */
@Stable
class AddStockFormState {
    /** tvItemcode */
    var itemCode by mutableStateOf("")

    /** tvItemName */
    var itemName by mutableStateOf("")

    /** prodImage: null shows the placeholder */
    var imageUrl by mutableStateOf<String?>(null)

    /** etQty */
    var quantity by mutableStateOf("")

    /** spnWarehouse items (empty = no adapter, shows blank) */
    val warehouseNames = mutableStateListOf<String>()
    var selectedWarehouseIndex by mutableStateOf(0)

    /** spnBin items (empty = no adapter, shows blank) */
    val binNames = mutableStateListOf<String?>()
    var selectedBinIndex by mutableStateOf(0)
}

/**
 * Compose replacement for `activity_add_stock.xml`:
 * gradient app bar (back + "Add Stock"), then a scrolling form with product image, item code
 * (+ scan button, long press = type a code), product name, warehouse / bin spinners,
 * quantity and the "Update Stock" button.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AddStockScreen(
    form: AddStockFormState,
    onWarehouseSelected: (Int) -> Unit,
    onBinSelected: (Int) -> Unit,
    onBack: () -> Unit,
    onScan: () -> Unit,
    onScanLongClick: () -> Unit,
    onUpdateStock: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Old shape: android:angle="270" (top -> bottom), startColor = gradient_end, endColor = gradient_start
    val gradient = Brush.verticalGradient(
        colors = listOf(
            colorResource(R.color.gradient_end),
            colorResource(R.color.gradient_start),
        ),
    )

    Column(modifier = modifier.fillMaxSize()) {
        // appBar03 (fitsSystemWindows -> statusBarsPadding)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradient)
                .statusBarsPadding(),
        ) {
            // toolbar (actionBarSize)
            Box(modifier = Modifier.fillMaxWidth().height(56.dp)) {
                // iv_back
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(48.dp)
                        .clickable(role = Role.Button, onClick = onBack),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(R.drawable.back_icon),
                        contentDescription = stringResource(R.string.back),
                        modifier = Modifier.size(24.dp),
                    )
                }
                BasicText(
                    text = stringResource(R.string.add_stock),
                    modifier = Modifier.align(Alignment.Center),
                    style = TextStyle(
                        color = colorResource(R.color.colorLight),
                        fontSize = 24.sp,
                        fontFamily = BoldMediumFont,
                    ),
                )
            }
        }

        // ScrollView(fillViewport) > scanLinearLayout (10dp margin, 8dp padding)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(10.dp)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BasicText(
                text = stringResource(R.string.scan_product_to_add),
                modifier = Modifier.padding(top = 16.dp),
                style = TextStyle(
                    color = colorResource(R.color.colorBlack),
                    fontSize = 22.sp,
                    fontFamily = RegularFont,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                ),
            )

            ProductImage(
                imageUrl = form.imageUrl,
                modifier = Modifier.padding(top = 20.dp),
            )

            // item code
            Column(modifier = Modifier.fillMaxWidth().padding(top = 20.dp)) {
                FieldLabel(stringResource(R.string.item_code))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ReadOnlyField(
                        text = form.itemCode,
                        modifier = Modifier.weight(1f).padding(end = 5.dp),
                    )
                    // bt_startScan: 40dp, round_edges, qr icon tinted white; long click = type a code
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(colorResource(R.color.colorPrimary))
                            .combinedClickable(
                                role = Role.Button,
                                onClickLabel = stringResource(R.string.scan),
                                onClick = onScan,
                                onLongClick = onScanLongClick,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_qr),
                            contentDescription = stringResource(R.string.scan),
                            modifier = Modifier.size(30.dp),
                            colorFilter = ColorFilter.tint(colorResource(R.color.colorWhite)),
                        )
                    }
                }

                FieldLabel(stringResource(R.string.product_name), modifier = Modifier.padding(top = 20.dp))
                Row(modifier = Modifier.fillMaxWidth().padding(top = 5.dp)) {
                    ReadOnlyField(
                        text = form.itemName,
                        modifier = Modifier.weight(1f).padding(end = 5.dp),
                    )
                }
            }

            // warehouse
            Column(modifier = Modifier.fillMaxWidth().padding(top = 20.dp)) {
                FieldLabel(stringResource(R.string.ware_house))
                SpinnerField(
                    items = form.warehouseNames,
                    selectedIndex = form.selectedWarehouseIndex,
                    onSelected = onWarehouseSelected,
                )
            }

            // bin
            Column(modifier = Modifier.fillMaxWidth().padding(top = 20.dp)) {
                FieldLabel(stringResource(R.string.bin))
                SpinnerField(
                    items = form.binNames,
                    selectedIndex = form.selectedBinIndex,
                    onSelected = onBinSelected,
                )
            }

            // quantity
            Column(modifier = Modifier.fillMaxWidth().padding(top = 20.dp)) {
                FieldLabel(stringResource(R.string.qauntity))
                QuantityField(value = form.quantity, onValueChange = { form.quantity = it })
            }

            // bt_update_stock (button_style: 16dp side margins; background round_edges)
            Box(
                modifier = Modifier
                    .padding(start = 16.dp, top = 30.dp, end = 16.dp)
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(colorResource(R.color.colorPrimary))
                    .clickable(role = Role.Button, onClick = onUpdateStock),
                contentAlignment = Alignment.Center,
            ) {
                BasicText(
                    text = stringResource(R.string.update_stock),
                    style = TextStyle(
                        color = colorResource(R.color.colorWhite),
                        fontSize = 14.sp,
                        fontFamily = RegularFont,
                    ),
                )
            }
        }
    }
}

/** `@drawable/gray_stroke_no_fill`: white, 1dp #D9DFE8 stroke, 8dp corners. */
private fun Modifier.fieldBackground(): Modifier {
    val shape = RoundedCornerShape(8.dp)
    return this
        .background(Color.White, shape)
        .border(1.dp, FieldStroke, shape)
}

/** title_style label (overridden to 18sp) with 10dp start margin. */
@Composable
private fun FieldLabel(text: String, modifier: Modifier = Modifier) {
    BasicText(
        text = text,
        modifier = modifier.padding(start = 10.dp),
        style = TextStyle(
            color = colorResource(R.color.colorBlack),
            fontSize = 18.sp,
            fontFamily = BoldMediumFont,
        ),
    )
}

/** `tvItemcode` / `tvItemName`: 40dp high, centred text (sub_title_style) in the stroke box. */
@Composable
private fun ReadOnlyField(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .fieldBackground()
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            text = text,
            style = TextStyle(
                color = colorResource(R.color.colorBlack),
                fontSize = 14.sp,
                fontFamily = RegularFont,
                textAlign = TextAlign.Center,
            ),
        )
    }
}

/**
 * `prodImage`: stroke box with 8dp padding around the product picture.
 * Loaded with coil exactly like before (FIT scale, placeholder_view as placeholder/error).
 */
@Composable
private fun ProductImage(imageUrl: String?, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fieldBackground().padding(8.dp)) {
        AndroidView(
            factory = { context ->
                ImageView(context).apply {
                    importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
                }
            },
            update = { imageView ->
                if (imageUrl == null) {
                    imageView.setImageResource(R.drawable.placeholder_view)
                } else {
                    imageView.load(imageUrl) {
                        scale(Scale.FIT)
                        placeholder(R.drawable.placeholder_view)
                        error(R.drawable.placeholder_view)
                    }
                }
            },
        )
    }
}

/** `Spinner` with `@drawable/spinner_style` (stroke box + dropdown arrow), 40dp high. */
@Composable
private fun SpinnerField(
    items: List<String?>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier.fillMaxWidth().padding(top = 5.dp).height(40.dp)) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .fieldBackground()
                .clip(RoundedCornerShape(8.dp))
                .clickable(enabled = items.isNotEmpty(), role = Role.DropdownList) { expanded = true }
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicText(
                text = items.getOrNull(selectedIndex).orEmpty(),
                modifier = Modifier.weight(1f),
                style = TextStyle(
                    color = colorResource(R.color.colorBlack),
                    fontSize = 14.sp,
                    fontFamily = RegularFont,
                ),
            )
            Image(
                painter = painterResource(R.drawable.ic_arrow_drop),
                contentDescription = null,
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEachIndexed { index, item ->
                DropdownMenuItem(
                    text = {
                        BasicText(
                            text = item.orEmpty(),
                            style = TextStyle(
                                color = colorResource(R.color.colorBlack),
                                fontSize = 14.sp,
                                fontFamily = RegularFont,
                            ),
                        )
                    },
                    onClick = {
                        expanded = false
                        onSelected(index)
                    },
                )
            }
        }
    }
}

/** `etQty`: number input, centred text, `selectAllOnFocus`. */
@Composable
private fun QuantityField(value: String, onValueChange: (String) -> Unit) {
    var fieldValue by remember { mutableStateOf(TextFieldValue(value)) }
    // the activity resets the quantity to "0" after a successful update
    if (fieldValue.text != value) fieldValue = TextFieldValue(value, TextRange(value.length))

    BasicTextField(
        value = fieldValue,
        onValueChange = {
            fieldValue = it
            onValueChange(it.text)
        },
        modifier = Modifier
            .padding(top = 5.dp)
            .fillMaxWidth()
            .height(40.dp)
            .onFocusChanged {
                if (it.isFocused) fieldValue = fieldValue.copy(selection = TextRange(0, fieldValue.text.length))
            },
        singleLine = true,
        textStyle = TextStyle(
            color = colorResource(R.color.colorBlack),
            fontSize = 14.sp,
            fontFamily = RegularFont,
            textAlign = TextAlign.Center,
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        cursorBrush = SolidColor(colorResource(R.color.colorPrimary)),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .fieldBackground()
                    .padding(8.dp),
                contentAlignment = Alignment.Center,
            ) { innerTextField() }
        },
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AddStockScreenPreview() {
    val form = remember {
        AddStockFormState().apply {
            itemCode = "8901234567890"
            itemName = "Sample product"
            quantity = "0"
            warehouseNames.addAll(listOf("Select warehouse", "Main Warehouse"))
            binNames.addAll(listOf("Select bin location", "A-01"))
        }
    }
    AddStockScreen(
        form = form,
        onWarehouseSelected = { form.selectedWarehouseIndex = it },
        onBinSelected = { form.selectedBinIndex = it },
        onBack = {},
        onScan = {},
        onScanLongClick = {},
        onUpdateStock = {},
    )
}

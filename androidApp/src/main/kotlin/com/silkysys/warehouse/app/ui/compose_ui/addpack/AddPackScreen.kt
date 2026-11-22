package com.silkysys.warehouse.app.ui.compose_ui.addpack

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.silkysys.warehouse.R
import com.silkysys.warehouse.sharedcode.data.packing.addPack.dto.AddPackRequest
import com.silkysys.warehouse.sharedcode.domain.packing.packItems.entity.PackItemsEntity

/**
 * UI state of [AddPackScreen]; owned by AddPackActivity. It also holds what
 * `ItemsInPackAdapter` used to keep: the packed quantity of every row and the list of
 * packed items that is sent to the API.
 */
@Stable
class AddPackFormState {
    var height by mutableStateOf("")
    var width by mutableStateOf("")
    var length by mutableStateOf("")
    var weight by mutableStateOf("")

    /** rb_bag / rb_box (Bag is checked by default) */
    var isBagSelected by mutableStateOf(true)

    /** cb_isFragile */
    var isFragile by mutableStateOf(false)

    /** the rows of the "Other items" list */
    var items by mutableStateOf<List<PackItemsEntity.PackItems>>(emptyList())
        private set

    private val quantityTexts = mutableStateMapOf<Int, String>()
    private val invalidIds = mutableStateMapOf<Int, Boolean>()
    private val packedItems = ArrayList<AddPackRequest.Items>()

    /** A new ItemsInPackAdapter: every row starts at "0" and nothing is packed yet. */
    fun loadItems(newItems: List<PackItemsEntity.PackItems>) {
        items = newItems
        quantityTexts.clear()
        invalidIds.clear()
        packedItems.clear()
    }

    fun quantityText(id: Int): String = quantityTexts[id] ?: "0"

    fun isInvalid(id: Int): Boolean = invalidIds[id] == true

    /** The adapter's TextWatcher.afterTextChanged. */
    fun onQuantityChange(item: PackItemsEntity.PackItems, text: String) {
        quantityTexts[item.id] = text
        invalidIds.remove(item.id)
        if (text.isNotEmpty()) {
            // remove redundant
            for (i in 0 until packedItems.size) {
                if (item.id == packedItems[i].id) {
                    packedItems.remove(packedItems[i])
                    break
                }
            }
            // add not duplicated items
            val quantity = text.toIntOrNull() ?: return
            if (quantity > item.allowedQuantity) {
                invalidIds[item.id] = true
            } else {
                packedItems.add(AddPackRequest.Items(item.id, quantity))
            }
        }
    }

    /** iv_increasePack click. */
    fun increase(item: PackItemsEntity.PackItems) {
        invalidIds.remove(item.id)
        val text = quantityText(item.id)
        if (text.isNotEmpty()) {
            var packed = text.toIntOrNull() ?: return
            if (packed == item.allowedQuantity) {
                // already at the allowable quantity
            } else {
                packed++
                onQuantityChange(item, packed.toString())
            }
        }
    }

    /** iv_decreasePack click. */
    fun decrease(item: PackItemsEntity.PackItems) {
        invalidIds.remove(item.id)
        val text = quantityText(item.id)
        if (text.isNotEmpty()) {
            var packed = text.toIntOrNull() ?: return
            if (packed <= 0) {
                // nothing to decrease
            } else {
                packed--
                onQuantityChange(item, packed.toString())
            }
        }
    }

    /** ItemsInPackAdapter.getAllPackedItems() */
    fun getAllPackedItems(): ArrayList<AddPackRequest.Items> = packedItems
}

/**
 * Compose replacement for `activity_add_pack.xml` (8dp side margins like the old root):
 * gradient app bar (back + "Add new pack"), UOM radios, height / width / length / weight fields,
 * "Is Fragile", the "Other items" list ([AddPackHeaderItem] + [ItemsInPackItem]s) and the Pack button.
 */
@Composable
fun AddPackScreen(
    form: AddPackFormState,
    onBack: () -> Unit,
    onAddPack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Old shape: android:angle="270" (top -> bottom), startColor = gradient_end, endColor = gradient_start
    val gradient = Brush.verticalGradient(
        colors = listOf(
            colorResource(R.color.gradient_end),
            colorResource(R.color.gradient_start),
        ),
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .padding(horizontal = 8.dp),
    ) {
        // addPackHeader (fitsSystemWindows -> statusBarsPadding)
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
                    text = stringResource(R.string.add_new_pack),
                    modifier = Modifier.align(Alignment.Center),
                    style = TextStyle(
                        color = colorResource(R.color.colorLight),
                        fontSize = 24.sp,
                        fontFamily = AddPackBoldMediumFont,
                    ),
                )
            }
            // tabs_main: a TabLayout without tabs still draws its 48dp strip
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(colorResource(R.color.colorBackgroundLight)),
            )
        }

        // content (8dp margin)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(8.dp),
        ) {
            // UOM
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BasicText(
                    text = stringResource(R.string.uom),
                    style = TextStyle(
                        color = colorResource(R.color.colorBlack),
                        fontSize = 14.sp,
                        fontFamily = AddPackRegularFont,
                    ),
                )
                Spacer(Modifier.width(4.dp)) // drawablePadding
                Image(painter = painterResource(R.drawable.ic_required), contentDescription = null)
                Row(
                    modifier = Modifier.padding(start = 16.dp).selectableGroup(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    UomRadio(
                        label = stringResource(R.string.bag),
                        selected = form.isBagSelected,
                        onClick = { form.isBagSelected = true },
                    )
                    UomRadio(
                        label = stringResource(R.string.box),
                        selected = !form.isBagSelected,
                        onClick = { form.isBagSelected = false },
                        modifier = Modifier.padding(start = 16.dp),
                    )
                }
            }

            // height | width
            Row(modifier = Modifier.fillMaxWidth()) {
                DimensionField(
                    label = stringResource(R.string.height),
                    unit = stringResource(R.string.cm),
                    value = form.height,
                    onValueChange = { form.height = it },
                    modifier = Modifier.weight(1f),
                )
                DimensionField(
                    label = stringResource(R.string.width),
                    unit = stringResource(R.string.cm),
                    value = form.width,
                    onValueChange = { form.width = it },
                    modifier = Modifier.weight(1f),
                )
            }
            // length | weight
            Row(modifier = Modifier.fillMaxWidth()) {
                DimensionField(
                    label = stringResource(R.string.length),
                    unit = stringResource(R.string.cm),
                    value = form.length,
                    onValueChange = { form.length = it },
                    modifier = Modifier.weight(1f),
                )
                DimensionField(
                    label = stringResource(R.string.weight),
                    unit = stringResource(R.string.kg),
                    value = form.weight,
                    onValueChange = { form.weight = it },
                    modifier = Modifier.weight(1f),
                )
            }

            // cb_isFragile
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .toggleable(
                        value = form.isFragile,
                        role = Role.Checkbox,
                        onValueChange = { form.isFragile = it },
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = form.isFragile,
                    onCheckedChange = null, // the whole row is the click target
                    colors = CheckboxDefaults.colors(checkedColor = colorResource(R.color.colorAccent)),
                )
                Spacer(Modifier.width(8.dp))
                BasicText(
                    text = stringResource(R.string.is_fragile),
                    style = TextStyle(color = AddPackTextColorPrimary, fontSize = 14.sp),
                )
            }

            // title_style "Other items"
            BasicText(
                text = stringResource(R.string.other_items),
                modifier = Modifier.fillMaxWidth(),
                style = TextStyle(
                    color = colorResource(R.color.colorBlack),
                    fontSize = 16.sp,
                    fontFamily = AddPackBoldMediumFont,
                ),
            )

            // rv_packItems: header row + one row per item
            LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
                item { AddPackHeaderItem() }
                items(form.items) { item ->
                    ItemsInPackItem(
                        item = item,
                        quantityText = form.quantityText(item.id),
                        isInvalid = form.isInvalid(item.id),
                        onQuantityChange = { form.onQuantityChange(item, it) },
                        onIncrease = { form.increase(item) },
                        onDecrease = { form.decrease(item) },
                    )
                }
            }
        }

        // bt_addPack (button_style: 16dp side/top margins, min height 48dp; marginBottom 10dp)
        Box(
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 10.dp)
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(colorResource(R.color.colorPrimary))
                .clickable(role = Role.Button, onClick = onAddPack),
            contentAlignment = Alignment.Center,
        ) {
            BasicText(
                text = stringResource(R.string.pack),
                style = TextStyle(
                    color = colorResource(R.color.colorWhite),
                    fontSize = 14.sp,
                    fontFamily = AddPackRegularFont,
                ),
            )
        }
    }
}

/** A RadioButton with its label (the whole row selects). */
@Composable
private fun UomRadio(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .heightIn(min = 48.dp)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = colorResource(R.color.colorAccent),
                unselectedColor = Color(0x99000000),
            ),
        )
        Spacer(Modifier.width(8.dp))
        BasicText(text = label, style = TextStyle(color = AddPackTextColorPrimary, fontSize = 14.sp))
    }
}

/** One cell: label at the start, unit at the end, number field below (edit_text_add_pack). */
@Composable
private fun DimensionField(
    label: String,
    unit: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            BasicText(
                text = label,
                modifier = Modifier.weight(1f),
                style = TextStyle(color = AddPackTextColorPrimary, fontSize = 14.sp),
            )
            BasicText(
                text = unit,
                modifier = Modifier.padding(end = 4.dp),
                style = TextStyle(color = AddPackTextColorPrimary, fontSize = 14.sp),
            )
        }
        BasicTextField(
            value = value,
            onValueChange = { onValueChange(it.filter { c -> c.isDigit() }) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            textStyle = TextStyle(
                color = colorResource(R.color.colorBlack),
                fontSize = 16.sp,
                fontFamily = AddPackRegularFont,
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            cursorBrush = SolidColor(colorResource(R.color.colorPrimary)),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 40.dp)
                        .darkerRectangle(colorResource(R.color.colorPrimary))
                        .padding(horizontal = 4.dp),
                    contentAlignment = Alignment.CenterStart,
                ) { innerTextField() }
            },
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AddPackScreenPreview() {
    val form = remember {
        AddPackFormState().apply {
            loadItems(
                listOf(
                    PackItemsEntity.PackItems(1, "Sample product one", 5),
                    PackItemsEntity.PackItems(2, "Sample product two", 0),
                ),
            )
        }
    }
    AddPackScreen(form = form, onBack = {}, onAddPack = {})
}

package com.silkysys.warehouse.app.ui.compose_ui.addpack

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.silkysys.warehouse.R

@Composable
fun AddPackHeaderItem(modifier: Modifier = Modifier) {
    val style = TextStyle(
        color = colorResource(R.color.colorAccent),
        fontSize = 16.sp,
        fontFamily = AddPackBoldMediumFont,
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
    ) {
        BasicText(
            text = stringResource(R.string.name),
            modifier = Modifier.weight(1f),
            style = style,
        )
        BasicText(
            text = stringResource(R.string.allowable_q),
            modifier = Modifier.weight(1f),
            style = style.copy(textAlign = TextAlign.Center),
        )
        // packed_quantity with drawableEnd = ic_required
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicText(text = stringResource(R.string.packed_quantity), style = style)
            Image(
                painter = painterResource(R.drawable.ic_required),
                contentDescription = null,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun AddPackHeaderItemPreview() {
    AddPackHeaderItem()
}

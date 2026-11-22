package com.silkysys.warehouse.app.ui.compose_ui.pick

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.silkysys.warehouse.R


@Composable
fun PickStartedHeaderItem(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(5.dp) // layout_margin
            .itemsBorder()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HeaderCell(stringResource(R.string.header_id), weight = 0.7f, fontSize = 16.sp)
        HeaderCell(stringResource(R.string.header_order), weight = 1.1f)
        HeaderCell(stringResource(R.string.header_products), weight = 1.1f)
        HeaderCell(stringResource(R.string.status), weight = 1.1f)
    }
}

@Composable
private fun RowScope.HeaderCell(text: String, weight: Float, fontSize: TextUnit = 14.sp) {
    BasicText(
        text = text,
        modifier = Modifier.weight(weight),
        style = TextStyle(
            color = colorResource(R.color.colorAccent),
            fontSize = fontSize,
            fontFamily = PickBoldMediumFont,
            textAlign = TextAlign.Center,
        ),
    )
}

@Preview(showBackground = true, name = "Started header")
@Composable
private fun PickStartedHeaderItemPreview() {
    PickStartedHeaderItem()
}

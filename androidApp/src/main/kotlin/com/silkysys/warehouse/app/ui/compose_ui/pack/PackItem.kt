package com.silkysys.warehouse.app.ui.compose_ui.pack

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.silkysys.warehouse.R
import com.silkysys.warehouse.sharedcode.domain.packing.packList.entity.PackListEntity
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val BoldMediumFont = FontFamily(Font(R.font.bold_medium))
private val RegularFont = FontFamily(Font(R.font.regular))

/**
 * Compose replacement for `pack_item.xml` + the bind code of PackAdapter.
 * Left column: document name, order id (cut to 20 chars like `android:maxLength="20"`), branch.
 * Right column: "Status" label, status chip, created date.
 */
@Composable
fun PackItem(
    item: PackListEntity.Fulfillments,
    onClick: (PackListEntity.Fulfillments) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(5.dp)
    val date = LocalDate.parse(item.created_at, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toString()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp) // CardView layout_marginTop
            // @drawable/items_border: white, 5dp corners, 1dp #F0F0F0 stroke
            .background(Color.White, shape)
            .border(1.dp, Color(0xFFF0F0F0), shape)
            .clip(shape)
            .clickable(role = Role.Button) { onClick(item) },
    ) {
        Column(modifier = Modifier.weight(1f).padding(8.dp)) {
            // tv_docName (title_style)
            BasicText(
                text = item.document.document_name,
                style = TextStyle(
                    color = colorResource(R.color.colorBlack),
                    fontSize = 16.sp,
                    fontFamily = BoldMediumFont,
                ),
            )
            // tv_orderId (sub_title_style)
            BasicText(
                text = item.name.take(20),
                modifier = Modifier.padding(top = 8.dp),
                style = TextStyle(
                    color = colorResource(R.color.colorBlack),
                    fontSize = 14.sp,
                    fontFamily = RegularFont,
                ),
            )
            // tv_branchName (drawableStart = branch_icon, drawablePadding 4dp)
            Row(
                modifier = Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(R.drawable.branch_icon),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(Modifier.width(4.dp))
                BasicText(
                    text = item.warehouse.name,
                    style = TextStyle(
                        color = colorResource(R.color.colorPrimary),
                        fontSize = 14.sp,
                    ),
                )
            }
        }

        Column(modifier = Modifier.weight(1f).padding(8.dp)) {
            BasicText(
                text = stringResource(R.string.status),
                style = TextStyle(
                    color = colorResource(R.color.colorBlack),
                    fontSize = 16.sp,
                    fontFamily = BoldMediumFont,
                ),
            )
            // tv_packStatus: @drawable/status_bg = #00B012 at 10% alpha, pill shaped
            BasicText(
                text = item.status,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .background(colorResource(R.color.textGreen).copy(alpha = 0.1f), RoundedCornerShape(50))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                style = TextStyle(
                    color = colorResource(R.color.textGreen),
                    fontSize = 14.sp,
                    fontFamily = RegularFont,
                ),
            )
            // tv_packDate (sub_title_gray_style)
            BasicText(
                text = date,
                modifier = Modifier.padding(top = 4.dp),
                style = TextStyle(
                    color = colorResource(R.color.textColorDarker),
                    fontSize = 14.sp,
                    fontFamily = RegularFont,
                ),
            )
        }
    }
}

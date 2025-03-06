package amat.laundry.ui.component

import amat.laundry.R
import amat.laundry.ui.theme.Blue
import amat.laundry.ui.theme.FontBlack
import amat.laundry.ui.theme.GreyLight
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProductCartItem(
    modifier: Modifier = Modifier,
    productId: String,
    productName: String,
    productPrice: String,
    categoryName: String,
    qty: Float,
    unit: String,
    note: String,
    onClickDelete: (String) -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(start = 8.dp, top = 8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = productName[0].uppercase() + productName.drop(1),
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = FontBlack,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(end = 8.dp)
                )
                BoxRectangle(title = categoryName[0].uppercase() + categoryName.drop(1))
            }
        }

        Row(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = productPrice,
                style = TextStyle(
                    fontSize = 16.sp,
                    color = FontBlack,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(end = 8.dp)
            )

            if (qty > 0) {
                BoxRectangle(title = "$qty $unit", backgroundColor = Blue)
            }
        }

        if (qty > 0) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconBooking(
                    id = R.drawable.baseline_close_24,
                    modifier = Modifier
                        .clickable { onClickDelete(productId) }
                        .align(Alignment.Bottom)
                )
            }
        }

        if (qty > 0 && note != "") {
            Text(
                text = note,
                style = TextStyle(
                    fontSize = 16.sp,
                    color = FontBlack,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(end = 8.dp)
            )
        }

        Divider(
            color = GreyLight,
            thickness = 1.dp
        )

    }
}
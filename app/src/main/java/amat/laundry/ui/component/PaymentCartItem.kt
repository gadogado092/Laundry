package amat.laundry.ui.component

import amat.laundry.R
import amat.laundry.ui.theme.Blue
import amat.laundry.ui.theme.FontBlack
import amat.laundry.ui.theme.GreyLight
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PaymentCartItem(
    modifier: Modifier = Modifier,
    productId: String,
    productName: String,
    productPrice: String,
    productTotalPrice: String,
    categoryName: String,
    qty: Float,
    unit: String,
    note: String
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
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(end = 8.dp)
                )
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
                text = "$qty $unit X $productPrice",
                style = TextStyle(
                    fontSize = 18.sp,
                    color = FontBlack,
                ),
                modifier = Modifier.padding(end = 8.dp)
            )

            if (qty > 0) {
                Text(
                    text = productTotalPrice,
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = FontBlack,
                    )
                )
            }
        }

        if (qty > 0 && note != "") {
            Text(
                text = note,
                style = TextStyle(
                    fontSize = 14.sp,
                    color = FontBlack,
                ),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        Divider(
            modifier = Modifier.padding(top = 8.dp),
            color = GreyLight,
            thickness = 1.dp
        )

    }
}
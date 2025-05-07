package amat.laundry.ui.component

import amat.laundry.ui.theme.Blue
import amat.laundry.ui.theme.ColorPayment
import amat.laundry.ui.theme.FontBlack
import amat.laundry.ui.theme.FontBlue
import amat.laundry.ui.theme.GreyLight
import amat.laundry.ui.theme.TealGreen
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DryCleaning
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TransactionItem(
    modifier: Modifier = Modifier,
    invoiceCode: String,
    price: String,
    createAt: String,
    customerName: String,
    cashierName: String,
    note: String,
    isFullPayment: Boolean
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Spacer(Modifier.height(4.dp))
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = invoiceCode,
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = FontBlue,
                    ),
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = createAt,
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = FontBlack,
                    ),
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = customerName,
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = FontBlack,
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = price,
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = FontBlack,
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    if (note.isNotEmpty()) {
                        Text(
                            text = note,
                            style = TextStyle(
                                fontSize = 16.sp,
                                color = FontBlack,
                            ),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
                BoxRectangle(
                    title = if (isFullPayment) "Lunas " else "Belum Lunas ",
                    backgroundColor = if (isFullPayment) Blue else ColorPayment
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 4.dp),
                    imageVector = Icons.Default.SupportAgent,
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(color = TealGreen)
                )
                Text(
                    text = cashierName,
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = FontBlack,
                    ),
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            Row (verticalAlignment = Alignment.Bottom){
                Image(
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 4.dp),
                    imageVector = Icons.Default.DryCleaning,
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(color = TealGreen)
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Divider(
            color = GreyLight,
            thickness = 1.dp
        )
    }
}
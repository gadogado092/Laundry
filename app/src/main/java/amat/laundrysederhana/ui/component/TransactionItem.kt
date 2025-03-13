package amat.laundrysederhana.ui.component

import amat.laundrysederhana.ui.theme.Blue
import amat.laundrysederhana.ui.theme.ColorPayment
import amat.laundrysederhana.ui.theme.FontBlack
import amat.laundrysederhana.ui.theme.FontBlue
import amat.laundrysederhana.ui.theme.GreyLight
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                    modifier = Modifier.padding(end = 8.dp)
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
        }
        Spacer(Modifier.height(4.dp))
        Divider(
            color = GreyLight,
            thickness = 1.dp
        )
    }
}
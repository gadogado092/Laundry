package amat.laundrysederhana.ui.component

import amat.laundrysederhana.ui.theme.Blue
import amat.laundrysederhana.ui.theme.FontBlack
import amat.laundrysederhana.ui.theme.GreyLight
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CashFlowItem(
    modifier: Modifier = Modifier,
    categoryId: String,
    categoryName: String,
    nominal: String,
    createAt: String,
    qty: String,
    unit: String,
    note: String
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Spacer(Modifier.height(8.dp))
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    if (categoryId == "0") {
                        BoxRectangle(
                            title = categoryName[0].uppercase() + categoryName.drop(1),
                            backgroundColor = Color.DarkGray,
                            fontSize = 14.sp
                        )
                    } else {
                        BoxRectangle(
                            title = categoryName[0].uppercase() + categoryName.drop(1),
                            fontSize = 14.sp
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                    BoxRectangle(
                        title = "$qty $unit",
                        backgroundColor = Blue,
                        fontSize = 14.sp
                    )
                }
                Text(
                    text = createAt,
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = FontBlack,
                    ),
                )
            }

            Spacer(Modifier.height(2.dp))

            Text(
                text = nominal,
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


        Divider(
            modifier = Modifier.padding(top = 4.dp),
            color = GreyLight,
            thickness = 1.dp
        )
    }
}

fun generateTextType(typePayment: Int): String {

    if (typePayment == 0) {
        return "Transfer"
    } else if (typePayment == 1) {
        return "Cash"
    }

    return ""
}
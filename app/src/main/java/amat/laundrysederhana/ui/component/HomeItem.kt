package amat.laundrysederhana.ui.component

import amat.laundrysederhana.ui.theme.BGCashFlow
import amat.laundrysederhana.ui.theme.Blue
import amat.laundrysederhana.ui.theme.FontWhite
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeItem(
    modifier: Modifier = Modifier,
    categoryName: String,
    categoryUnit: String,
    totalQty: String,
    totalPrice: String
) {
    Card(
        modifier = modifier,
        backgroundColor = Blue
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = categoryName,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 20.sp,
                    color = FontWhite
                )
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    "$totalQty $categoryUnit", style = TextStyle(
                        fontSize = 16.sp,
                        color = FontWhite
                    )
                )
                Text(
                    totalPrice, style = TextStyle(
                        fontSize = 16.sp,
                        color = FontWhite
                    )
                )
            }
        }
    }
}

@Composable
fun HomeItemSmall(
    modifier: Modifier = Modifier,
    categoryName: String,
    categoryUnit: String,
    totalQty: String,
    totalPrice: String
) {
    Card(
        modifier = modifier,
        backgroundColor = BGCashFlow
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Spacer(Modifier.height(4.dp))
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = categoryName,
                style = TextStyle(
                    fontSize = 16.sp,
                    color = FontWhite
                )
            )
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    "$totalQty $categoryUnit", style = TextStyle(
                        fontSize = 16.sp,
                        color = FontWhite
                    )
                )
                Text(
                    totalPrice, style = TextStyle(
                        fontSize = 16.sp,
                        color = FontWhite
                    )
                )
            }
        }
    }
}
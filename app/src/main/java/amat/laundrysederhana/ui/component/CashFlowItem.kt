package amat.laundrysederhana.ui.component

import amat.laundrysederhana.currencyFormatterStringViewZero
import amat.laundrysederhana.dateToDisplayMidFormat
import amat.laundrysederhana.ui.theme.ColorIncome
import amat.laundrysederhana.ui.theme.ColorRed
import amat.laundrysederhana.ui.theme.FontBlack
import amat.laundrysederhana.ui.theme.GreyLight
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CashFlowItem(
    modifier: Modifier = Modifier,
    nominal: String,
    typePayment: Int,
    createAt: String,
    type: Int,
    note: String
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 4.dp)
    ) {
        Row {
            Image(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(32.dp),
                imageVector = if (type == 0) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                contentDescription = "",
                colorFilter = ColorFilter.tint(if (type == 0) ColorIncome else ColorRed)
            )
            Column(modifier = Modifier.padding(end = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = currencyFormatterStringViewZero(nominal),
                        style = TextStyle(
                            fontWeight = FontWeight.Medium,
                            color = if (type == 0) ColorIncome else ColorRed,
                            fontSize = 14.sp
                        )
                    )
                    Text(
                        text = dateToDisplayMidFormat(createAt),
                        style = TextStyle(
                            fontWeight = FontWeight.Medium,
                            color = FontBlack,
                            fontSize = 14.sp
                        )
                    )
                }
                Text(text = note, textAlign = TextAlign.Justify, color = FontBlack)
                Text(text = generateTextType(typePayment), color = FontBlack)
            }
        }
        Divider(
            modifier = Modifier.padding(top = 2.dp),
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
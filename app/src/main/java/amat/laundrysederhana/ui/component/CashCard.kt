package amat.laundrysederhana.ui.component

import amat.laundrysederhana.ui.theme.ColorIncome
import amat.laundrysederhana.ui.theme.ColorRed
import amat.laundrysederhana.ui.theme.FontBlack
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp

@Composable
fun CashCard(
    type: Int,
    nominal: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = 4.dp
    ) {
        Row {
            Image(
                modifier = Modifier.padding(4.dp),
                imageVector = if (type == 0) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                contentDescription = "",
                colorFilter = ColorFilter.tint(if (type == 0) ColorIncome else ColorRed)
            )
            Text(
                    text = nominal,
            color = FontBlack,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(
                    end = 8.dp
                ),
            )
        }
    }
}
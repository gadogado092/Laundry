package amat.laundry.ui.component

import amat.laundry.ui.theme.FontBlackSoft
import amat.laundry.ui.theme.GreyLight
import amat.laundry.ui.theme.GreyLight3
import amat.laundry.ui.theme.LaundryAppTheme
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FilterItem(
    title: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        border = if (isSelected) BorderStroke(0.4.dp, GreyLight3) else BorderStroke(
            0.4.dp,
            GreyLight
        ),
        backgroundColor = if (isSelected) GreyLight3 else GreyLight,
        contentColor = if (isSelected) GreyLight3 else GreyLight,
        content = {
            Text(
                text = title,
                modifier = Modifier
                    .background(if (isSelected) GreyLight3 else GreyLight)
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                style = TextStyle(
                    color = if (isSelected) Color.White else FontBlackSoft,
                ),
                fontSize = 18.sp
            )
        }
    )
}

@Composable
@Preview(showBackground = true)
fun FilterItemPreview() {
    LaundryAppTheme {
        Row {
            FilterItem(
                "Semua",
                isSelected = true,
            )
            FilterItem(
                "Musik",
                isSelected = false
            )
            FilterItem(
                "Berita",
                isSelected = false
            )
        }
    }
}
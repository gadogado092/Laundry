package amat.laundrysederhana.ui.component

import amat.laundrysederhana.ui.theme.TealGreen
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BoxPrice(
    modifier: Modifier = Modifier,
    title: String,
    fontSize: TextUnit = 12.sp
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        backgroundColor = TealGreen,
        contentColor = TealGreen,
        content = {
            Text(
                text = title,
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                style = TextStyle(
                    color = Color.White,
                ),
                fontSize = fontSize
            )
        }
    )
}
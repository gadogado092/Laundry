package amat.kelolakost.ui.component

import amat.kelolakost.ui.theme.LightGreen
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BoxRectangle(
    title: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        backgroundColor = LightGreen,
        contentColor = LightGreen,
        content = {
            Text(
                text = title,
                modifier = Modifier
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                style = TextStyle(
                    color = Color.White,
                ),
                fontSize = 12.sp
            )
        }
    )
}
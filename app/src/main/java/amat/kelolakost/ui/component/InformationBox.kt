package amat.kelolakost.ui.component

import amat.kelolakost.ui.theme.FontBlack
import amat.kelolakost.ui.theme.LightGreen
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
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
fun InformationBox(
    modifier: Modifier = Modifier,
    value: String,
    fontSize: TextUnit = 14.sp,
    borderColor: Color = LightGreen
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, borderColor),
        content = {
            Text(
                text = value,
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                style = TextStyle(
                    color = FontBlack,
                ),
                fontSize = fontSize
            )
        }
    )
}

@Composable
fun InformationBox(
    modifier: Modifier = Modifier,
    borderColor: Color = LightGreen,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, borderColor),
        content = content
    )
}
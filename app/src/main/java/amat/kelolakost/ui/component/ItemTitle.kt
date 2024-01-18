package amat.kelolakost.ui.component

import amat.kelolakost.ui.theme.FontBlack
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ItemTitle(text: String) {
    Text(
        text = text[0].uppercase() + text.drop(1),
        style = TextStyle(
            fontSize = 18.sp,
            color = FontBlack,
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.padding(end = 8.dp)
    )
}
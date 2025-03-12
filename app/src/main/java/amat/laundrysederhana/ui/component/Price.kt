package amat.laundrysederhana.ui.component

import amat.laundrysederhana.ui.theme.FontBlack
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Price(
    value: String,
    tint: Color = Color.Gray
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Adjust,
            contentDescription = "",
            Modifier
                .padding(end = 4.dp)
                .size(16.dp),
            tint = tint
        )
        Text(
            text = value, style = TextStyle(fontSize = 14.sp),
            color = FontBlack
        )
    }
}
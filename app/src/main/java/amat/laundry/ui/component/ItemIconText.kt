package amat.laundry.ui.component

import amat.laundry.ui.theme.FontBlack
import amat.laundry.ui.theme.TealGreen
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ItemIconText(
    imageVector: ImageVector,
    title: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Image(
            modifier = Modifier
                .size(24.dp)
                .padding(end = 4.dp),
            imageVector = imageVector,
            contentDescription = "",
            colorFilter = ColorFilter.tint(color = TealGreen)
        )
        Text(
            text = title, style = TextStyle(fontSize = 16.sp, color = FontBlack), maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
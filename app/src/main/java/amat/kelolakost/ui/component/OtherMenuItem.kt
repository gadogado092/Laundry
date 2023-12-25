package amat.kelolakost.ui.component

import amat.kelolakost.ui.theme.TealGreen
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OtherMenuItem(
    @DrawableRes id: Int,
    title: String,
    subTitle: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.padding(8.dp)) {
        Image(
            modifier = Modifier
                .size(40.dp)
                .padding(8.dp),
            painter = painterResource(id = id),
            contentDescription = "",
        )
        Column(
            modifier = Modifier
                .weight(1F)
                .padding(start = 8.dp)
        ) {
            Text(text = title, style = TextStyle(fontSize = 14.sp))
            Text(
                text = subTitle,
                style = TextStyle(fontSize = 14.sp),
                color = Color.Gray
            )
        }
    }
}

@Composable
fun OtherMenuItem(
    imageVector: ImageVector,
    title: String,
    subTitle: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.padding(8.dp)) {
        Image(
            modifier = Modifier
                .size(40.dp)
                .padding(8.dp),
            imageVector = imageVector,
            contentDescription = "",
            colorFilter = ColorFilter.tint(color = TealGreen)
        )
        Column(
            modifier = Modifier
                .weight(1F)
                .padding(start = 8.dp)
        ) {
            Text(text = title, style = TextStyle(fontSize = 14.sp))
            Text(
                text = subTitle,
                style = TextStyle(fontSize = 14.sp),
                color = Color.Gray
            )
        }
    }
}
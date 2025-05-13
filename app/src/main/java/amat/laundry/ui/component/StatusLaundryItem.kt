package amat.laundry.ui.component

import amat.laundry.ui.theme.FontBlack
import amat.laundry.ui.theme.FontGrey
import amat.laundry.ui.theme.TealGreen
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StatusLaundryItem(
    imageVector: ImageVector,
    title: String,
    statusIcon: Int = 1,
    modifier: Modifier = Modifier
) {
    //statusIcon 1=now, 2= ready click, 3= not ready click
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(32.dp),
            imageVector = imageVector,
            contentDescription = "",
            colorFilter = ColorFilter.tint(
                color = if (statusIcon == 3) {
                    FontGrey
                } else if (statusIcon == 2) {
                    FontBlack
                } else {
                    TealGreen
                }
            )
        )
        Text(
            text = title,
            style = TextStyle(
                fontSize = 16.sp,
                color = if (statusIcon == 3) {
                    FontGrey
                } else if (statusIcon == 2) {
                    FontBlack
                } else {
                    TealGreen
                },
            ),
        )
    }
}
package amat.kelolakost.ui.component

import amat.kelolakost.ui.theme.FontBlack
import amat.kelolakost.ui.theme.GreenDark
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DateLayout(
    modifier: Modifier = Modifier,
    title: String = "",
    value: String,
    contentHorizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    isEnable: Boolean = false
) {
    Column(modifier = modifier) {
        if (title.isNotEmpty()) {
            Text(
                modifier = Modifier.padding(bottom = 2.dp),
                text = title,
                style = TextStyle(color = FontBlack),
                fontSize = 16.sp
            )
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(4.dp),
            border = if (isEnable) BorderStroke(1.dp, GreenDark) else BorderStroke(
                1.dp,
                Color.Gray
            ),
            content = {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = contentHorizontalArrangement
                ) {
                    Image(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "",
                        colorFilter = if (isEnable) ColorFilter.tint(GreenDark) else ColorFilter.tint(
                            Color.Gray
                        )
                    )
                    Text(
                        text = value,
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        style = TextStyle(
                            color = FontBlack,
                        )
                    )
                }
            }
        )
    }
}
package amat.kelolakost.ui.component

import amat.kelolakost.ui.theme.FontBlack
import amat.kelolakost.ui.theme.GreyLight
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun KostItem(
    name: String,
    address: String,
    note: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
            Text(
                text = name[0].uppercase() + name.drop(1), style = TextStyle(fontSize = 18.sp),
                color = FontBlack
            )
            Text(
                text = address, style = TextStyle(fontSize = 14.sp),
                color = FontBlack
            )
            Text(
                text = note, style = TextStyle(fontSize = 14.sp),
                color = FontBlack
            )
        }
        Divider(
            color = GreyLight,
            thickness = 2.dp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
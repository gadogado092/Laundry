package amat.laundrysederhana.ui.component

import amat.laundrysederhana.ui.theme.FontBlack
import amat.laundrysederhana.ui.theme.FontGrey
import amat.laundrysederhana.ui.theme.GreyLight
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CategoryItem(
    modifier: Modifier = Modifier,
    name: String,
    unit: String
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(start = 8.dp, top = 8.dp)) {
            Text(
                "Nama Kategori", style = TextStyle(
                    fontSize = 14.sp,
                    color = FontGrey,
                )
            )
            Text(
                text = name[0].uppercase() + name.drop(1),
                style = TextStyle(
                    fontSize = 18.sp,
                    color = FontBlack,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                "Nama Satuan", style = TextStyle(
                    fontSize = 14.sp,
                    color = FontGrey,
                )
            )
            Text(
                text = unit[0].uppercase() + unit.drop(1),
                style = TextStyle(
                    fontSize = 18.sp,
                    color = FontBlack,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
        Divider(
            color = GreyLight,
            thickness = 2.dp
        )
    }
}
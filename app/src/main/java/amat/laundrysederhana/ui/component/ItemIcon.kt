package amat.laundrysederhana.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun ItemIcon(@DrawableRes id: Int, modifier: Modifier = Modifier) {
    Image(
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .size(20.dp),
        painter = painterResource(id = id),
        contentDescription = "",
    )
}
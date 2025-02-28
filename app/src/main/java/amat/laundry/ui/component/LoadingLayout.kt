package amat.laundry.ui.component

import amat.laundry.R
import amat.laundry.ui.theme.FontBlack
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun LoadingLayout(
    modifier: Modifier = Modifier,
    text: String = stringResource(id = R.string.please_wait)
) {
    CenterLayout(modifier = modifier,
        content = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(50.dp)
                )
                Text(text = text, color = FontBlack)
            }
        }
    )
}
package amat.kelolakost.ui.component

import amat.kelolakost.R
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
fun LoadingLayout(modifier: Modifier = Modifier) {
    CenterLayout(modifier = modifier,
        content = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(50.dp)
                )
                Text(text = stringResource(id = R.string.please_wait))
            }
        }
    )
}
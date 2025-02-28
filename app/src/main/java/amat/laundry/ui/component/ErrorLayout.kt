package amat.laundry.ui.component

import amat.laundry.R
import amat.laundry.ui.theme.FontBlack
import amat.laundry.ui.theme.TealGreen
import androidx.compose.foundation.layout.Column
import androidx.compose.material.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

@Composable
fun ErrorLayout(modifier: Modifier = Modifier, errorMessage: String, onTryAgainClick: () -> Unit) {
    CenterLayout(
        modifier = modifier,
        content = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = errorMessage, color= FontBlack)
                OutlinedButton(onClick = onTryAgainClick) {
                    Text(stringResource(id = R.string.try_again), color = TealGreen)
                }
            }
        }
    )
}
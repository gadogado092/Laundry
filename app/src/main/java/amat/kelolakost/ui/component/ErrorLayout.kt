package amat.kelolakost.ui.component

import amat.kelolakost.R
import amat.kelolakost.ui.theme.TealGreen
import androidx.compose.foundation.layout.Column
import androidx.compose.material.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource

@Composable
fun ErrorLayout(errorMessage: String, onTryAgainClick: () -> Unit) {
    CenterLayout(
        content = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = errorMessage)
                OutlinedButton(onClick = onTryAgainClick) {
                    Text(stringResource(id = R.string.try_again), color = TealGreen)
                }
            }
        }
    )
}
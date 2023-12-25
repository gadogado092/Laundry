package amat.kelolakost.ui.screen.other

import amat.kelolakost.R
import amat.kelolakost.ui.component.OtherMenuItem
import amat.kelolakost.ui.theme.GreyLight
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.BookOnline
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OtherScreen(
    context: Context,
    modifier: Modifier = Modifier,
    onClickExtend: () -> Unit,
    navigateToBooking: () -> Unit,
    navigateToTenant: () -> Unit,
    onClickTutorial: () -> Unit,
    onClickCostumerService: () -> Unit,
) {
    Column {
        Divider(
            color = GreyLight,
            thickness = 8.dp,
        )
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1F)
            ) {
                Text(
                    text = stringResource(id = R.string.limit_extend),
                    style = TextStyle(fontSize = 12.sp),
                    color = Color.Gray
                )
                Text(text = "20 Oktober 1992")
                Text(text = "25.000/Bulan")
            }
            OutlinedButton(onClick = onClickExtend) {
                Text(text = stringResource(id = R.string.extend))
            }
        }
        Divider(
            color = GreyLight,
            thickness = 8.dp,
        )
        OtherMenuItem(
            Icons.Default.BookOnline,
            stringResource(id = R.string.title_booking),
            stringResource(id = R.string.subtitle_booking),
            modifier = Modifier
                .clickable {
                    navigateToBooking()
                },
        )
        OtherMenuItem(
            Icons.Default.ListAlt,
            stringResource(id = R.string.title_debt_tenant),
            stringResource(id = R.string.subtitle_debt_tenant),
            modifier = Modifier
                .clickable {
                    navigateToTenant()
                },
        )
        OtherMenuItem(
            Icons.Default.House,
            stringResource(id = R.string.title_kost),
            stringResource(id = R.string.subtitle_other_kost),
            modifier = Modifier
                .clickable {
                    navigateToTenant()
                },
        )
        OtherMenuItem(
            Icons.Default.Bed,
            stringResource(id = R.string.title_type_unit),
            stringResource(id = R.string.subtitle_type_unit),
            modifier = Modifier
                .clickable {
                    navigateToTenant()
                },
        )
        OtherMenuItem(
            R.drawable.baseline_play_circle_filled_24,
            stringResource(id = R.string.title_tutorial),
            stringResource(id = R.string.subtitle_tutorial),
            modifier = Modifier
                .clickable {
                    onClickTutorial()
                },
        )
        OtherMenuItem(
            R.drawable.ic_wa,
            stringResource(id = R.string.title_customer_service),
            stringResource(id = R.string.subtitle_customer_service),
            modifier = Modifier
                .clickable {
                    onClickCostumerService()
                },
        )
    }

}
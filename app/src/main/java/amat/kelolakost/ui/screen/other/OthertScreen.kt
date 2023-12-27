package amat.kelolakost.ui.screen.other

import amat.kelolakost.R
import amat.kelolakost.currencyFormatterStringViewZero
import amat.kelolakost.dateToDisplayMidFormat
import amat.kelolakost.di.Injection
import amat.kelolakost.ui.common.OnLifecycleEvent
import amat.kelolakost.ui.common.UiState
import amat.kelolakost.ui.component.OtherMenuItem
import amat.kelolakost.ui.theme.GreyLight
import amat.kelolakost.ui.theme.TealGreen
import android.content.Context
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.BookOnline
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun OtherScreen(
    context: Context,
    modifier: Modifier = Modifier,
    onClickExtend: () -> Unit,
    navigateToBooking: () -> Unit,
    navigateToTenant: () -> Unit,
    navigateToKost: () -> Unit,
    navigateToUnitType: () -> Unit,
    navigateToProfile: () -> Unit,
    onClickTutorial: () -> Unit,
    onClickCostumerService: (String) -> Unit,
) {
    val viewModel: OtherViewModel =
        viewModel(factory = OtherViewModelFactory(Injection.provideUserRepository(context)))

    OnLifecycleEvent { owner, event ->
        // do stuff on event
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                viewModel.getKostInit()
            }

            else -> {}
        }

    }


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
                ContentPrice(viewModel)
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
                    navigateToKost()
                },
        )
        OtherMenuItem(
            Icons.Default.Bed,
            stringResource(id = R.string.title_type_unit),
            stringResource(id = R.string.subtitle_type_unit),
            modifier = Modifier
                .clickable {
                    navigateToUnitType()
                },
        )
        OtherMenuItem(
            Icons.Default.AccountCircle,
            stringResource(id = R.string.title_profile),
            stringResource(id = R.string.subtitle_profile),
            modifier = Modifier
                .clickable {
                    navigateToProfile()
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
                    onClickCostumerService(viewModel.typeWa.value)
                },
        )
    }

}

@Composable
fun ContentPrice(viewModel: OtherViewModel) {
    viewModel.stateUser.collectAsState(initial = UiState.Loading).value.let { uiState ->
        when (uiState) {
            is UiState.Error -> {
                Text(text = uiState.errorMessage, style = TextStyle(fontSize = 12.sp))
                Text(text = uiState.errorMessage, color = TealGreen)
            }

            UiState.Loading -> {
                Text(text = "Loading", style = TextStyle(fontSize = 12.sp))
                Text(text = "Loading", color = TealGreen)
            }

            is UiState.Success -> {
                val decodedDateTime = Uri.decode(uiState.data.limit)
                Text(
                    text = dateToDisplayMidFormat(decodedDateTime),
                    style = TextStyle(fontSize = 12.sp),
                )
                Text(
                    text = currencyFormatterStringViewZero(uiState.data.cost.toString()) + "/Bulan",
                    color = TealGreen
                )
            }
        }
    }
}


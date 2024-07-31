package amat.kelolakost.ui.screen.other

import amat.kelolakost.AccountBackupPreference
import amat.kelolakost.R
import amat.kelolakost.currencyFormatterStringViewZero
import amat.kelolakost.dateToDisplayMidFormat
import amat.kelolakost.di.Injection
import amat.kelolakost.ui.common.OnLifecycleEvent
import amat.kelolakost.ui.common.UiState
import amat.kelolakost.ui.component.OtherMenuItem
import amat.kelolakost.ui.component.SimpleQuantityTextField
import amat.kelolakost.ui.screen.back_up.BackUpActivity
import amat.kelolakost.ui.screen.back_up.LoginActivity
import amat.kelolakost.ui.theme.ErrorColor
import amat.kelolakost.ui.theme.GreyLight
import amat.kelolakost.ui.theme.TealGreen
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.BookOnline
import androidx.compose.material.icons.filled.CloudCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Reorder
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun OtherScreen(
    context: Context,
    modifier: Modifier = Modifier,
    onClickCsExtend: (String) -> Unit,
    navigateToBooking: () -> Unit,
    navigateCreditTenant: () -> Unit,
    navigateToKost: () -> Unit,
    navigateToUnitType: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToDebtCredit: () -> Unit,
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

    if (!viewModel.isProsesSuccess.collectAsState().value.isError) {
        Toast.makeText(context, "Perpanjang Berhasil Dilakukan", Toast.LENGTH_SHORT)
            .show()
        viewModel.getKostInit()
    } else {
        if (viewModel.isProsesSuccess.collectAsState().value.errorMessage.isNotEmpty()) {
            Toast.makeText(
                context,
                viewModel.isProsesSuccess.collectAsState().value.errorMessage,
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1F)) {
                Text(
                    text = stringResource(id = R.string.limit_extend),
                    style = TextStyle(fontSize = 12.sp),
                    color = Color.Gray
                )
                ContentLimitDate(viewModel)
            }
            Column(modifier = Modifier.weight(1F), horizontalAlignment = Alignment.End) {
                ContentPrice(viewModel)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
        ) {
            SimpleQuantityTextField(
                modifier = Modifier.weight(3.5F),
                value = viewModel.stateUi.collectAsState().value.qty.toString(),
                onAddClick = {
                    viewModel.addQuantity()
                },
                onMinClick = {
                    viewModel.minQuantity()
                }
            )
            Spacer(modifier = Modifier.weight(0.3F))
            OutlinedTextField(
                modifier = Modifier.weight(6F),
                value = viewModel.stateUi.collectAsState().value.extendPassword,
                onValueChange = { newText ->
                    viewModel.setPassword(newText)
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = TealGreen,
                    errorBorderColor = ErrorColor
                ),
                placeholder = { Text("Password Perpanjang") }
            )
        }
        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            style = TextStyle(fontSize = 12.sp),
            text = "Perpanjang pemakaian aplikasi ${viewModel.stateUi.collectAsState().value.qty} Bulan. " +
                    "Biaya ${currencyFormatterStringViewZero((viewModel.stateUi.collectAsState().value.qty * viewModel.stateUi.collectAsState().value.cost).toString())}" +
                    "\nBatas Pemakaian setelah perpanjang ${dateToDisplayMidFormat(viewModel.stateUi.collectAsState().value.newLimit)}"
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            text = "Support Kami Melalui",
            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium)
        )
        ItemSupport(
            subtitle = context.getString(R.string.bank),
            modifier = Modifier.clickable {
                copyTextToClipboard(
                    context,
                    "Rekening Bsi",
                    context.getString(R.string.rek_bsi)
                )
            })
        ItemSupport(
            subtitle = context.getString(R.string.ovo),
            modifier = Modifier.clickable {
                copyTextToClipboard(
                    context,
                    "Nomor Ovo",
                    context.getString(R.string.number_ovo)
                )
            })
        ItemSupport(
            subtitle = context.getString(R.string.go_pay),
            modifier = Modifier.clickable {
                copyTextToClipboard(
                    context,
                    "Nomor Gopay",
                    context.getString(R.string.number_gopay)
                )
            })

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            OutlinedButton(
                modifier = Modifier.weight(1F),
                onClick = {
                    val note =
                        "Assalamualaikum... Selamat pagi, siang, sore atau malam..." +
                                "\nSaya Ingin Perpanjang pemakaian aplikasi ${viewModel.stateUi.value.qty} Bulan. " +
                                "Biaya ${currencyFormatterStringViewZero((viewModel.stateUi.value.qty * viewModel.stateUi.value.cost).toString())}" +
                                "\nBatas Pemakaian setelah perpanjang ${
                                    dateToDisplayMidFormat(
                                        viewModel.stateUi.value.newLimit
                                    )
                                }" +
                                "\nkode = ${viewModel.stateUi.value.kode}"
                    onClickCsExtend(note)
                }) {
                Text(text = stringResource(id = R.string.cs_extend))
            }
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedButton(
                modifier = Modifier.weight(1F),
                onClick = {
                    viewModel.proses()
                }
            ) {
                Text(text = stringResource(id = R.string.extend))
            }
        }
        Divider(
            color = GreyLight,
            thickness = 8.dp,
        )
        OtherMenuItem(
            Icons.Default.CloudCircle,
            "Backup & Restore Online",
            "Backup data Kost secara berkala",
            modifier = Modifier
                .clickable {
                    val accountBackupPreference = AccountBackupPreference(context).getAccount()
                    if (accountBackupPreference.isLogin){
                        val activity = (context as? Activity)
                        val intent = Intent(context, BackUpActivity::class.java)
                        context.startActivity(intent)
                        activity?.finish()
                    }else{
                        val activity = (context as? Activity)
                        val intent = Intent(context, LoginActivity::class.java)
                        context.startActivity(intent)
                        activity?.finish()
                    }
                },
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
            stringResource(id = R.string.title_credit_tenant),
            stringResource(id = R.string.subtitle_credit_tenant),
            modifier = Modifier
                .clickable {
                    navigateCreditTenant()
                },
        )
        OtherMenuItem(
            Icons.Default.Reorder,
            stringResource(id = R.string.title_debt_credit),
            stringResource(id = R.string.subtitle_debt_credit_tenant),
            modifier = Modifier
                .clickable {
                    navigateToDebtCredit()
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
                Text(
                    text = uiState.errorMessage,
                    style = TextStyle(fontSize = 12.sp),
                    color = Color.Gray
                )
                Text(text = uiState.errorMessage, color = TealGreen)
            }

            UiState.Loading -> {
                Text(text = "Loading", style = TextStyle(fontSize = 12.sp), color = Color.Gray)
                Text(text = "Loading", color = TealGreen)
            }

            is UiState.Success -> {
                Text(
                    text = "Kode = ${uiState.data.key}",
                    style = TextStyle(fontSize = 12.sp),
                    color = Color.Gray
                )
                Text(
                    text = currencyFormatterStringViewZero(uiState.data.cost.toString()) + "/Bulan",
                    color = TealGreen
                )
            }
        }
    }
}

@Composable
fun ContentLimitDate(viewModel: OtherViewModel) {
    viewModel.stateUser.collectAsState(initial = UiState.Loading).value.let { uiState ->
        when (uiState) {
            is UiState.Error -> {
                Text(text = uiState.errorMessage)
            }

            UiState.Loading -> {
                Text(text = "Loading")
            }

            is UiState.Success -> {
                Text(
                    text = dateToDisplayMidFormat(uiState.data.limit),
                )
            }
        }
    }
}

@Composable
fun ItemSupport(
    modifier: Modifier = Modifier,
    subtitle: String = "",
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp), Arrangement.SpaceBetween, Alignment.CenterVertically
    ) {
        Text(text = subtitle, style = TextStyle(fontSize = 14.sp))
        Image(
            modifier = Modifier
                .padding(2.dp)
                .size(20.dp),
            imageVector = Icons.Default.ContentCopy,
            contentDescription = "",
            colorFilter = ColorFilter.tint(color = TealGreen)
        )
    }
}

private fun copyTextToClipboard(context: Context, info: String, value: String) {
    val clipboardManager =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText("text", value)
    clipboardManager.setPrimaryClip(clipData)
    Toast.makeText(context, "$info terCopy ke clipboard", Toast.LENGTH_LONG).show()
}



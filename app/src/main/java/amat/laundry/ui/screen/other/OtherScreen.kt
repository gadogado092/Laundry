package amat.laundry.ui.screen.other

import amat.laundry.R
import amat.laundry.currencyFormatterStringViewZero
import amat.laundry.dateToDisplayMidFormat
import amat.laundry.di.Injection
import amat.laundry.ui.common.OnLifecycleEvent
import amat.laundry.ui.common.UiState
import amat.laundry.ui.component.OtherMenuItem
import amat.laundry.ui.component.SimpleQuantityTextField
import amat.laundry.ui.theme.ErrorColor
import amat.laundry.ui.theme.FontBlack
import amat.laundry.ui.theme.GreyLight
import amat.laundry.ui.theme.TealGreen
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Wash
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
    modifier: Modifier = Modifier,
    context: Context,
    onClickCsExtend: (String) -> Unit,
    navigateToProfile: () -> Unit,
    navigateToPrinter: () -> Unit,
    navigateToProduct: () -> Unit,
    navigateToCategory: () -> Unit,
    navigateToCashFlow: () -> Unit,
    navigateToCashier: () -> Unit,
    navigateToCustomer: () -> Unit,
    navigateToCashFlowCategory: () -> Unit,
    onClickTutorial: () -> Unit,
    onClickCostumerService: (String) -> Unit
) {

    val viewModel: OtherViewModel =
        viewModel(factory = OtherViewModelFactory(Injection.provideUserRepository(context)))

    OnLifecycleEvent { owner, event ->
        // do stuff on event
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                viewModel.getUserInit()
            }

            else -> {}
        }

    }

    if (!viewModel.isProsesSuccess.collectAsState().value.isError) {
        Toast.makeText(context, "Perpanjang Berhasil Dilakukan", Toast.LENGTH_SHORT)
            .show()
        viewModel.getUserInit()
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

    //START UI
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
                placeholder = { Text("Password Perpanjang", color = FontBlack) }
            )
        }
        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            style = TextStyle(fontSize = 12.sp, color = FontBlack),
            text = "Perpanjang pemakaian aplikasi ${viewModel.stateUi.collectAsState().value.qty} Bulan. " +
                    "Biaya ${currencyFormatterStringViewZero((viewModel.stateUi.collectAsState().value.qty * viewModel.stateUi.collectAsState().value.cost).toString())}" +
                    "\nBatas Pemakaian setelah perpanjang ${dateToDisplayMidFormat(viewModel.stateUi.collectAsState().value.newLimit)}"
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            text = "Support Kami Melalui",
            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = FontBlack)
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
                Text(text = stringResource(id = R.string.cs_extend), color = FontBlack)
            }
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedButton(
                modifier = Modifier.weight(1F),
                onClick = {
                    viewModel.proses()
                }
            ) {
                Text(text = stringResource(id = R.string.extend), color = FontBlack)
            }
        }
        Divider(
            color = GreyLight,
            thickness = 8.dp,
        )

        OtherMenuItem(
            Icons.Default.SupportAgent,
            stringResource(id = R.string.title_customer),
            stringResource(id = R.string.subtitle_customer_2),
            modifier = Modifier
                .clickable {
                    navigateToCustomer()
                },
        )

        OtherMenuItem(
            Icons.Default.ManageAccounts,
            stringResource(id = R.string.title_cashier),
            stringResource(id = R.string.subtitle_cashier),
            modifier = Modifier
                .clickable {
                    navigateToCashier()
                },
        )

        OtherMenuItem(
            Icons.Default.Wash,
            stringResource(id = R.string.title_product),
            stringResource(id = R.string.subtitle_product),
            modifier = Modifier
                .clickable {
                    navigateToProduct()
                },
        )

        OtherMenuItem(
            Icons.Default.Category,
            stringResource(id = R.string.title_category),
            stringResource(id = R.string.subtitle_category),
            modifier = Modifier
                .clickable {
                    navigateToCategory()
                },
        )

        OtherMenuItem(
            Icons.Default.Print,
            stringResource(id = R.string.title_printer),
            stringResource(id = R.string.subtitle_printer),
            modifier = Modifier
                .clickable {
                    navigateToPrinter()
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

        Divider(
            color = GreyLight,
            thickness = 8.dp,
        )

        OtherMenuItem(
            Icons.Default.AccountBalanceWallet,
            stringResource(id = R.string.title_cash_flow),
            stringResource(id = R.string.subtitle_cash_flow),
            modifier = Modifier
                .clickable {
                    navigateToCashFlow()
                },
        )

        OtherMenuItem(
            Icons.Default.Category,
            stringResource(id = R.string.title_cash_flow_category),
            stringResource(id = R.string.subtitle_cash_flow_category),
            modifier = Modifier
                .clickable {
                    navigateToCashFlowCategory()
                },
        )

        Divider(
            color = GreyLight,
            thickness = 8.dp,
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
                Text(text = uiState.errorMessage, color = FontBlack)
            }

            UiState.Loading -> {
                Text(text = "Loading", color = FontBlack)
            }

            is UiState.Success -> {
                Text(
                    text = dateToDisplayMidFormat(uiState.data.limit), color = FontBlack
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
        Text(text = subtitle, style = TextStyle(fontSize = 14.sp), color = FontBlack)
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
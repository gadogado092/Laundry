package amat.laundry.ui.screen.transaction

import amat.laundry.R
import amat.laundry.currencyFormatterStringViewZero
import amat.laundry.data.TransactionCustomer
import amat.laundry.dateTimeUniversalToDateDisplay
import amat.laundry.dateTimeUniversalToDisplay
import amat.laundry.dateToDisplayMidFormat
import amat.laundry.di.Injection
import amat.laundry.sendWhatsApp
import amat.laundry.ui.common.OnLifecycleEvent
import amat.laundry.ui.common.UiState
import amat.laundry.ui.component.CenterLayout
import amat.laundry.ui.component.CustomSearchView
import amat.laundry.ui.component.LoadingLayout
import amat.laundry.ui.component.TransactionItem
import amat.laundry.ui.screen.bill.BillActivityNew
import amat.laundry.ui.theme.FontBlack
import amat.laundry.ui.theme.LaundryAppTheme
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

class SearchTransactionActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            LaundryAppTheme {
                SearchTransactionScreen(context)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, insets ->
            val bottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            view.updatePadding(bottom = bottom)
            insets
        }

    }
}

@Composable
fun SearchTransactionScreen(
    context: Context
) {

    val viewModel: SearchTransactionViewModel =
        viewModel(
            factory = SearchTransactionViewModelFactory(
                Injection.provideTransactionRepository(context),
                Injection.provideUserRepository(context),
            )
        )

    OnLifecycleEvent { owner, event ->
        // do stuff on event
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                viewModel.setSearch(viewModel.searchValue.value)
            }

            else -> { /* other stuff */
            }
        }
    }

    //START UI
    Column {
        CustomSearchView(
            placeHolderText = "Nomor atau Kode Invoice",
            search = viewModel.searchValue.collectAsState().value,
            onValueChange = {
                viewModel.setSearch(it)
            },
            onClickBack = {
                val activity = (context as? Activity)
                activity?.finish()
            })

        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            viewModel.stateTransaction.collectAsState(initial = UiState.Loading).value.let { uiState ->
                when (uiState) {
                    is UiState.Error -> {
                        CenterLayout(
                            content = {
                                Text(
                                    text = uiState.errorMessage,
                                    color = FontBlack
                                )
                            }
                        )
                    }

                    UiState.Loading -> {
                        LoadingLayout(modifier = Modifier.fillMaxHeight())
                    }

                    is UiState.Success -> {
                        ListSearchTransactionView(
                            uiState.data,
                            onItemClick = { id ->
                                val intent = Intent(context, BillActivityNew::class.java)
                                intent.putExtra("id", id)
                                context.startActivity(intent)
                            },
                            context,
                            viewModel
                        )
                    }

                }
            }

        }

    }

}

@Composable
fun ListSearchTransactionView(
    listData: List<TransactionCustomer>,
    onItemClick: (String) -> Unit,
    context: Context,
    viewModel: SearchTransactionViewModel
) {
    if (listData.isEmpty() && viewModel.searchValue.collectAsState().value == "") {
        CenterLayout(
            content = {
                Text(
                    text = "Masukkan Nomor Invoice",
                    color = FontBlack
                )
            }
        )
    } else if (listData.isEmpty()) {
        CenterLayout(
            content = {
                Text(
                    text = stringResource(
                        id = R.string.note_empty_search_data,
                        "Transaksi"
                    ),
                    color = FontBlack
                )
            }
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 64.dp)
        ) {
            items(listData) { dataTransaction ->
                TransactionItem(
                    modifier = Modifier.clickable {
                        onItemClick(dataTransaction.id)
                    },
                    invoiceCode = dataTransaction.invoiceCode,
                    price = currencyFormatterStringViewZero(dataTransaction.totalPrice),
                    isFullPayment = dataTransaction.isFullPayment,
                    finishAt = dateTimeUniversalToDateDisplay(dataTransaction.finishAt),
                    customerName = dataTransaction.customerName,
                    createAt = dateTimeUniversalToDisplay(dataTransaction.createAt),
                    statusId = dataTransaction.laundryStatusId,
                    paymentDate = dateTimeUniversalToDateDisplay(dataTransaction.paymentDate),
                    estimationReadyToPickup = dateToDisplayMidFormat(dataTransaction.estimationReadyToPickup),
                    onClickSms = {
                        try {
                            var message = ""
                            message = if (dataTransaction.isFullPayment) {
                                "Assalamualaikum... Selamat Pagi, Siang, Sore atau Malam..." +
                                        "Laundry ${dataTransaction.invoiceCode} Siap Untuk Diambil... Terima Kasih..."
                            } else {
                                "Assalamualaikum... Selamat Pagi, Siang, Sore atau Malam..." +
                                        "Laundry ${dataTransaction.invoiceCode} Siap Untuk Diambil... " +
                                        "Total Pembayaran ${
                                            currencyFormatterStringViewZero(
                                                dataTransaction.totalPrice
                                            )
                                        }" +
                                        "Terima Kasih..."
                            }
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("smsto:${dataTransaction.customerNumberPhone}")
                                putExtra("sms_body", message)
                            }
                            if (intent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(intent)
                            } else {
                                Toast.makeText(context, "Kirim SMS gagal", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Kirim SMS gagal", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onClickWa = {
                        try {
                            var message = ""
                            message = if (dataTransaction.isFullPayment) {
                                "Assalamualaikum... Selamat Pagi, Siang, Sore atau Malam..." +
                                        "\nLaundry ${dataTransaction.invoiceCode} Siap Untuk Diambil... \nTerima Kasih..."
                            } else {
                                "Assalamualaikum... Selamat Pagi, Siang, Sore atau Malam..." +
                                        "\nLaundry ${dataTransaction.invoiceCode} Siap Untuk Diambil... " +
                                        "\nTotal Pembayaran ${
                                            currencyFormatterStringViewZero(
                                                dataTransaction.totalPrice
                                            )
                                        }" +
                                        "\nTerima Kasih..."
                            }

                            sendWhatsApp(
                                context,
                                dataTransaction.customerNumberPhone,
                                message,
                                viewModel.user.value.typeWa
                            )
                        } catch (e: Exception) {
                            Toast.makeText(context, "Kirim Wa gagal", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onClickPhone = {
                        try {
                            val intent = Intent(Intent.ACTION_DIAL)
                            intent.data = Uri.parse("tel:${dataTransaction.customerNumberPhone}")
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Telpon gagal", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }
}
package amat.laundry.ui.screen.home

import amat.laundry.R
import amat.laundry.currencyFormatterStringViewZero
import amat.laundry.data.TransactionCustomer
import amat.laundry.data.entity.FilterEntity
import amat.laundry.dateTimeUniversalToDateDisplay
import amat.laundry.dateTimeUniversalToDisplay
import amat.laundry.dateToDisplayMidFormat
import amat.laundry.di.Injection
import amat.laundry.sendWhatsApp
import amat.laundry.ui.common.OnLifecycleEvent
import amat.laundry.ui.common.UiState
import amat.laundry.ui.component.CenterLayout
import amat.laundry.ui.component.ErrorLayout
import amat.laundry.ui.component.FilterItem
import amat.laundry.ui.component.LoadingLayout
import amat.laundry.ui.component.TransactionItem
import amat.laundry.ui.screen.bill.BillActivityNew
import amat.laundry.ui.screen.transaction.AddTransactionActivity
import amat.laundry.ui.screen.transaction.SearchTransactionActivity
import amat.laundry.ui.theme.FontBlack
import amat.laundry.ui.theme.FontWhite
import amat.laundry.ui.theme.GreenDark
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

class TransactionActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        val id = intent.getStringExtra("status")

        setContent {
            val context = LocalContext.current
            LaundryAppTheme {
                if (id != null) {
                    TransactionScreen(context, id)
                } else {
                    TransactionScreen(context, "1")
                }
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
fun TransactionScreen(
    context: Context,
    id: String
) {

    val viewModel: TransactionViewModel =
        viewModel(
            factory = TransactionViewModelFactory(
                Injection.provideTransactionRepository(context),
                Injection.provideUserRepository(context)
            )
        )

    OnLifecycleEvent { owner, event ->
        // do stuff on event
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                viewModel.refreshTotalStatus()
                viewModel.updateStatusSelected(id)
//                viewModel.getCustomer()
            }

            else -> { /* other stuff */
            }
        }
    }

    //START UI
    Column {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.title_transaction),
                    color = FontWhite,
                    fontSize = 22.sp
                )
            },
            backgroundColor = GreenDark,
            navigationIcon = {
                IconButton(
                    onClick = {
                        val activity = (context as? Activity)
                        activity?.finish()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "",
                        tint = Color.White
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = {
                        val intent = Intent(context, SearchTransactionActivity::class.java)
                        context.startActivity(intent)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "",
                        tint = Color.White
                    )
                }
            }
        )

        Column {
            val statusSelected = viewModel.statusSelected.collectAsState().value
            val listStatus: List<FilterEntity> = viewModel.listStatus.collectAsState().value
            LazyRow(contentPadding = PaddingValues(vertical = 4.dp)) {
                items(listStatus, key = { it.value }) { item ->
                    FilterItem(
                        title = item.title,
                        isSelected = item.value == statusSelected.value,
                        modifier = Modifier
                            .padding(horizontal = 4.dp, vertical = 4.dp)
                            .clickable {
                                viewModel.updateStatusSelected(item.value)
                            }
                    )
                }
            }

            Box(
                modifier = Modifier.fillMaxSize()
            ) {

                viewModel.stateTransactionCustomer.collectAsState(initial = UiState.Loading).value.let { uiState ->
                    when (uiState) {
                        is UiState.Error -> {
                            ErrorLayout(
                                modifier = Modifier.fillMaxHeight(),
                                errorMessage = uiState.errorMessage
                            ) {
//                            viewModel.getCustomer()
                            }
                        }

                        UiState.Loading -> {
                            LoadingLayout(modifier = Modifier.fillMaxHeight())
                        }

                        is UiState.Success -> {
                            ListTransactionView(
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

                FloatingActionButton(
                    onClick = {
                        val intent = Intent(context, AddTransactionActivity::class.java)
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp),
                    backgroundColor = GreenDark
                ) {
                    Icon(
                        Icons.Filled.Add,
                        "",
                        modifier = Modifier.size(30.dp),
                        tint = Color.White,
                    )
                }

            }
        }

    }

}

@Composable
fun ListTransactionView(
    listData: List<TransactionCustomer>,
    onItemClick: (String) -> Unit,
    context: Context,
    viewModel: TransactionViewModel
) {
    if (listData.isEmpty()) {
        CenterLayout(
            content = {
                Text(
                    text = stringResource(
                        id = R.string.note_empty_data,
                        "Transaksi"
                    ), color = FontBlack
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
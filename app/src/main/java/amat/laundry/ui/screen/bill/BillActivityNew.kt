package amat.laundry.ui.screen.bill

import amat.laundry.R
import amat.laundry.cleanPointZeroFloat
import amat.laundry.currencyFormatterStringViewZero
import amat.laundry.dateTimeUniversalToDisplay
import amat.laundry.di.Injection
import amat.laundry.ui.common.OnLifecycleEvent
import amat.laundry.ui.common.UiState
import amat.laundry.ui.component.BillItem
import amat.laundry.ui.component.CenterLayout
import amat.laundry.ui.component.ErrorLayout
import amat.laundry.ui.component.LoadingLayout
import amat.laundry.ui.component.PaymentCartItem
import amat.laundry.ui.screen.transaction.AddTransactionActivity
import amat.laundry.ui.theme.Blue
import amat.laundry.ui.theme.FontBlack
import amat.laundry.ui.theme.FontBlackSoft
import amat.laundry.ui.theme.FontGrey
import amat.laundry.ui.theme.FontWhite
import amat.laundry.ui.theme.GreenDark
import amat.laundry.ui.theme.GreyLight
import amat.laundry.ui.theme.LaundryAppTheme
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Print
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

class BillActivityNew : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        val id = intent.getStringExtra("id")

        setContent {
            val context = LocalContext.current
            LaundryAppTheme {
                if (id != null) {
                    BillNewScreen(context, id)
                } else {
                    BillNewScreen(context, "")
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
fun BillNewScreen(
    context: Context,
    transactionId: String
) {

    val viewModel: BillViewModel =
        viewModel(
            factory = BillViewModelFactory(
                Injection.provideUserRepository(context),
                Injection.provideTransactionRepository(context),
                Injection.provideDetailTransactionRepository(context),
            )
        )

    OnLifecycleEvent { owner, event ->
        // do stuff on event
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                if (transactionId != "") {
                    viewModel.getData(transactionId)
                }
            }

            else -> { /* other stuff */
            }
        }
    }

    Column {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.bill),
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
                        //todo check print
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Print,
                        contentDescription = "",
                        tint = Color.White
                    )
                }
            }
        )
        if (transactionId == "") {
            ErrorLayout(errorMessage = "ID Transaksi tidak ada") {
                val activity = (context as? Activity)
                activity?.finish()
            }
        } else {
            viewModel.stateUi.collectAsState(initial = UiState.Loading).value.let { uiState ->
                when (uiState) {
                    is UiState.Error -> {
                        ErrorLayout(
                            modifier = Modifier.fillMaxHeight(),
                            errorMessage = uiState.errorMessage
                        ) {
                            viewModel.getData(transactionId)
                        }
                    }

                    UiState.Loading -> {
                        LoadingLayout(modifier = Modifier.fillMaxHeight())
                    }

                    is UiState.Success -> {
                        BillMainArea(viewModel, context, uiState.data)
                    }
                }
            }
        }
    }
}

@Composable
fun BillMainArea(viewModel: BillViewModel, context: Context, data: BillUi) {
    if (data.listDetailTransaction.isEmpty()) {
        CenterLayout(
            content = {
                Text(
                    text = stringResource(
                        id = R.string.note_empty_data,
                        "List Transaksi"
                    ),
                    color = FontBlack
                )
            }
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        data.businessName, style = TextStyle(
                            fontSize = 18.sp,
                            color = FontBlack,
                        )
                    )
                    Text(
                        data.businessAddress, style = TextStyle(
                            fontSize = 16.sp,
                            color = FontBlack,
                        )
                    )
                    Text(
                        data.businessNumberPhone, style = TextStyle(
                            fontSize = 16.sp,
                            color = FontBlack,
                        )
                    )
                }

                Divider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = GreyLight,
                    thickness = 4.dp
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "Waktu Transaksi", style = TextStyle(
                            fontSize = 14.sp,
                            color = FontGrey,
                        )
                    )
                    Text(
                        dateTimeUniversalToDisplay(data.dateTimeTransaction), style = TextStyle(
                            fontSize = 16.sp,
                            color = FontBlack,
                        )
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "Nama Pelanggan", style = TextStyle(
                            fontSize = 14.sp,
                            color = FontGrey,
                        )
                    )
                    Text(
                        data.customerName, style = TextStyle(
                            fontSize = 16.sp,
                            color = FontBlack,
                        )
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "Nomor Invoice", style = TextStyle(
                            fontSize = 14.sp,
                            color = FontGrey,
                        )
                    )
                    Text(
                        data.invoiceCode, style = TextStyle(
                            fontSize = 16.sp,
                            color = FontBlack,
                        )
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "Status Pembayaran", style = TextStyle(
                            fontSize = 14.sp,
                            color = FontGrey,
                        )
                    )
                    Text(
                        if (data.isFullPayment) "Lunas" else "Belum Lunas", style = TextStyle(
                            fontSize = 16.sp,
                            color = FontBlack,
                        )
                    )
                }

                Divider(
                    modifier = Modifier.padding(top = 4.dp),
                    color = GreyLight,
                    thickness = 4.dp
                )

            }

            items(data.listDetailTransaction) { data ->
                BillItem(
                    productName = data.productName,
                    productPrice = currencyFormatterStringViewZero(data.price.toString()),
                    productTotalPrice = currencyFormatterStringViewZero(data.totalPrice),
                    note = data.note,
                    unit = data.unit,
                    qty = data.qty
                )
            }

            item {
                Divider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = GreyLight,
                    thickness = 4.dp
                )

                Row(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total Pembayaran",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = FontBlack,
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    Text(
                        text = currencyFormatterStringViewZero(data.totalPrice),
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = FontBlack,
                        ),
                        fontWeight = FontWeight.Bold,
                    )

                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp, horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = data.footerNote,
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = FontBlack,
                        )
                    )
                }

                Divider(
                    modifier = Modifier.padding(top = 16.dp),
                    color = GreyLight,
                    thickness = 4.dp
                )

                if (!data.isFullPayment) {
                    Button(
                        onClick = {
                            // todo
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp, horizontal = 8.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Blue)
                    ) {
                        Text(text = "Update Status Lunas", color = FontWhite)
                    }
                }

                Button(
                    onClick = {
                        val activity = (context as? Activity)
                        activity?.finish()
                        val intent = Intent(context, AddTransactionActivity::class.java)
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp, horizontal = 8.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Blue)
                ) {
                    Text(text = "Tambah Transaksi Baru", color = FontWhite)
                }

                Button(
                    onClick = {
                        //todo hapus transaksi
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp, horizontal = 8.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = GreyLight)
                ) {
                    Text(text = "Hapus Transaksi", color = FontBlackSoft)
                }
            }

        }
    }

}
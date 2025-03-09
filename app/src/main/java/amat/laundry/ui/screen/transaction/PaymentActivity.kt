package amat.laundry.ui.screen.transaction

import amat.laundry.R
import amat.laundry.currencyFormatterStringViewZero
import amat.laundry.data.ProductCart
import amat.laundry.di.Injection
import amat.laundry.ui.common.OnLifecycleEvent
import amat.laundry.ui.common.UiState
import amat.laundry.ui.component.BoxPrice
import amat.laundry.ui.component.CenterLayout
import amat.laundry.ui.component.ErrorLayout
import amat.laundry.ui.component.LoadingLayout
import amat.laundry.ui.component.PaymentCartItem
import amat.laundry.ui.theme.ErrorColor
import amat.laundry.ui.theme.FontBlack
import amat.laundry.ui.theme.FontWhite
import amat.laundry.ui.theme.GreenDark
import amat.laundry.ui.theme.GreyLight
import amat.laundry.ui.theme.LaundryAppTheme
import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.material.icons.filled.Clear
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

class PaymentActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            LaundryAppTheme {
                PaymentScreen(context = context)
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
fun PaymentScreen(
    modifier: Modifier = Modifier,
    context: Context
) {
    //todo handle if service 0

    val viewModel: PaymentViewModel =
        viewModel(
            factory = PaymentViewModelFactory(
                Injection.provideCartRepository(context)
            )
        )

    OnLifecycleEvent { owner, event ->
        // do stuff on event
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                viewModel.getProduct()
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
                    text = stringResource(id = R.string.title_payment_transaction),
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
            }
        )

        viewModel.stateProduct.collectAsState(initial = UiState.Loading).value.let { uiState ->
            when (uiState) {
                is UiState.Error -> {
                    ErrorLayout(
                        modifier = Modifier.fillMaxHeight(),
                        errorMessage = uiState.errorMessage
                    ) {
                        viewModel.getProduct()
                    }
                }

                UiState.Loading -> {
                    LoadingLayout(modifier = Modifier.fillMaxHeight())
                }

                is UiState.Success -> {
                    FormPayment(viewModel, uiState.data)
                }
            }
        }

    }
}

@Composable
fun FormPayment(viewModel: PaymentViewModel, listData: List<ProductCart>) {
    if (listData.isEmpty()) {
        CenterLayout(
            content = {
                Text(
                    text = stringResource(
                        id = R.string.note_empty_data,
                        "Keranjang"
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
                Text(
                    text = "Waktu Transaksi",
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = FontBlack,
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Divider(
                    color = GreyLight,
                    thickness = 6.dp
                )
            }

            items(listData) { data ->
                PaymentCartItem(
                    productId = data.productId,
                    productName = data.productName,
                    productPrice = currencyFormatterStringViewZero(data.productPrice.toString()),
                    productTotalPrice = currencyFormatterStringViewZero(data.productTotalPrice),
                    categoryName = data.categoryName,
                    note = data.note,
                    unit = data.unit,
                    qty = data.qty
                )
            }

            item {
                Divider(
                    color = GreyLight,
                    thickness = 6.dp
                )

                Text(
                    text = "Total Pembayaran",
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = FontBlack,
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BoxPrice(
                        title = currencyFormatterStringViewZero(viewModel.stateUi.collectAsState().value.totalPrice),
                        fontSize = 20.sp
                    )
                }

                Text(
                    text = "Tipe Pembayaran",
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = FontBlack,
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Text(
                    text = "Nama Pelanggan",
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = FontBlack,
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Button(
                    onClick = {
                        viewModel.checkPayment()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = GreenDark)
                ) {
                    Text(text = "Proses", color = FontWhite)
                }
            }
        }
    }
}
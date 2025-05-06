package amat.laundry.ui.screen.transaction

import amat.laundry.CashierAdapter
import amat.laundry.R
import amat.laundry.currencyFormatterStringViewZero
import amat.laundry.data.Cashier
import amat.laundry.data.ProductCart
import amat.laundry.di.Injection
import amat.laundry.ui.common.OnLifecycleEvent
import amat.laundry.ui.common.UiState
import amat.laundry.ui.component.BoxPrice
import amat.laundry.ui.component.CenterLayout
import amat.laundry.ui.component.ComboBox
import amat.laundry.ui.component.ErrorLayout
import amat.laundry.ui.component.LoadingLayout
import amat.laundry.ui.component.MyOutlinedTextField
import amat.laundry.ui.component.PaymentCartItem
import amat.laundry.ui.screen.bill.BillActivityNew
import amat.laundry.ui.screen.cashier.AddCashierActivity
import amat.laundry.ui.theme.FontBlack
import amat.laundry.ui.theme.FontWhite
import amat.laundry.ui.theme.GreenDark
import amat.laundry.ui.theme.GreyLight
import amat.laundry.ui.theme.LaundryAppTheme
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog

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

    val viewModel: PaymentViewModel =
        viewModel(
            factory = PaymentViewModelFactory(
                Injection.provideCartRepository(context),
                Injection.provideTransactionRepository(context),
                Injection.provideCashierRepository(context)
            )
        )

    BackHandler {
        val activity = (context as? Activity)
        activity?.finish()
        val intent = Intent(context, AddTransactionActivity::class.java)
        context.startActivity(intent)
    }

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

    //catch get Cashier result
    viewModel.stateListCashier.collectAsState(initial = UiState.Error("")).value.let { uiState ->
        when (uiState) {
            is UiState.Error -> {
                if (uiState.errorMessage.isNotEmpty()) {
                    Toast.makeText(
                        context,
                        uiState.errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            UiState.Loading -> {
                Toast.makeText(
                    context,
                    "Loading Data Kasir",
                    Toast.LENGTH_SHORT
                ).show()
            }

            is UiState.Success -> {
                showBottomSheetCashier(
                    viewModel = viewModel,
                    context = context,
                    uiState.data
                )
            }
        }
    }

    if (!viewModel.isProsesFailed.collectAsState().value.isError) {
        Toast.makeText(context, "Pembayaran Transaksi Berhasil", Toast.LENGTH_SHORT)
            .show()
        val activity = (context as? Activity)
        activity?.finish()
        val intent = Intent(context, BillActivityNew::class.java)
        intent.putExtra("id", viewModel.transactionId.collectAsState().value)
        context.startActivity(intent)
    } else {
        if (viewModel.isProsesFailed.collectAsState().value.errorMessage.isNotEmpty()) {
            Toast.makeText(
                context,
                viewModel.isProsesFailed.collectAsState().value.errorMessage,
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode== RESULT_OK){
            val data = result.data
            val id = data?.getStringExtra("id")
            val name = data?.getStringExtra("name")
            if (id != null && name != null) {
                viewModel.setCustomerId(id)
                viewModel.setCustomerName(name)
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
                        val intent = Intent(context, AddTransactionActivity::class.java)
                        context.startActivity(intent)
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
                    FormPayment(viewModel, uiState.data, context, launcher)
                }
            }
        }

    }
}

@Composable
fun FormPayment(
    viewModel: PaymentViewModel,
    listData: List<ProductCart>,
    context: Context,
    startForResult: ActivityResultLauncher<Intent>
) {
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

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Tipe Customer/Pelanggan", style = TextStyle(
                            fontSize = 18.sp,
                            color = FontBlack,
                        )
                    )
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .weight(1F)
                                .clickable {
                                    viewModel.setIsOldCustomer(true)
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = viewModel.stateUi.collectAsState().value.isOldCustomer,
                                onClick = { viewModel.setIsOldCustomer(true) }
                            )
                            Text(
                                text = "Lama", style = TextStyle(
                                    fontSize = 18.sp,
                                    color = FontBlack,
                                )
                            )
                        }
                        Row(
                            modifier = Modifier
                                .weight(1F)
                                .clickable {
                                    viewModel.setIsOldCustomer(false)
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = !viewModel.stateUi.collectAsState().value.isOldCustomer,
                                onClick = { viewModel.setIsOldCustomer(false) }
                            )
                            Text(
                                text = "Baru", style = TextStyle(
                                    fontSize = 18.sp,
                                    color = FontBlack,
                                )
                            )
                        }
                    }
                }

                if (viewModel.stateUi.collectAsState().value.isOldCustomer) {
                    //old user
                    ComboBox(
                        modifier = Modifier.padding(8.dp, 0.dp, 8.dp, 0.dp),
                        title = stringResource(id = R.string.title_customer),
                        value = viewModel.stateUi.collectAsState().value.customerName,
                        isError = viewModel.isOldCustomerValid.collectAsState().value.isError,
                        errorMessage = viewModel.isOldCustomerValid.collectAsState().value.errorMessage
                    ) {
                        startForResult.launch(Intent(context, CustomerActivity::class.java))
                    }
                } else {
                    //new user
                    MyOutlinedTextField(
                        label = "Nama Pelanggan",
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                        value = viewModel.stateUi.collectAsState().value.customerName,
                        onValueChange = {
                            viewModel.setCustomerName(it)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        isError = viewModel.isNewCustomerNameValid.collectAsState().value.isError,
                        errorMessage = viewModel.isNewCustomerNameValid.collectAsState().value.errorMessage
                    )
                    MyOutlinedTextField(
                        label = "Nomor Handphone",
                        value = viewModel.stateUi.collectAsState().value.customerNumberPhone,
                        onValueChange = {
                            viewModel.setCustomerNumberPhone(it)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        isError = viewModel.isNewCustomerNumberPhoneValid.collectAsState().value.isError,
                        errorMessage = viewModel.isNewCustomerNumberPhoneValid.collectAsState().value.errorMessage
                    )
                    MyOutlinedTextField(
                        label = "Catatan",
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        value = viewModel.stateUi.collectAsState().value.customerNote,
                        onValueChange = {
                            viewModel.setCustomerNote(it)
                        },
                    )
                }

                Divider(
                    modifier = Modifier.padding(top = 8.dp),
                    color = GreyLight,
                    thickness = 8.dp
                )

                ComboBox(
                    modifier = Modifier.padding(8.dp, 8.dp, 8.dp, 0.dp),
                    title = stringResource(id = R.string.title_cashier),
                    value = viewModel.stateUi.collectAsState().value.cashierName,
                    isError = viewModel.isCashierValid.collectAsState().value.isError,
                    errorMessage = viewModel.isCashierValid.collectAsState().value.errorMessage
                ) {
                    viewModel.getCashier()
                }

                MyOutlinedTextField(
                    label = "Catatan Transaksi",
                    value = viewModel.stateUi.collectAsState().value.note,
                    onValueChange = {
                        viewModel.setNote(it)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                )

                Divider(
                    modifier = Modifier.padding(top = 8.dp),
                    color = GreyLight,
                    thickness = 8.dp
                )
            }

            items(listData) { data ->
                PaymentCartItem(
                    productName = data.productName,
                    productPrice = currencyFormatterStringViewZero(data.productPrice.toString()),
                    productTotalPrice = currencyFormatterStringViewZero(data.productTotalPrice),
                    note = data.note,
                    unit = data.unit,
                    qty = data.qty
                )
            }

            item {
                Divider(
                    color = GreyLight,
                    thickness = 8.dp
                )

                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Total Pembayaran",
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = FontBlack,
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BoxPrice(
                        title = currencyFormatterStringViewZero(viewModel.stateUi.collectAsState().value.totalPrice),
                        fontSize = 20.sp
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Tipe Pembayaran", style = TextStyle(
                            fontSize = 18.sp,
                            color = FontBlack,
                        )
                    )
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .weight(1F)
                                .clickable {
                                    viewModel.setPaymentType(true)
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = viewModel.stateUi.collectAsState().value.isFullPayment,
                                onClick = { viewModel.setPaymentType(true) }
                            )
                            Text(
                                text = "Lunas", style = TextStyle(
                                    fontSize = 18.sp,
                                    color = FontBlack,
                                )
                            )
                        }
                        Row(
                            modifier = Modifier
                                .weight(1F)
                                .clickable {
                                    viewModel.setPaymentType(false)
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = !viewModel.stateUi.collectAsState().value.isFullPayment,
                                onClick = { viewModel.setPaymentType(false) }
                            )
                            Text(
                                text = "Bayar Nanti", style = TextStyle(
                                    fontSize = 18.sp,
                                    color = FontBlack,
                                )
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        if (viewModel.dataIsComplete()) {
                            showBottomConfirm(context, viewModel, listData)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = GreenDark)
                ) {
                    Text(
                        text = "Proses",
                        style = TextStyle(
                            fontSize = 18.sp,
                            color = FontWhite,
                        )
                    )
                }
            }
        }
    }
}

private fun showBottomConfirm(
    context: Context,
    viewModel: PaymentViewModel,
    listData: List<ProductCart>
) {
    val bottomSheetDialog = BottomSheetDialog(context)
    bottomSheetDialog.setContentView(R.layout.bottom_sheet_confirm)
    val message = bottomSheetDialog.findViewById<TextView>(R.id.text_message)
    val buttonOk = bottomSheetDialog.findViewById<Button>(R.id.ok_button)

    val messageString =
        "Proses Transaksi Ini?"

    message?.text = messageString

    buttonOk?.setOnClickListener {
        bottomSheetDialog.dismiss()
        viewModel.process(listData)
    }
    bottomSheetDialog.show()

}

fun showBottomSheetCashier(
    viewModel: PaymentViewModel,
    context: Context,
    data: List<Cashier>
) {
    val bottomSheetDialog = BottomSheetDialog(context)
    bottomSheetDialog.setContentView(R.layout.bottom_sheet_select_list)
    val title = bottomSheetDialog.findViewById<TextView>(R.id.text_title)
    val textEmpty = bottomSheetDialog.findViewById<TextView>(R.id.text_empty)
    val buttonAdd = bottomSheetDialog.findViewById<Button>(R.id.button_add)
    val recyclerView = bottomSheetDialog.findViewById<RecyclerView>(R.id.recyclerView)

    title?.setText(R.string.title_cashier)
    buttonAdd?.setText(R.string.add)

    if (data.isEmpty()) {
        textEmpty?.visibility = View.VISIBLE
    }

    buttonAdd?.setOnClickListener {
        val intent = Intent(context, AddCashierActivity::class.java)
        context.startActivity(intent)
        bottomSheetDialog.dismiss()
    }

    val adapter = CashierAdapter {
        viewModel.setCashierSelected(it.id, it.name)
        bottomSheetDialog.dismiss()
    }

    with(recyclerView) {
        this?.setHasFixedSize(true)
        this?.layoutManager =
            LinearLayoutManager(context)
        this?.adapter = adapter
    }

    adapter.setData(data)
    bottomSheetDialog.show()
}
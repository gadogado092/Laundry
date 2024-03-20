package amat.kelolakost.ui.screen.extend

import amat.kelolakost.PriceDurationAdapter
import amat.kelolakost.R
import amat.kelolakost.convertDateToDay
import amat.kelolakost.convertDateToMonth
import amat.kelolakost.convertDateToYear
import amat.kelolakost.currencyFormatterStringViewZero
import amat.kelolakost.data.entity.PriceDuration
import amat.kelolakost.dateToDisplayMidFormat
import amat.kelolakost.di.Injection
import amat.kelolakost.generateLimitText
import amat.kelolakost.generateTextDuration
import amat.kelolakost.ui.common.OnLifecycleEvent
import amat.kelolakost.ui.common.UiState
import amat.kelolakost.ui.component.BoxPrice
import amat.kelolakost.ui.component.BoxRectangle
import amat.kelolakost.ui.component.ComboBox
import amat.kelolakost.ui.component.DateLayout
import amat.kelolakost.ui.component.MyOutlinedTextField
import amat.kelolakost.ui.component.MyOutlinedTextFieldCurrency
import amat.kelolakost.ui.component.QuantityTextField
import amat.kelolakost.ui.screen.bill.BillActivityXml
import amat.kelolakost.ui.theme.ColorRed
import amat.kelolakost.ui.theme.FontBlack
import amat.kelolakost.ui.theme.FontWhite
import amat.kelolakost.ui.theme.GreenDark
import amat.kelolakost.ui.theme.GreyLight
import amat.kelolakost.ui.theme.KelolaKostTheme
import amat.kelolakost.ui.theme.TealGreen
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.RadioButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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

class ExtendActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        val unitId = intent.getStringExtra("unitId")
        val price = intent.getStringExtra("price")
        val duration = intent.getStringExtra("duration")

        setContent {
            val context = LocalContext.current

            KelolaKostTheme {

                var unitIdScreen = ""
                var priceScreen = ""
                var durationScreen = ""

                if (unitId != null) {
                    unitIdScreen = unitId
                }
                if (price != null) {
                    priceScreen = price
                }

                if (duration != null) {
                    durationScreen = duration
                }
                ExtendScreen(
                    context = context,
                    unitId = unitIdScreen,
                    price = priceScreen,
                    duration = durationScreen
                )
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
fun ExtendScreen(
    modifier: Modifier = Modifier,
    context: Context,
    unitId: String,
    price: String = "",
    duration: String = ""
) {
    val extendViewModel: ExtendViewModel =
        viewModel(
            factory = ExtendViewModelModelFactory(
                Injection.provideUnitRepository(context),
                Injection.provideCreditTenantRepository(context),
                Injection.provideUserRepository(context),
                Injection.provideCashFlowRepository(context)
            )
        )
    OnLifecycleEvent { owner, event ->
        // do stuff on event
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                extendViewModel.getDetail(unitId, price, duration)
            }

            else -> {}
        }
    }

    //catch get price duration result
    extendViewModel.stateListPriceDuration.collectAsState(initial = UiState.Error("")).value.let { uiState ->
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
                    "Loading Harga dan Durasi",
                    Toast.LENGTH_SHORT
                ).show()
            }

            is UiState.Success -> {
                showBottomSheetPriceDuration(
                    extendViewModel = extendViewModel,
                    context = context,
                    uiState.data
                )
            }
        }
    }

    if (!extendViewModel.isExtendSuccess.collectAsState().value.isError) {
        Toast.makeText(
            context,
            stringResource(id = R.string.success_extend_rent),
            Toast.LENGTH_SHORT
        ).show()
        val intent = Intent(context, BillActivityXml::class.java)
        val bill = extendViewModel.billEntity.collectAsState().value
        intent.putExtra("object", bill)
        context.startActivity(intent)
        val activity = (context as? Activity)
        activity?.finish()
    } else {
        if (extendViewModel.isExtendSuccess.collectAsState().value.errorMessage.isNotEmpty()) {
            Toast.makeText(
                context,
                extendViewModel.isExtendSuccess.collectAsState().value.errorMessage,
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }


    //START UI
    Column(modifier = modifier) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.extend_rent),
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ComboBox(
                modifier = Modifier.padding(bottom = 8.dp),
                title = stringResource(id = R.string.subtitle_tenant),
                value = extendViewModel.extendUi.collectAsState().value.tenantName
            )
            ComboBox(
                modifier = Modifier.padding(bottom = 8.dp),
                title = stringResource(id = R.string.location_unit),
                value = extendViewModel.extendUi.collectAsState().value.kostName
            )
            ComboBox(
                modifier = Modifier.padding(bottom = 8.dp),
                title = stringResource(id = R.string.subtitle_unit),
                value = "${extendViewModel.extendUi.collectAsState().value.unitName} - ${extendViewModel.extendUi.collectAsState().value.unitTypeName}"
            )
            Text(
                modifier = Modifier.padding(bottom = 8.dp),
                text = "Batas Keluar",
                style = TextStyle(color = FontBlack),
                fontSize = 16.sp
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DateLayout(
                    modifier = Modifier.fillMaxWidth(0.75F),
                    contentHorizontalArrangement = Arrangement.Center,
                    value = if (extendViewModel.extendUi.collectAsState().value.limitCheckOut.isNotEmpty())
                        "${dateToDisplayMidFormat(extendViewModel.extendUi.collectAsState().value.limitCheckOut)} - ${
                            generateLimitText(
                                extendViewModel.extendUi.collectAsState().value.limitCheckOut
                            )
                        }" else "-"
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(id = R.string.debt_tenant),
                        style = TextStyle(color = FontBlack),
                        fontSize = 16.sp
                    )
                    Text(
                        text = "* lakukan pembayaran hutang melalui menu lainnya",
                        style = TextStyle(color = FontBlack),
                        fontSize = 12.sp
                    )
                }
                BoxRectangle(
                    title = currencyFormatterStringViewZero(extendViewModel.extendUi.collectAsState().value.currentDebtTenant.toString()),
                    fontSize = 14.sp,
                    backgroundColor = ColorRed
                )
            }
            MyOutlinedTextFieldCurrency(
                label = "Biaya Tambahan",
                value = extendViewModel.extendUi.collectAsState().value.additionalCost.replace(
                    ".",
                    ""
                ),
                onValueChange = {
                    extendViewModel.setExtraPrice(it)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth(),
                currencyValue = extendViewModel.extendUi.collectAsState().value.additionalCost
            )
            MyOutlinedTextField(
                label = "Keterangan Biaya Tambahan",
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                value = extendViewModel.extendUi.collectAsState().value.noteAdditionalCost,
                onValueChange = {
                    extendViewModel.setNoteExtraPrice(it)
                },
                isError = extendViewModel.isNoteExtraPriceValid.collectAsState().value.isError,
                errorMessage = extendViewModel.isNoteExtraPriceValid.collectAsState().value.errorMessage,
                modifier = Modifier
                    .fillMaxWidth()
            )
            MyOutlinedTextFieldCurrency(
                label = "Diskon/Potongan Harga",
                value = extendViewModel.extendUi.collectAsState().value.discount.replace(
                    ".",
                    ""
                ),
                onValueChange = {
                    extendViewModel.setDiscount(it)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth(),
                currencyValue = extendViewModel.extendUi.collectAsState().value.discount
            )
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                text = stringResource(id = R.string.price_and_duration),
                style = TextStyle(color = FontBlack),
                fontSize = 16.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                QuantityTextField(
                    modifier = Modifier.weight(3.5F),
                    value = extendViewModel.extendUi.collectAsState().value.qty.toString(),
                    onAddClick = {
                        extendViewModel.addQuantity()
                    },
                    onMinClick = {
                        extendViewModel.minQuantity()
                    }
                )
                Spacer(modifier = Modifier.weight(0.5F))
                ComboBox(
                    modifier = Modifier.weight(6F),
                    value = if (extendViewModel.extendUi.collectAsState().value.price == 0) {
                        "Pilih Durasi Sewa"
                    } else {
                        "${currencyFormatterStringViewZero(extendViewModel.extendUi.collectAsState().value.price.toString())}/${extendViewModel.extendUi.collectAsState().value.duration}"
                    },
                    isError = extendViewModel.isDurationSelectedValid.collectAsState().value.isError,
                    errorMessage = extendViewModel.isDurationSelectedValid.collectAsState().value.errorMessage
                ) {
                    extendViewModel.getPriceDuration()
                }
            }
            Text(
                text = "Batas Check Out Setelah Perpanjang",
                style = TextStyle(color = FontBlack),
                fontSize = 16.sp
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DateLayout(
                    modifier = Modifier.fillMaxWidth(0.75F),
                    contentHorizontalArrangement = Arrangement.Center,
                    value = if (extendViewModel.extendUi.collectAsState().value.checkOutDateNew.isNotEmpty()) dateToDisplayMidFormat(
                        extendViewModel.extendUi.collectAsState().value.checkOutDateNew
                    ) else "-"
                )
            }

            Divider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = GreyLight,
                thickness = 2.dp
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(end = 3.dp),
                        text = stringResource(id = R.string.total_payment),
                        style = TextStyle(color = FontBlack),
                        fontSize = 18.sp
                    )
                    Text(
                        text = generateTextDuration(
                            extendViewModel.extendUi.collectAsState().value.duration,
                            extendViewModel.extendUi.collectAsState().value.qty
                        ),
                        style = TextStyle(color = TealGreen, fontWeight = FontWeight.Medium),
                        fontSize = 18.sp
                    )
                }
                BoxPrice(
                    title = currencyFormatterStringViewZero(extendViewModel.extendUi.collectAsState().value.totalPrice),
                    fontSize = 20.sp
                )
            }

            Divider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = GreyLight,
                thickness = 2.dp
            )

            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)) {
                Text(
                    text = stringResource(id = R.string.payment_via),
                    style = TextStyle(color = FontBlack),
                    fontSize = 16.sp
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .weight(1F)
                            .clickable {
                                extendViewModel.setPaymentType(true)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (extendViewModel.extendUi.collectAsState().value.isCash),
                            onClick = { extendViewModel.setPaymentType(true) }
                        )
                        Text(
                            text = "Cash", style = TextStyle(color = FontBlack),
                            fontSize = 16.sp
                        )
                    }
                    Row(
                        modifier = Modifier
                            .weight(1F)
                            .clickable {
                                extendViewModel.setPaymentType(false)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (!extendViewModel.extendUi.collectAsState().value.isCash),
                            onClick = { extendViewModel.setPaymentType(false) }
                        )
                        Text(
                            text = "Transfer", style = TextStyle(color = FontBlack),
                            fontSize = 16.sp
                        )
                    }
                }
            }

            DateLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePickerPayment(context, extendViewModel) },
                title = "Tanggal Pembayaran",
                value = if (extendViewModel.extendUi.collectAsState().value.createAt.isNotEmpty())
                    dateToDisplayMidFormat(extendViewModel.extendUi.collectAsState().value.createAt) else "-",
                isEnable = true
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.payment_method),
                    style = TextStyle(color = FontBlack),
                    fontSize = 16.sp
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .weight(1F)
                            .clickable {
                                extendViewModel.setPaymentMethod(true)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (extendViewModel.extendUi.collectAsState().value.isFullPayment),
                            onClick = { extendViewModel.setPaymentMethod(true) }
                        )
                        Text(
                            text = "Lunas", style = TextStyle(color = FontBlack),
                            fontSize = 16.sp
                        )
                    }
                    Row(
                        modifier = Modifier
                            .weight(1F)
                            .clickable {
                                extendViewModel.setPaymentMethod(false)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (!extendViewModel.extendUi.collectAsState().value.isFullPayment),
                            onClick = { extendViewModel.setPaymentMethod(false) }
                        )
                        Text(
                            text = "Cicil/Angsuran", style = TextStyle(color = FontBlack),
                            fontSize = 16.sp
                        )
                    }
                }
            }

            if (!extendViewModel.extendUi.collectAsState().value.isFullPayment) {
                Column {
                    MyOutlinedTextFieldCurrency(
                        label = "Uang Muka",
                        value = extendViewModel.extendUi.collectAsState().value.downPayment.replace(
                            ".",
                            ""
                        ),
                        onValueChange = {
                            extendViewModel.setDownPayment(it)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth(),
                        currencyValue = extendViewModel.extendUi.collectAsState().value.downPayment,
                        isError = extendViewModel.isDownPaymentValid.collectAsState().value.isError,
                        errorMessage = extendViewModel.isDownPaymentValid.collectAsState().value.errorMessage
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(id = R.string.debt_tenant_extend),
                            style = TextStyle(color = FontBlack),
                            fontSize = 16.sp
                        )
                        BoxRectangle(
                            title = currencyFormatterStringViewZero(extendViewModel.extendUi.collectAsState().value.debtTenantExtend),
                            fontSize = 14.sp,
                            backgroundColor = ColorRed
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(id = R.string.total_debt),
                            style = TextStyle(color = FontBlack),
                            fontSize = 16.sp
                        )
                        BoxRectangle(
                            title = currencyFormatterStringViewZero(extendViewModel.extendUi.collectAsState().value.totalDebtTenant),
                            fontSize = 14.sp,
                            backgroundColor = ColorRed
                        )
                    }
                    Divider(
                        modifier = Modifier.padding(top = 8.dp),
                        color = GreyLight,
                        thickness = 2.dp
                    )
                }
            }

            Row(modifier = Modifier.padding(vertical = 16.dp)) {
                Text(
                    text = "Simpan Dana ",
                    style = TextStyle(color = FontBlack),
                    fontSize = 18.sp
                )
                Text(
                    text = currencyFormatterStringViewZero(extendViewModel.extendUi.collectAsState().value.totalPayment),
                    style = TextStyle(color = TealGreen, fontWeight = FontWeight.Medium),
                    fontSize = 18.sp
                )
            }

            Button(
                onClick = {
                    if (extendViewModel.checkLimitApp()) {
                        showBottomLimitApp(context)
                    } else {
                        if (extendViewModel.dataIsComplete()) {
                            showBottomConfirm(context, extendViewModel)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = GreenDark)
            ) {
                Text(text = stringResource(id = R.string.process), color = FontWhite)
            }

//            Button(
//                onClick = {
//                    val intent = Intent(context, InvoiceActivity::class.java)
//                    context.startActivity(intent)
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(bottom = 16.dp),
//                colors = ButtonDefaults.buttonColors(backgroundColor = GreyLight)
//            ) {
//                Text(text = stringResource(id = R.string.invoice), color = FontBlack)
//            }


        }
    }
}

private fun showBottomConfirm(
    context: Context,
    extendViewModel: ExtendViewModel
) {
    val bottomSheetDialog = BottomSheetDialog(context)
    bottomSheetDialog.setContentView(R.layout.bottom_sheet_confirm)
    val message = bottomSheetDialog.findViewById<TextView>(R.id.text_message)
    val buttonOk = bottomSheetDialog.findViewById<Button>(R.id.ok_button)

    val messageString =
        "Proses Perpanjang penyewa ${extendViewModel.extendUi.value.tenantName} unit ${extendViewModel.extendUi.value.unitName}" +
                "\nDari ${dateToDisplayMidFormat(extendViewModel.extendUi.value.limitCheckOut)} sampai ${
                    dateToDisplayMidFormat(
                        extendViewModel.extendUi.value.checkOutDateNew
                    )
                }?"

    message?.text = messageString

    buttonOk?.setOnClickListener {
        bottomSheetDialog.dismiss()
        extendViewModel.prosesExtend()
    }
    bottomSheetDialog.show()

}

private fun showBottomLimitApp(
    context: Context
) {
    val bottomSheetDialog = BottomSheetDialog(context)
    bottomSheetDialog.setContentView(R.layout.bottom_sheet_confirm)
    val message = bottomSheetDialog.findViewById<TextView>(R.id.text_message)
    val buttonOk = bottomSheetDialog.findViewById<Button>(R.id.ok_button)

    message?.text = context.getString(R.string.info_limit)

    buttonOk?.setOnClickListener {
        bottomSheetDialog.dismiss()
    }
    bottomSheetDialog.show()

}

fun showBottomSheetPriceDuration(
    extendViewModel: ExtendViewModel,
    context: Context,
    data: List<PriceDuration>
) {
    val bottomSheetDialog = BottomSheetDialog(context)
    bottomSheetDialog.setContentView(R.layout.bottom_sheet_select_list)
    val title = bottomSheetDialog.findViewById<TextView>(R.id.text_title)
    val buttonAdd = bottomSheetDialog.findViewById<Button>(R.id.button_add)
    val recyclerView = bottomSheetDialog.findViewById<RecyclerView>(R.id.recyclerView)

    title?.text = context.getText(R.string.subtitle_select_price)
    buttonAdd?.visibility = View.INVISIBLE

    val adapter = PriceDurationAdapter {
        extendViewModel.setPriceDurationSelected(it.price, it.duration)
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

fun showDatePickerPayment(context: Context, viewModel: ExtendViewModel) {
    val mYear: Int = convertDateToYear(viewModel.extendUi.value.createAt).toInt()
    val mMonth: Int = convertDateToMonth(viewModel.extendUi.value.createAt).toInt() - 1
    val mDay: Int = convertDateToDay(viewModel.extendUi.value.createAt).toInt()

    val mDatePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            val dateSelected = "$year-${month + 1}-$day"
            viewModel.setPaymentDate(dateSelected)
        }, mYear, mMonth, mDay
    )
    mDatePickerDialog.show()
}
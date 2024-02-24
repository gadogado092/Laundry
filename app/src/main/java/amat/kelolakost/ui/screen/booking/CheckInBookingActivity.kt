package amat.kelolakost.ui.screen.booking

import amat.kelolakost.PriceDurationAdapter
import amat.kelolakost.R
import amat.kelolakost.TenantAdapter
import amat.kelolakost.convertDateToDay
import amat.kelolakost.convertDateToMonth
import amat.kelolakost.convertDateToYear
import amat.kelolakost.currencyFormatterStringViewZero
import amat.kelolakost.data.Tenant
import amat.kelolakost.data.entity.PriceDuration
import amat.kelolakost.dateToDisplayMidFormat
import amat.kelolakost.di.Injection
import amat.kelolakost.generateTextDuration
import amat.kelolakost.ui.common.OnLifecycleEvent
import amat.kelolakost.ui.common.UiState
import amat.kelolakost.ui.component.BoxPrice
import amat.kelolakost.ui.component.BoxRectangle
import amat.kelolakost.ui.component.ComboBox
import amat.kelolakost.ui.component.DateLayout
import amat.kelolakost.ui.component.ErrorLayout
import amat.kelolakost.ui.component.InformationBox
import amat.kelolakost.ui.component.LoadingLayout
import amat.kelolakost.ui.component.MyOutlinedTextField
import amat.kelolakost.ui.component.MyOutlinedTextFieldCurrency
import amat.kelolakost.ui.component.QuantityTextField
import amat.kelolakost.ui.component.SubBooking
import amat.kelolakost.ui.screen.bill.BillActivityXml
import amat.kelolakost.ui.screen.tenant.AddTenantActivity
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
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.outlined.StickyNote2
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
import androidx.compose.ui.text.style.TextOverflow
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

class CheckInBookingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        val bookingId = intent.getStringExtra("bookingId")

        setContent {
            val context = LocalContext.current
            KelolaKostTheme {
                CheckInScreen(
                    context = context,
                    bookingId = bookingId
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
fun CheckInScreen(
    modifier: Modifier = Modifier,
    context: Context,
    bookingId: String?
) {
    val checkInViewModel: CheckInBookingViewModel =
        viewModel(
            factory = CheckInBookingViewModelFactory(
                Injection.provideTenantRepository(context),
                Injection.provideUnitRepository(context),
                Injection.provideBookingRepository(context),
                Injection.provideUserRepository(context)
            )
        )

    //Init Send Value
    OnLifecycleEvent { owner, event ->
        // do stuff on event
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                if (bookingId != null) {
                    checkInViewModel.getBooking(bookingId)
                }
            }

            else -> {}
        }
    }

    //catch get Tenant result
    checkInViewModel.stateListTenant.collectAsState(initial = UiState.Error("")).value.let { uiState ->
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
                    "Loading Data Penyewa/Tenant",
                    Toast.LENGTH_SHORT
                ).show()
            }

            is UiState.Success -> {
                showBottomSheetTenant(
                    checkInViewModel = checkInViewModel,
                    context = context,
                    uiState.data
                )
            }
        }
    }

    //catch get price duration result
    checkInViewModel.stateListPriceDuration.collectAsState(initial = UiState.Error("")).value.let { uiState ->
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
                    checkInViewModel = checkInViewModel,
                    context = context,
                    uiState.data
                )
            }
        }
    }

    if (!checkInViewModel.isCheckInSuccess.collectAsState().value.isError) {
        Toast.makeText(context, stringResource(id = R.string.success_check_in), Toast.LENGTH_SHORT)
            .show()
        val intent = Intent(context, BillActivityXml::class.java)
        val bill = checkInViewModel.billEntity.collectAsState().value
        intent.putExtra("object", bill)
        context.startActivity(intent)
        val activity = (context as? Activity)
        activity?.finish()
    } else {
        if (checkInViewModel.isCheckInSuccess.collectAsState().value.errorMessage.isNotEmpty()) {
            Toast.makeText(
                context,
                checkInViewModel.isCheckInSuccess.collectAsState().value.errorMessage,
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    //START UI
    Column {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.title_check_in),
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

        checkInViewModel.stateBooking.collectAsState(initial = UiState.Loading).value.let { uiState ->
            when (uiState) {
                is UiState.Error -> {
                    ErrorLayout(errorMessage = uiState.errorMessage) {
                        if (bookingId != null) {
                            checkInViewModel.getBooking(bookingId)
                        }
                    }
                }

                UiState.Loading -> {
                    LoadingLayout()
                }

                is UiState.Success -> {
                    ContentCheckInBooking(checkInViewModel, context)
                }
            }
        }

    }
}

private fun showBottomConfirm(
    context: Context,
    checkInViewModel: CheckInBookingViewModel
) {
    val bottomSheetDialog = BottomSheetDialog(context)
    bottomSheetDialog.setContentView(R.layout.bottom_sheet_confirm)
    val message = bottomSheetDialog.findViewById<TextView>(R.id.text_message)
    val buttonOk = bottomSheetDialog.findViewById<Button>(R.id.ok_button)

    val messageString =
        "Proses Check-In penyewa ${checkInViewModel.checkInUi.value.tenantName} unit ${checkInViewModel.checkInUi.value.unitName}" +
                "\nDari ${dateToDisplayMidFormat(checkInViewModel.checkInUi.value.checkInDate)} sampai ${
                    dateToDisplayMidFormat(
                        checkInViewModel.checkInUi.value.checkOutDate
                    )
                }?"

    message?.text = messageString

    buttonOk?.setOnClickListener {
        bottomSheetDialog.dismiss()
        checkInViewModel.processCheckIn()
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

@Composable
fun ContentCheckInBooking(checkInViewModel: CheckInBookingViewModel, context: Context) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        val data = checkInViewModel.getData()
        InformationBox {
            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = data.unitName[0].uppercase() + data.unitName.drop(1),
                        style = TextStyle(
                            fontSize = 18.sp,
                            color = FontBlack,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    BoxRectangle(title = data.unitTypeName[0].uppercase() + data.unitTypeName.drop(1))
                }
                SubBooking(Icons.Default.Person, data.name)
                SubBooking(Icons.Default.PhoneIphone, data.numberPhone)
                SubBooking(Icons.Default.House, data.kostName)
                SubBooking(
                    Icons.Outlined.StickyNote2,
                    data.note.ifEmpty { "-" }
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Rencana Check-In")
                    Text(text = dateToDisplayMidFormat(data.planCheckIn))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Uang Booking")
                    Text(text = currencyFormatterStringViewZero(data.nominalBooking))
                }
            }
        }
        Spacer(modifier = Modifier.padding(top = 8.dp))

        ComboBox(
            title = stringResource(id = R.string.subtitle_tenant),
            value = checkInViewModel.checkInUi.collectAsState().value.tenantName,
            isError = checkInViewModel.isTenantSelectedValid.collectAsState().value.isError,
            errorMessage = checkInViewModel.isTenantSelectedValid.collectAsState().value.errorMessage
        ) {
            checkInViewModel.getTenant()
        }
        MyOutlinedTextFieldCurrency(
            label = "Biaya Tambahan",
            value = checkInViewModel.checkInUi.collectAsState().value.additionalCost.replace(
                ".",
                ""
            ),
            onValueChange = {
                checkInViewModel.setExtraPrice(it)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth(),
            currencyValue = checkInViewModel.checkInUi.collectAsState().value.additionalCost
        )
        MyOutlinedTextField(
            label = "Keterangan Biaya Tambahan",
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            value = checkInViewModel.checkInUi.collectAsState().value.noteAdditionalCost,
            onValueChange = {
                checkInViewModel.setNoteExtraPrice(it)
            },
            isError = checkInViewModel.isNoteExtraPriceValid.collectAsState().value.isError,
            errorMessage = checkInViewModel.isNoteExtraPriceValid.collectAsState().value.errorMessage,
            modifier = Modifier
                .fillMaxWidth()
        )
        MyOutlinedTextFieldCurrency(
            label = "Diskon/Potongan Harga",
            value = checkInViewModel.checkInUi.collectAsState().value.discount.replace(
                ".",
                ""
            ),
            onValueChange = {
                checkInViewModel.setDiscount(it)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth(),
            currencyValue = checkInViewModel.checkInUi.collectAsState().value.discount
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.subtitle_price_guarantee),
                style = TextStyle(color = FontBlack),
                fontSize = 16.sp
            )
            BoxRectangle(
                title = currencyFormatterStringViewZero(checkInViewModel.checkInUi.collectAsState().value.guaranteeCost.toString()),
                fontSize = 14.sp
            )
        }

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
                value = checkInViewModel.checkInUi.collectAsState().value.qty.toString(),
                onAddClick = {
                    checkInViewModel.addQuantity()
                },
                onMinClick = {
                    checkInViewModel.minQuantity()
                }
            )
            Spacer(modifier = Modifier.weight(0.5F))
            ComboBox(
                modifier = Modifier.weight(6F),
                value = if (checkInViewModel.checkInUi.collectAsState().value.price == 0) {
                    "Pilih Durasi Sewa"
                } else {
                    "${currencyFormatterStringViewZero(checkInViewModel.checkInUi.collectAsState().value.price.toString())}/${checkInViewModel.checkInUi.collectAsState().value.duration}"
                },
                isError = checkInViewModel.isDurationSelectedValid.collectAsState().value.isError,
                errorMessage = checkInViewModel.isDurationSelectedValid.collectAsState().value.errorMessage
            ) {
                checkInViewModel.getPriceDuration()
            }
        }

        Row {
            DateLayout(
                modifier = Modifier
                    .weight(6F)
                    .clickable { showDatePicker(context, checkInViewModel) },
                title = stringResource(id = R.string.date_check_in),
                value = if (checkInViewModel.checkInUi.collectAsState().value.checkInDate.isNotEmpty())
                    dateToDisplayMidFormat(checkInViewModel.checkInUi.collectAsState().value.checkInDate) else "-",
                isEnable = true
            )
            Spacer(modifier = Modifier.weight(1F))
            DateLayout(
                modifier = Modifier.weight(6F),
                title = stringResource(id = R.string.date_check_out),
                value = if (checkInViewModel.checkInUi.collectAsState().value.checkOutDate.isNotEmpty())
                    dateToDisplayMidFormat(checkInViewModel.checkInUi.collectAsState().value.checkOutDate) else "-"
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
                        checkInViewModel.checkInUi.collectAsState().value.duration,
                        checkInViewModel.checkInUi.collectAsState().value.qty
                    ),
                    style = TextStyle(color = TealGreen, fontWeight = FontWeight.Medium),
                    fontSize = 18.sp
                )
            }
            BoxPrice(
                title = currencyFormatterStringViewZero(checkInViewModel.checkInUi.collectAsState().value.totalPrice),
                fontSize = 20.sp
            )
        }

        Divider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = GreyLight,
            thickness = 2.dp
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
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
                            checkInViewModel.setPaymentType(true)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (checkInViewModel.checkInUi.collectAsState().value.isCash),
                        onClick = { checkInViewModel.setPaymentType(true) }
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
                            checkInViewModel.setPaymentType(false)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (!checkInViewModel.checkInUi.collectAsState().value.isCash),
                        onClick = { checkInViewModel.setPaymentType(false) }
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
                .clickable { showDatePickerPayment(context, checkInViewModel) },
            title = "Tanggal Pembayaran",
            value = if (checkInViewModel.checkInUi.collectAsState().value.createAt.isNotEmpty())
                dateToDisplayMidFormat(checkInViewModel.checkInUi.collectAsState().value.createAt) else "-",
            isEnable = true
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
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
                            checkInViewModel.setPaymentMethod(true)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (checkInViewModel.checkInUi.collectAsState().value.isFullPayment),
                        onClick = { checkInViewModel.setPaymentMethod(true) }
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
                            checkInViewModel.setPaymentMethod(false)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (!checkInViewModel.checkInUi.collectAsState().value.isFullPayment),
                        onClick = { checkInViewModel.setPaymentMethod(false) }
                    )
                    Text(
                        text = "Cicil/Angsuran", style = TextStyle(color = FontBlack),
                        fontSize = 16.sp
                    )
                }
            }
        }

        if (!checkInViewModel.checkInUi.collectAsState().value.isFullPayment) {
            Column {
                MyOutlinedTextFieldCurrency(
                    label = "Uang Muka",
                    value = checkInViewModel.checkInUi.collectAsState().value.downPayment.replace(
                        ".",
                        ""
                    ),
                    onValueChange = {
                        checkInViewModel.setDownPayment(it)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth(),
                    currencyValue = checkInViewModel.checkInUi.collectAsState().value.downPayment,
                    isError = checkInViewModel.isDownPaymentValid.collectAsState().value.isError,
                    errorMessage = checkInViewModel.isDownPaymentValid.collectAsState().value.errorMessage
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = R.string.debt_tenant),
                        style = TextStyle(color = FontBlack),
                        fontSize = 16.sp
                    )
                    BoxRectangle(
                        title = currencyFormatterStringViewZero(checkInViewModel.checkInUi.collectAsState().value.debtTenant),
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
                text = currencyFormatterStringViewZero(checkInViewModel.checkInUi.collectAsState().value.totalPayment),
                style = TextStyle(color = TealGreen, fontWeight = FontWeight.Medium),
                fontSize = 18.sp
            )
        }

        Button(
            onClick = {
                if (checkInViewModel.checkLimitApp()) {
                    showBottomLimitApp(context)
                } else {
                    if (checkInViewModel.dataIsComplete()) {
                        showBottomConfirm(context, checkInViewModel)
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

//        Button(
//            onClick = {
//                val intent = Intent(context, InvoiceActivity::class.java)
//                context.startActivity(intent)
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 16.dp),
//            colors = ButtonDefaults.buttonColors(backgroundColor = GreyLight)
//        ) {
//            Text(text = stringResource(id = R.string.invoice), color = FontBlack)
//        }
    }
}


fun showBottomSheetTenant(
    checkInViewModel: CheckInBookingViewModel,
    context: Context,
    data: List<Tenant>
) {
    val bottomSheetDialog = BottomSheetDialog(context)
    bottomSheetDialog.setContentView(R.layout.bottom_sheet_select_list)
    val title = bottomSheetDialog.findViewById<TextView>(R.id.text_title)
    val textEmpty = bottomSheetDialog.findViewById<TextView>(R.id.text_empty)
    val buttonAdd = bottomSheetDialog.findViewById<Button>(R.id.button_add)
    val recyclerView = bottomSheetDialog.findViewById<RecyclerView>(R.id.recyclerView)

    title?.setText(R.string.subtitle_tenant)
    buttonAdd?.setText(R.string.add)

    if (data.isEmpty()) {
        textEmpty?.visibility = View.VISIBLE
    }

    buttonAdd?.setOnClickListener {
        val intent = Intent(context, AddTenantActivity::class.java)
        context.startActivity(intent)
        bottomSheetDialog.dismiss()
    }

    val adapter = TenantAdapter {
        checkInViewModel.setTenantSelected(it.id, it.name, it.numberPhone)
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

fun showBottomSheetPriceDuration(
    checkInViewModel: CheckInBookingViewModel,
    context: Context,
    data: List<PriceDuration>
) {
    val bottomSheetDialog = BottomSheetDialog(context)
    bottomSheetDialog.setContentView(R.layout.bottom_sheet_select_list)
    val title = bottomSheetDialog.findViewById<TextView>(R.id.text_title)
    val textEmpty = bottomSheetDialog.findViewById<TextView>(R.id.text_empty)
    val buttonAdd = bottomSheetDialog.findViewById<Button>(R.id.button_add)
    val recyclerView = bottomSheetDialog.findViewById<RecyclerView>(R.id.recyclerView)

    title?.text = context.getText(R.string.subtitle_select_price)
    buttonAdd?.visibility = View.INVISIBLE

    if (data.isEmpty()) {
        textEmpty?.visibility = View.VISIBLE
    }

    val adapter = PriceDurationAdapter {
        checkInViewModel.setPriceDurationSelected(it.price, it.duration)
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

fun showDatePicker(context: Context, viewModel: CheckInBookingViewModel) {
    val mYear: Int = convertDateToYear(viewModel.checkInUi.value.checkInDate).toInt()
    val mMonth: Int = convertDateToMonth(viewModel.checkInUi.value.checkInDate).toInt() - 1
    val mDay: Int = convertDateToDay(viewModel.checkInUi.value.checkInDate).toInt()

    val mDatePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            val dateSelected = "$year-${month + 1}-$day"
            viewModel.setCheckInDate(dateSelected)
        }, mYear, mMonth, mDay
    )
    mDatePickerDialog.show()
}

fun showDatePickerPayment(context: Context, viewModel: CheckInBookingViewModel) {
    val mYear: Int = convertDateToYear(viewModel.checkInUi.value.createAt).toInt()
    val mMonth: Int = convertDateToMonth(viewModel.checkInUi.value.createAt).toInt() - 1
    val mDay: Int = convertDateToDay(viewModel.checkInUi.value.createAt).toInt()

    val mDatePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            val dateSelected = "$year-${month + 1}-$day"
            viewModel.setPaymentDate(dateSelected)
        }, mYear, mMonth, mDay
    )
    mDatePickerDialog.show()
}
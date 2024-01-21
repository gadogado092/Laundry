package amat.kelolakost.ui.screen.credit_debit

import amat.kelolakost.R
import amat.kelolakost.convertDateToDay
import amat.kelolakost.convertDateToMonth
import amat.kelolakost.convertDateToYear
import amat.kelolakost.currencyFormatterStringViewZero
import amat.kelolakost.data.CreditDebitHome
import amat.kelolakost.dateToDisplayMidFormat
import amat.kelolakost.di.Injection
import amat.kelolakost.ui.common.OnLifecycleEvent
import amat.kelolakost.ui.common.UiState
import amat.kelolakost.ui.component.BoxRectangle
import amat.kelolakost.ui.component.CenterLayout
import amat.kelolakost.ui.component.DateLayout
import amat.kelolakost.ui.component.ErrorLayout
import amat.kelolakost.ui.component.InformationBox
import amat.kelolakost.ui.component.LoadingLayout
import amat.kelolakost.ui.component.MyOutlinedTextFieldCurrency
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
import android.os.Bundle
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
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class PaymentCreditDebitActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        val creditDebitId = intent.getStringExtra("creditDebitId")

        setContent {
            val context = LocalContext.current
            KelolaKostTheme {
                PaymentCreditDebitScreen(context = context, creditDebitId = creditDebitId)
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
fun PaymentCreditDebitScreen(
    modifier: Modifier = Modifier,
    context: Context,
    creditDebitId: String?,
) {

    val myViewModel: PaymentCreditDebitViewModel =
        viewModel(
            factory = PaymentCreditDebitViewModelFactory(
                Injection.provideCreditDebitRepository(
                    context
                )
            )
        )

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                if (creditDebitId != null) {
                    myViewModel.getDetailCreditDebit(creditDebitId)
                }
            }

            else -> {

            }

        }
    }

    if (!myViewModel.isProsesSuccess.collectAsState().value.isError) {
        Toast.makeText(
            context,
            stringResource(id = R.string.success_payment),
            Toast.LENGTH_SHORT
        )
            .show()
        val activity = (context as? Activity)
        activity?.finish()
    } else {
        if (myViewModel.isProsesSuccess.collectAsState().value.errorMessage.isNotEmpty()) {
            Toast.makeText(
                context,
                myViewModel.isProsesSuccess.collectAsState().value.errorMessage,
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
                    text = stringResource(id = R.string.title_payment_credit_debit),
                    color = FontWhite,
                    fontSize = 20.sp
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

        myViewModel.stateCreditDebit.collectAsState(initial = UiState.Loading).value.let { uiState ->
            when (uiState) {
                is UiState.Error -> {
                    ErrorLayout(errorMessage = uiState.errorMessage) {
                        if (creditDebitId != null) {
                            myViewModel.getDetailCreditDebit(creditDebitId)
                        }
                    }
                }

                UiState.Loading -> LoadingLayout()
                is UiState.Success -> {
                    ContentPaymentCreditDebit(
                        context = context,
                        myViewModel = myViewModel,
                        uiState.data
                    )
                }
            }

        }

    }
}

@Composable
fun ContentPaymentCreditDebit(
    context: Context,
    myViewModel: PaymentCreditDebitViewModel,
    data: CreditDebitHome
) {
    if (data.creditDebitId.isEmpty()) {
        CenterLayout(
            content = {
                Text(
                    text = stringResource(
                        id = R.string.note_empty_data,
                        ""
                    )
                )
            }
        )
    } else {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                .verticalScroll(
                    rememberScrollState()
                )
        ) {
            Text(
                text = stringResource(id = R.string.subtitle_customer),
                style = TextStyle(color = FontBlack),
                fontSize = 16.sp
            )
            InformationBox(borderColor = TealGreen, value = data.customerCreditDebitName)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.subtitle_number_phone),
                style = TextStyle(color = FontBlack),
                fontSize = 16.sp
            )
            InformationBox(borderColor = TealGreen, value = data.customerCreditDebitNumberPhone)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Keterangan",
                style = TextStyle(color = FontBlack),
                fontSize = 16.sp
            )
            InformationBox(borderColor = TealGreen, value = data.note)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = generateTextLabel(data.status),
                style = TextStyle(
                    color = generateColorStatus(data.status),
                    fontWeight = FontWeight.Medium
                ),
                fontSize = 16.sp
            )
            InformationBox(
                borderColor = generateColorStatus(data.status),
                value = currencyFormatterStringViewZero(data.remaining.toString())
            )
            Spacer(modifier = Modifier.height(8.dp))
            DateLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePickerPayment(context, myViewModel) },
                title = "Tanggal Pembayaran",
                value = if (myViewModel.stateUi.collectAsState().value.createAt.isNotEmpty())
                    dateToDisplayMidFormat(myViewModel.stateUi.collectAsState().value.createAt) else "-",
                isEnable = true
            )
            Spacer(modifier = Modifier.height(8.dp))
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
                            myViewModel.setPaymentType(true)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (myViewModel.stateUi.collectAsState().value.isCash),
                        onClick = { myViewModel.setPaymentType(true) }
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
                            myViewModel.setPaymentType(false)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (!myViewModel.stateUi.collectAsState().value.isCash),
                        onClick = { myViewModel.setPaymentType(false) }
                    )
                    Text(
                        text = "Transfer", style = TextStyle(color = FontBlack),
                        fontSize = 16.sp
                    )
                }
            }

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
                                myViewModel.setPaymentMethod(true)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (myViewModel.stateUi.collectAsState().value.isFullPayment),
                            onClick = { myViewModel.setPaymentMethod(true) }
                        )
                        Text(
                            text = "Pelunasan", style = TextStyle(color = FontBlack),
                            fontSize = 16.sp
                        )
                    }
                    Row(
                        modifier = Modifier
                            .weight(1F)
                            .clickable {
                                myViewModel.setPaymentMethod(false)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (!myViewModel.stateUi.collectAsState().value.isFullPayment),
                            onClick = { myViewModel.setPaymentMethod(false) }
                        )
                        Text(
                            text = "Cicil/Angsuran", style = TextStyle(color = FontBlack),
                            fontSize = 16.sp
                        )
                    }
                }
            }

            if (!myViewModel.stateUi.collectAsState().value.isFullPayment) {
                Column {
                    DateLayout(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showDatePickerDueDate(
                                    context,
                                    myViewModel
                                )
                            },
                        title = "Jatuh Tempo Berikutnya",
                        value = if (myViewModel.stateUi.collectAsState().value.dueDate.isNotEmpty())
                            dateToDisplayMidFormat(myViewModel.stateUi.collectAsState().value.dueDate) else "-",
                        isEnable = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    MyOutlinedTextFieldCurrency(
                        label = "Jumlah Pembayaran",
                        value = myViewModel.stateUi.collectAsState().value.downPayment.replace(
                            ".",
                            ""
                        ),
                        onValueChange = {
                            myViewModel.setDownPayment(it)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth(),
                        currencyValue = myViewModel.stateUi.collectAsState().value.downPayment,
                        isError = myViewModel.isDownPaymentValid.collectAsState().value.isError,
                        errorMessage = myViewModel.isDownPaymentValid.collectAsState().value.errorMessage
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = generateTextLabel(data.status),
                            style = TextStyle(color = FontBlack),
                            fontSize = 16.sp
                        )
                        BoxRectangle(
                            title = currencyFormatterStringViewZero(myViewModel.stateUi.collectAsState().value.remaining),
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
                    text = generateLabelNominal(data.status),
                    style = TextStyle(color = FontBlack),
                    fontSize = 18.sp
                )
                Text(
                    text = currencyFormatterStringViewZero(myViewModel.stateUi.collectAsState().value.totalPayment),
                    style = TextStyle(color = TealGreen, fontWeight = FontWeight.Medium),
                    fontSize = 18.sp
                )
            }

            Button(
                onClick = {
                    if (myViewModel.dataIsComplete()) {
                        showBottomConfirm(context, myViewModel)
                    }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = GreenDark)
            ) {
                Text(text = stringResource(id = R.string.process), color = FontWhite)
            }

        }

    }
}

fun generateTextLabel(status: Int): String {
    if (status == 0) {
        return "Sisa Hutang"
    } else if (status == 1) {
        return "Sisa Piutang"
    }
    return ""
}

fun generateLabelNominal(status: Int): String {
    if (status == 0) {
        return "Dana Keluar "
    } else if (status == 1) {
        return "Dana Masuk "
    }
    return ""
}

fun generateColorStatus(status: Int): Color {
    if (status == 0) {
        return ColorRed
    } else if (status == 1) {
        return TealGreen
    }
    return TealGreen
}

private fun showBottomConfirm(
    context: Context,
    viewModel: PaymentCreditDebitViewModel
) {
    val bottomSheetDialog = BottomSheetDialog(context)
    bottomSheetDialog.setContentView(R.layout.bottom_sheet_confirm)
    val message = bottomSheetDialog.findViewById<TextView>(R.id.text_message)
    val buttonOk = bottomSheetDialog.findViewById<Button>(R.id.ok_button)

    var messageString = ""
    if (viewModel.stateUi.value.status == 0) {
        messageString =
            "Proses Pembayaran Hutang ${viewModel.stateUi.value.creditDebitName}?"
    } else if (viewModel.stateUi.value.status == 1) {
        messageString = "Proses Pembayaran Piutang ${viewModel.stateUi.value.creditDebitName}?"
    }

    message?.text = messageString

    buttonOk?.setOnClickListener {
        bottomSheetDialog.dismiss()
        viewModel.proses()
    }
    bottomSheetDialog.show()

}

fun showDatePickerPayment(context: Context, viewModel: PaymentCreditDebitViewModel) {
    val mYear: Int = convertDateToYear(viewModel.stateUi.value.createAt).toInt()
    val mMonth: Int = convertDateToMonth(viewModel.stateUi.value.createAt).toInt() - 1
    val mDay: Int = convertDateToDay(viewModel.stateUi.value.createAt).toInt()

    val mDatePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            val dateSelected = "$year-${month + 1}-$day"
            viewModel.setPaymentDate(dateSelected)
        }, mYear, mMonth, mDay
    )
    mDatePickerDialog.show()
}

fun showDatePickerDueDate(context: Context, viewModel: PaymentCreditDebitViewModel) {
    val mYear: Int = convertDateToYear(viewModel.stateUi.value.dueDate).toInt()
    val mMonth: Int = convertDateToMonth(viewModel.stateUi.value.dueDate).toInt() - 1
    val mDay: Int = convertDateToDay(viewModel.stateUi.value.dueDate).toInt()

    val mDatePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            val dateSelected = "$year-${month + 1}-$day"
            viewModel.setDueDate(dateSelected)
        }, mYear, mMonth, mDay
    )
    mDatePickerDialog.show()
}
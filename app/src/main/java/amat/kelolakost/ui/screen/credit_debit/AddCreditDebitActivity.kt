package amat.kelolakost.ui.screen.credit_debit

import amat.kelolakost.CustomerAdapter
import amat.kelolakost.R
import amat.kelolakost.convertDateToDay
import amat.kelolakost.convertDateToMonth
import amat.kelolakost.convertDateToYear
import amat.kelolakost.data.CustomerCreditDebit
import amat.kelolakost.dateToDisplayMidFormat
import amat.kelolakost.di.Injection
import amat.kelolakost.ui.common.UiState
import amat.kelolakost.ui.component.ComboBox
import amat.kelolakost.ui.component.DateLayout
import amat.kelolakost.ui.component.MyOutlinedTextField
import amat.kelolakost.ui.component.MyOutlinedTextFieldCurrency
import amat.kelolakost.ui.theme.FontBlack
import amat.kelolakost.ui.theme.FontWhite
import amat.kelolakost.ui.theme.GreenDark
import amat.kelolakost.ui.theme.KelolaKostTheme
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog

class AddCreditDebitActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            KelolaKostTheme {
                AddCreditDebitScreen(context = context)
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
fun AddCreditDebitScreen(
    modifier: Modifier = Modifier,
    context: Context
) {
    val myViewModel: AddCreditDebitViewModel =
        viewModel(
            factory = AddCreditDebitViewModelFactory(
                Injection.provideCreditDebitRepository(context),
                Injection.provideCustomerCreditDebitRepository(context)
            )
        )

    //catch get Customer result
    myViewModel.stateListCustomer.collectAsState(initial = UiState.Error("")).value.let { uiState ->
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
                showBottomSheetCustomer(
                    addCreditDebitViewModel = myViewModel,
                    context = context,
                    uiState.data
                )
            }
        }
    }

    if (!myViewModel.isProsesSuccess.collectAsState().value.isError) {
        Toast.makeText(context, stringResource(id = R.string.success_add_data), Toast.LENGTH_SHORT)
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
                    text = stringResource(id = R.string.title_add_debt_credit),
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
                title = stringResource(id = R.string.subtitle_customer),
                value = myViewModel.stateUi.collectAsState().value.customerCreditDebitName,
                isError = myViewModel.isCustomerSelectedValid.collectAsState().value.isError,
                errorMessage = myViewModel.isCustomerSelectedValid.collectAsState().value.errorMessage
            ) {
                myViewModel.getCustomer()
            }

            Text(
                text = stringResource(id = R.string.type_credit_debit),
                style = TextStyle(color = FontBlack),
                fontSize = 16.sp
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .weight(1F)
                        .clickable {
                            myViewModel.setType(true)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (myViewModel.stateUi.collectAsState().value.isCredit),
                        onClick = { myViewModel.setType(true) }
                    )
                    Text(
                        text = "Hutang", style = TextStyle(color = FontBlack),
                        fontSize = 16.sp
                    )
                }
                Row(
                    modifier = Modifier
                        .weight(1F)
                        .clickable {
                            myViewModel.setType(false)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (!myViewModel.stateUi.collectAsState().value.isCredit),
                        onClick = { myViewModel.setType(false) }
                    )
                    Text(
                        text = "Piutang", style = TextStyle(color = FontBlack),
                        fontSize = 16.sp
                    )
                }
            }
            Text(
                text = generateInfoText(myViewModel.stateUi.collectAsState().value.isCredit),
                style = TextStyle(color = FontBlack),
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            MyOutlinedTextFieldCurrency(
                label = generateNominalLabel(myViewModel.stateUi.collectAsState().value.isCredit),
                value = myViewModel.stateUi.collectAsState().value.nominal.replace(
                    ".",
                    ""
                ),
                onValueChange = {
                    myViewModel.setNominal(it)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth(),
                isError = myViewModel.isNominalValid.collectAsState().value.isError,
                errorMessage = myViewModel.isNominalValid.collectAsState().value.errorMessage,
                currencyValue = myViewModel.stateUi.collectAsState().value.nominal
            )
            MyOutlinedTextField(
                label = generateNoteLabel(myViewModel.stateUi.collectAsState().value.isCredit),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                value = myViewModel.stateUi.collectAsState().value.note,
                onValueChange = {
                    myViewModel.setNote(it)
                },
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth(),
                singleLine = false,
                isError = myViewModel.isNoteValid.collectAsState().value.isError,
                errorMessage = myViewModel.isNoteValid.collectAsState().value.errorMessage
            )
            DateLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePickerPayment(context, myViewModel) },
                title = "Tanggal Transaksi",
                value = if (myViewModel.stateUi.collectAsState().value.createAt.isNotEmpty())
                    dateToDisplayMidFormat(myViewModel.stateUi.collectAsState().value.createAt) else "-",
                isEnable = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            DateLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDueDatePicker(context, myViewModel) },
                title = "Tanggal Jatuh Tempo",
                value = if (myViewModel.stateUi.collectAsState().value.dueDate.isNotEmpty())
                    dateToDisplayMidFormat(myViewModel.stateUi.collectAsState().value.dueDate) else "-",
                isEnable = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = generateLabelTypePayment(myViewModel.stateUi.collectAsState().value.isCredit),
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

fun generateInfoText(isCredit: Boolean): String {
    return if (isCredit) {
        "Hutang adalah Pinjaman Anda kepada Pelanggan"
    } else {
        "Piutang adalah Pinjaman Pelanggan kepada Anda"
    }
}

fun generateLabelTypePayment(isCredit: Boolean): String {
    return if (isCredit) {
        "Pembayaran Kas Masuk Secara"
    } else {
        "Pembayaran Kas Keluar Secara"
    }
}

fun generateNoteLabel(isCredit: Boolean): String {
    return if (isCredit) {
        "Keterangan Hutang"
    } else {
        "Keterangan Piutang"
    }
}

fun generateNominalLabel(isCredit: Boolean): String {
    return if (isCredit) {
        "Nominal Hutang"
    } else {
        "Nominal Piutang"
    }
}

private fun showBottomConfirm(
    context: Context,
    addCreditDebitViewModel: AddCreditDebitViewModel
) {
    val bottomSheetDialog = BottomSheetDialog(context)
    bottomSheetDialog.setContentView(R.layout.bottom_sheet_confirm)
    val message = bottomSheetDialog.findViewById<TextView>(R.id.text_message)
    val buttonOk = bottomSheetDialog.findViewById<Button>(R.id.ok_button)

    val messageString: String

    if (addCreditDebitViewModel.stateUi.value.isCredit) {
        messageString = "Tambah Data Hutang ${addCreditDebitViewModel.stateUi.value.customerCreditDebitName}?"
    } else {
        messageString = "Tambah Data Piutang ${addCreditDebitViewModel.stateUi.value.customerCreditDebitName}?"
    }

    message?.text = messageString

    buttonOk?.setOnClickListener {
        bottomSheetDialog.dismiss()
        addCreditDebitViewModel.proses()
    }
    bottomSheetDialog.show()

}

fun showBottomSheetCustomer(
    addCreditDebitViewModel: AddCreditDebitViewModel,
    context: Context,
    data: List<CustomerCreditDebit>
) {
    val bottomSheetDialog = BottomSheetDialog(context)
    bottomSheetDialog.setContentView(R.layout.bottom_sheet_select_list)
    val title = bottomSheetDialog.findViewById<TextView>(R.id.text_title)
    val textEmpty = bottomSheetDialog.findViewById<TextView>(R.id.text_empty)
    val buttonAdd = bottomSheetDialog.findViewById<Button>(R.id.button_add)
    val recyclerView = bottomSheetDialog.findViewById<RecyclerView>(R.id.recyclerView)

    title?.setText(R.string.subtitle_customer)
    buttonAdd?.setText(R.string.add)

    if (data.isEmpty()) {
        textEmpty?.visibility = View.VISIBLE
    }

    buttonAdd?.setOnClickListener {
        val intent = Intent(context, AddCreditDebitCustomerActivity::class.java)
        context.startActivity(intent)
        bottomSheetDialog.dismiss()
    }

    val adapter = CustomerAdapter {
        addCreditDebitViewModel.setCustomerSelected(it.id, it.name)
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

fun showDatePickerPayment(context: Context, viewModel: AddCreditDebitViewModel) {
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

fun showDueDatePicker(context: Context, viewModel: AddCreditDebitViewModel) {
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
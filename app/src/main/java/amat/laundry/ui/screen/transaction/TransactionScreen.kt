package amat.laundry.ui.screen.transaction

import amat.laundry.R
import amat.laundry.checkDateRangeValid
import amat.laundry.currencyFormatterStringViewZero
import amat.laundry.data.TransactionCustomer
import amat.laundry.dateRoomDay
import amat.laundry.dateRoomMonth
import amat.laundry.dateRoomYear
import amat.laundry.dateTimeUniversalToDateDisplay
import amat.laundry.dateTimeUniversalToDisplay
import amat.laundry.dateToDisplayMidFormat
import amat.laundry.di.Injection
import amat.laundry.sendWhatsApp
import amat.laundry.ui.common.OnLifecycleEvent
import amat.laundry.ui.common.UiState
import amat.laundry.ui.component.CenterLayout
import amat.laundry.ui.component.DateLayout
import amat.laundry.ui.component.ErrorLayout
import amat.laundry.ui.component.LoadingLayout
import amat.laundry.ui.component.TransactionItem
import amat.laundry.ui.screen.bill.BillActivityNew
import amat.laundry.ui.theme.FontBlack
import amat.laundry.ui.theme.GreenDark
import amat.laundry.ui.theme.TealGreen
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

@Composable
fun TransactionScreen(
    context: Context,
    modifier: Modifier = Modifier
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
                viewModel.getTransaction()
            }

            else -> { /* other stuff */
            }
        }
    }

    Column(modifier = modifier) {
        Column(modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)) {
            Text(
                text = "Range Tanggal Transaksi", style = TextStyle(
                    fontSize = 16.sp,
                    color = FontBlack,
                )
            )
            Spacer(Modifier.height(2.dp))
            viewModel.stateUi.collectAsState(initial = TransactionUi()).value.let { value ->
                if (value.startDate.isNotEmpty() && value.endDate.isNotEmpty()) {
                    DateLayout(
                        value = "${dateToDisplayMidFormat(value.startDate)} - ${
                            dateToDisplayMidFormat(
                                value.endDate
                            )
                        }",
                        isEnable = true,
                        modifier = Modifier.clickable {
                            showDatePickerStart(context, viewModel)
                        }
                    )
                } else {
                    DateLayout(
                        value = "error gan"
                    )
                }
            }
        }
        Divider(
            color = TealGreen,
            thickness = 2.dp,
            modifier = Modifier.padding(top = 2.dp),
        )

        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            viewModel.stateListTransaction.collectAsState(initial = UiState.Loading).value.let { uiState ->
                when (uiState) {
                    is UiState.Error -> {
                        ErrorLayout(errorMessage = uiState.errorMessage) {
                            viewModel.getTransaction()
                        }
                    }

                    UiState.Loading -> {
                        LoadingLayout()
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
                    if (viewModel.checkLimitApp()) {
                        showBottomLimitApp(context)
                    } else {
                        val intent = Intent(context, AddTransactionActivity::class.java)
                        context.startActivity(intent)
                    }
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

fun showDatePickerStart(context: Context, viewModel: TransactionViewModel) {
    val mYear: Int = dateRoomYear(viewModel.stateUi.value.startDate).toInt()
    val mMonth: Int = dateRoomMonth(viewModel.stateUi.value.startDate).toInt() - 1
    val mDay: Int = dateRoomDay(viewModel.stateUi.value.startDate).toInt()

    val mDatePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            val dateStart = "$year-${month + 1}-$day"
            showDatePickerEnd(context, viewModel, dateStart)
        }, mYear, mMonth, mDay
    )
    mDatePickerDialog.setMessage("Pilih Tanggal Awal")
    mDatePickerDialog.show()
}

fun showDatePickerEnd(context: Context, viewModel: TransactionViewModel, dateStart: String) {
    val mYear: Int = dateRoomYear(viewModel.stateUi.value.endDate).toInt()
    val mMonth: Int = dateRoomMonth(viewModel.stateUi.value.endDate).toInt() - 1
    val mDay: Int = dateRoomDay(viewModel.stateUi.value.endDate).toInt()

    val mDatePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            if (checkDateRangeValid(dateStart, "$year-${month + 1}-$day")) {
                viewModel.setDateDialog(dateStart, "$year-${month + 1}-$day")
            } else {
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.message_date_range_invalid),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }, mYear, mMonth, mDay
    )
    mDatePickerDialog.setMessage("Pilih Tanggal Akhir")
    mDatePickerDialog.show()
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
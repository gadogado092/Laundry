package amat.kelolakost.ui.screen.cash_flow

import amat.kelolakost.R
import amat.kelolakost.checkDateRangeValid
import amat.kelolakost.currencyFormatterStringViewZero
import amat.kelolakost.data.CashFlow
import amat.kelolakost.data.entity.BillEntity
import amat.kelolakost.dateRoomDay
import amat.kelolakost.dateRoomMonth
import amat.kelolakost.dateRoomYear
import amat.kelolakost.dateToDisplayMidFormat
import amat.kelolakost.di.Injection
import amat.kelolakost.ui.common.OnLifecycleEvent
import amat.kelolakost.ui.common.UiState
import amat.kelolakost.ui.common.ValidationResult
import amat.kelolakost.ui.component.CashCard
import amat.kelolakost.ui.component.CashFlowItem
import amat.kelolakost.ui.component.CenterLayout
import amat.kelolakost.ui.component.DateLayout
import amat.kelolakost.ui.component.ErrorLayout
import amat.kelolakost.ui.component.LoadingLayout
import amat.kelolakost.ui.screen.bill.BillActivityXml
import amat.kelolakost.ui.theme.FontBlack
import amat.kelolakost.ui.theme.GreenDark
import amat.kelolakost.ui.theme.TealGreen
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

@Composable
fun CashFlowScreen(
    modifier: Modifier = Modifier,
    context: Context,
) {
    val viewModel: CashFlowViewModel =
        viewModel(
            factory = CashFlowViewModelFactory(
                Injection.provideCashFlowRepository(context),
                Injection.provideUserRepository(context)
            )
        )

    OnLifecycleEvent { owner, event ->
        // do stuff on event
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                viewModel.getIncome()
                viewModel.getBalance()
                viewModel.getOutCome()
                viewModel.getCashFlow()
            }

            else -> { /* other stuff */
            }
        }
    }

    viewModel.stateReport.collectAsState().value.let { uiState ->

        when (uiState) {
            is ValidationResult.Error -> {
                Toast.makeText(
                    context,
                    "Problem ${uiState.errorMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            is ValidationResult.Loading -> LoadingLayout(text = uiState.loadingMessage)
            is ValidationResult.Success -> {
                showBottomConfirm(context, uiState.data, viewModel)
            }

            else -> {}
        }

    }

    //START UI
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Saldo Saat Ini",
                    style = TextStyle(fontSize = 12.sp, color = FontBlack)
                )
                ContentBalance(viewModel)
            }
            viewModel.stateCashFLowUi.collectAsState(initial = CashFLowUi()).value.let { value ->
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ContentTotalIncome(viewModel = viewModel)
            ContentTotalOutcome(viewModel = viewModel)

            Card(
                modifier = modifier
                    .padding(end = 4.dp)
                    .clickable {
                        viewModel.report()
                    },
                elevation = 4.dp
            ) {
                Row {
                    Image(
                        modifier = Modifier.padding(4.dp),
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(color = TealGreen)
                    )
                    Text(
                        text = "Excel",
                        color = FontBlack,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(
                                end = 8.dp
                            ),
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

            viewModel.stateListCashFlow.collectAsState(initial = UiState.Loading).value.let { uiState ->
                when (uiState) {
                    is UiState.Error -> {
                        ErrorLayout(errorMessage = uiState.errorMessage) {
                            viewModel.getCashFlow()
                        }
                    }

                    UiState.Loading -> {
                        LoadingLayout()
                    }

                    is UiState.Success -> {
                        ListCashFLow(uiState.data,
                            onItemClick = { billEntity ->
                                val intent = Intent(context, BillActivityXml::class.java)
                                intent.putExtra(
                                    "object",
                                    billEntity.copy(typeWa = viewModel.typeWa.value)
                                )
                                context.startActivity(intent)
                            })
                    }
                }
            }

            FloatingActionButton(
                onClick = {
                    val intent = Intent(context, AddCashFlowActivity::class.java)
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

@Composable
fun ListCashFLow(listData: List<CashFlow>, onItemClick: (BillEntity) -> Unit) {
    if (listData.isEmpty()) {
        CenterLayout(
            content = {
                Text(
                    text = stringResource(
                        id = R.string.note_empty_data,
                        "Alur Kas"
                    ), color = FontBlack
                )
            }
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 64.dp)
        ) {
            items(listData) { data ->
                CashFlowItem(
                    modifier = Modifier.clickable {
                        onItemClick(
                            BillEntity(
                                createAt = dateToDisplayMidFormat(data.createAt),
                                nominal = currencyFormatterStringViewZero(data.nominal),
                                note = data.note
                            )
                        )
                    },
                    nominal = data.nominal,
                    typePayment = data.typePayment,
                    createAt = data.createAt,
                    type = data.type,
                    note = data.note
                )
            }
        }
    }
}

@Composable
fun ContentBalance(viewModel: CashFlowViewModel) {
    viewModel.stateBalance.collectAsState(initial = UiState.Loading).value.let { uiState ->
        when (uiState) {
            is UiState.Error -> {
                Text(
                    text = "error",
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        color = FontBlack,
                        fontSize = 16.sp
                    )
                )
            }

            UiState.Loading -> {
                Text(
                    text = "Loading",
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        color = FontBlack,
                        fontSize = 16.sp
                    )
                )
            }

            is UiState.Success -> {
                Text(
                    text = if (uiState.data.total == null) "0" else currencyFormatterStringViewZero(
                        uiState.data.total!!
                    ),
                    style = TextStyle(fontWeight = FontWeight.Medium, color = FontBlack)
                )
            }
        }
    }
}

@Composable
fun ContentTotalIncome(viewModel: CashFlowViewModel) {
    viewModel.stateTotalIncome.collectAsState(initial = UiState.Loading).value.let { uiState ->
        when (uiState) {
            is UiState.Error -> {
                CashCard(
                    type = 0,
                    nominal = "error",
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            UiState.Loading -> {
                CashCard(type = 0, nominal = "...", modifier = Modifier.padding(horizontal = 8.dp))
            }

            is UiState.Success -> {
                CashCard(
                    type = 0,
                    nominal = if (uiState.data.total == null) "0" else currencyFormatterStringViewZero(
                        uiState.data.total!!
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}

@Composable
fun ContentTotalOutcome(viewModel: CashFlowViewModel) {
    viewModel.stateTotalOutcome.collectAsState(initial = UiState.Loading).value.let { uiState ->
        when (uiState) {
            is UiState.Error -> {
                CashCard(
                    type = 1,
                    nominal = "error",
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            UiState.Loading -> {
                CashCard(type = 1, nominal = "...", modifier = Modifier.padding(horizontal = 8.dp))
            }

            is UiState.Success -> {
                CashCard(
                    type = 1,
                    nominal = if (uiState.data.total == null) "0" else currencyFormatterStringViewZero(
                        uiState.data.total!!
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}

fun showDatePickerStart(context: Context, viewModel: CashFlowViewModel) {
    val mYear: Int = dateRoomYear(viewModel.stateCashFLowUi.value.startDate).toInt()
    val mMonth: Int = dateRoomMonth(viewModel.stateCashFLowUi.value.startDate).toInt() - 1
    val mDay: Int = dateRoomDay(viewModel.stateCashFLowUi.value.startDate).toInt()

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

fun showDatePickerEnd(context: Context, viewModel: CashFlowViewModel, dateStart: String) {
    val mYear: Int = dateRoomYear(viewModel.stateCashFLowUi.value.endDate).toInt()
    val mMonth: Int = dateRoomMonth(viewModel.stateCashFLowUi.value.endDate).toInt() - 1
    val mDay: Int = dateRoomDay(viewModel.stateCashFLowUi.value.endDate).toInt()

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

private fun showBottomConfirm(
    context: Context, url: String,
    viewModel: CashFlowViewModel,
) {
    val bottomSheetDialog = BottomSheetDialog(context)
    bottomSheetDialog.setContentView(R.layout.bottom_sheet_confirm)
    val message = bottomSheetDialog.findViewById<TextView>(R.id.text_message)
    val buttonOk = bottomSheetDialog.findViewById<Button>(R.id.ok_button)

    val messageString =
        "Buka Browser untuk download File?"

    message?.text = messageString

    val baseUrl = "https://apix.juragankost.id"

    buttonOk?.setOnClickListener {
        bottomSheetDialog.dismiss()
        try {
            val webIntent =
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(baseUrl + url)
                )
            context.startActivity(webIntent)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(
                context,
                "Tidak Bisa Akses Tutorial",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }
    bottomSheetDialog.show()

    bottomSheetDialog.setOnDismissListener {
        viewModel.resetReport()
    }
}

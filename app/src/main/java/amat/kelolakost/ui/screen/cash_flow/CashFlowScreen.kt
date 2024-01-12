package amat.kelolakost.ui.screen.cash_flow

import amat.kelolakost.R
import amat.kelolakost.checkDateRangeValid
import amat.kelolakost.currencyFormatterStringViewZero
import amat.kelolakost.dateRoomDay
import amat.kelolakost.dateRoomMonth
import amat.kelolakost.dateRoomYear
import amat.kelolakost.dateToDisplayMidFormat
import amat.kelolakost.di.Injection
import amat.kelolakost.ui.common.OnLifecycleEvent
import amat.kelolakost.ui.common.UiState
import amat.kelolakost.ui.component.CashCard
import amat.kelolakost.ui.component.DateLayout
import amat.kelolakost.ui.theme.FontBlack
import amat.kelolakost.ui.theme.TealGreen
import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CashFlowScreen(
    modifier: Modifier = Modifier,
    context: Context,
) {
    val viewModel: CashFlowViewModel =
        viewModel(
            factory = CashFlowViewModelFactory(
                Injection.provideCashFlowRepository(context),
            )
        )

    OnLifecycleEvent { owner, event ->
        // do stuff on event
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                viewModel.getIncome()
                viewModel.getBalance()
                viewModel.getOutCome()
            }
            else -> { /* other stuff */
            }
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
                Text(text = "Saldo Saat Ini", style = TextStyle(fontSize = 12.sp))
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
        }
        Divider(
            color = TealGreen,
            thickness = 2.dp,
            modifier = Modifier.padding(top = 2.dp),
        )

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
                    text = currencyFormatterStringViewZero(uiState.data),
                    style = TextStyle(fontWeight = FontWeight.Medium)
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
                    nominal = currencyFormatterStringViewZero(uiState.data),
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
                    nominal = currencyFormatterStringViewZero(uiState.data),
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

package amat.laundrysederhana.ui.screen.cashflow

import amat.laundrysederhana.R
import amat.laundrysederhana.checkDateRangeValid
import amat.laundrysederhana.cleanPointZeroFloat
import amat.laundrysederhana.currencyFormatterStringViewZero
import amat.laundrysederhana.data.CashFlowAndCategory
import amat.laundrysederhana.dateRoomDay
import amat.laundrysederhana.dateRoomMonth
import amat.laundrysederhana.dateRoomYear
import amat.laundrysederhana.dateToDisplayMidFormat
import amat.laundrysederhana.dateUniversalToDisplay
import amat.laundrysederhana.di.Injection
import amat.laundrysederhana.ui.common.OnLifecycleEvent
import amat.laundrysederhana.ui.common.UiState
import amat.laundrysederhana.ui.component.CashFlowItem
import amat.laundrysederhana.ui.component.CenterLayout
import amat.laundrysederhana.ui.component.DateLayout
import amat.laundrysederhana.ui.component.ErrorLayout
import amat.laundrysederhana.ui.component.LoadingLayout
import amat.laundrysederhana.ui.theme.FontBlack
import amat.laundrysederhana.ui.theme.FontWhite
import amat.laundrysederhana.ui.theme.GreenDark
import amat.laundrysederhana.ui.theme.LaundryAppTheme
import amat.laundrysederhana.ui.theme.TealGreen
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

class CashFlowActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            LaundryAppTheme {
                CashFlowScreen(context)
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
fun CashFlowScreen(
    context: Context
) {

    val viewModel: CashFlowViewModel =
        viewModel(
            factory = CashFlowViewModelFactory(
                Injection.provideCashFlowRepository(context)
            )
        )

    OnLifecycleEvent { owner, event ->
        // do stuff on event
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                viewModel.getCashFlowAndCategory()
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
                    text = stringResource(id = R.string.title_cash_flow_out),
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
        )

        Column(modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)) {
            Text(
                text = "Range Tanggal Transaksi", style = TextStyle(
                    fontSize = 16.sp,
                    color = FontBlack,
                )
            )
            Spacer(Modifier.height(2.dp))
            viewModel.stateUi.collectAsState(initial = CashFlowUi()).value.let { value ->
                if (value.startDate.isNotEmpty() && value.endDate.isNotEmpty()) {
                    DateLayout(
                        value = "${dateToDisplayMidFormat(value.startDate)} - ${
                            dateToDisplayMidFormat(
                                value.endDate
                            )
                        }",
                        isEnable = true,
                        modifier = Modifier.clickable {
                            showDatePickerStart(
                                context,
                                viewModel
                            )
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

            viewModel.stateCashFlow.collectAsState(initial = UiState.Loading).value.let { uiState ->
                when (uiState) {
                    is UiState.Error -> {
                        ErrorLayout(errorMessage = uiState.errorMessage) {
                            viewModel.getCashFlowAndCategory()
                        }
                    }

                    UiState.Loading -> {
                        LoadingLayout()
                    }

                    is UiState.Success -> {
                        ListCashFlowView(
                            uiState.data,
                            onItemClick = { id ->
                                val intent = Intent(context, AddCashFlowActivity::class.java)
                                intent.putExtra("id", id)
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
fun ListCashFlowView(
    listData: List<CashFlowAndCategory>,
    onItemClick: (String) -> Unit
) {
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
                        onItemClick(data.cashFlowId)
                    },
                    categoryId = data.cashFlowCategoryId,
                    categoryName = data.cashFlowCategoryName,
                    unit = data.unit,
                    qty = cleanPointZeroFloat(data.qty),
                    note = data.note,
                    nominal = currencyFormatterStringViewZero(data.nominal),
                    createAt = dateUniversalToDisplay(data.createAt)
                )
            }
        }
    }
}

fun showDatePickerStart(context: Context, viewModel: CashFlowViewModel) {
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

fun showDatePickerEnd(context: Context, viewModel: CashFlowViewModel, dateStart: String) {
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
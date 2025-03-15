package amat.laundrysederhana.ui.screen.home

import amat.laundrysederhana.R
import amat.laundrysederhana.currencyFormatterStringViewZero
import amat.laundrysederhana.dateToDisplayMidFormat
import amat.laundrysederhana.dateToDisplayMonthYear
import amat.laundrysederhana.di.Injection
import amat.laundrysederhana.ui.common.OnLifecycleEvent
import amat.laundrysederhana.ui.common.UiState
import amat.laundrysederhana.ui.component.ErrorLayout
import amat.laundrysederhana.ui.component.HomeItem
import amat.laundrysederhana.ui.component.LoadingLayout
import amat.laundrysederhana.ui.screen.transaction.AddTransactionActivity
import amat.laundrysederhana.ui.theme.FontBlack
import amat.laundrysederhana.ui.theme.FontWhite
import amat.laundrysederhana.ui.theme.GreenDark
import amat.laundrysederhana.ui.theme.TealGreen
import android.content.Context
import android.content.Intent
import android.widget.Button
import android.widget.TextView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

@Composable
fun HomeScreen(
    context: Context,
    modifier: Modifier = Modifier
) {
    val viewModel: HomeViewModel =
        viewModel(
            factory = HomeViewModelFactory(
                Injection.provideUserRepository(context),
                Injection.provideCategoryRepository(context),
                Injection.provideDetailTransactionRepository(context)
            )
        )

    OnLifecycleEvent { owner, event ->
        // do stuff on event
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                viewModel.getUserInit()
            }

            Lifecycle.Event.ON_RESUME -> {
                viewModel.getDataTransaction()
            }

            else -> {}
        }

    }

    Column(modifier = modifier) {

        viewModel.stateUser.collectAsState(initial = UiState.Loading).value.let { uiState ->
            when (uiState) {
                is UiState.Error -> {

                }

                UiState.Loading -> {

                }

                is UiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .drawBehind {
                                        drawCircle(
                                            color = TealGreen,
                                            radius = this.size.maxDimension
                                        )
                                    },
                                text = uiState.data.businessName.substring(0, 1),
                                color = FontWhite,
                                style = TextStyle(fontSize = 24.sp)
                            )

                            Column {
                                Text(
                                    uiState.data.businessName,
                                    style = TextStyle(fontSize = 20.sp),
                                    color = FontBlack,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                                Text(
                                    uiState.data.address,
                                    style = TextStyle(fontSize = 18.sp),
                                    color = FontBlack,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }

                        }

                        Divider(
                            modifier = Modifier.padding(top = 8.dp),
                            color = TealGreen,
                            thickness = 2.dp
                        )

                    }
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            viewModel.stateList.collectAsState(initial = UiState.Loading).value.let { uiState ->
                when (uiState) {
                    is UiState.Error -> {
                        ErrorLayout(errorMessage = uiState.errorMessage) {
                            viewModel.getDataTransaction()
                        }
                    }

                    UiState.Loading -> {
                        LoadingLayout()
                    }

                    is UiState.Success -> {
                        ListTransactionView(
                            uiState.data, viewModel
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
fun ListTransactionView(data: HomeList, viewModel: HomeViewModel) {

    LazyVerticalGrid(
        contentPadding = PaddingValues(bottom = 64.dp, top = 8.dp, start = 8.dp, end = 8.dp),
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item(span = { GridItemSpan(2) }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    "Transaksi Hari Ini", style = TextStyle(
                        fontSize = 16.sp,
                        color = FontBlack,
                    )
                )
                Text(
                    dateToDisplayMidFormat(viewModel.stateUi.collectAsState().value.currentDate),
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = FontBlack,
                    )
                )
            }

        }
        items(data.listToday) { item ->
            HomeItem(
                categoryName = item.categoryName,
                categoryUnit = item.categoryUnit,
                totalQty = item.totalQty,
                totalPrice = currencyFormatterStringViewZero(item.totalPrice)
            )
        }
        item(span = { GridItemSpan(2) }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    "Transaksi Bulan Ini", style = TextStyle(
                        fontSize = 16.sp,
                        color = FontBlack,
                    )
                )
                Text(
                    dateToDisplayMonthYear(viewModel.stateUi.collectAsState().value.startDateMonth),
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = FontBlack,
                    )
                )
            }
        }
        items(data.listMonth) { item ->
            HomeItem(
                categoryName = item.categoryName,
                categoryUnit = item.categoryUnit,
                totalQty = item.totalQty,
                totalPrice = currencyFormatterStringViewZero(item.totalPrice)
            )
        }
    }
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
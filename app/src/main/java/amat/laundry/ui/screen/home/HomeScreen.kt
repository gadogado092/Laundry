package amat.laundry.ui.screen.home

import amat.laundry.R
import amat.laundry.currencyFormatterStringViewZero
import amat.laundry.dateToDisplayMidFormat
import amat.laundry.dateToDisplayMonthYear
import amat.laundry.di.Injection
import amat.laundry.ui.common.OnLifecycleEvent
import amat.laundry.ui.common.UiState
import amat.laundry.ui.component.ErrorLayout
import amat.laundry.ui.component.HomeItem
import amat.laundry.ui.component.HomeItemSmall
import amat.laundry.ui.component.LoadingLayout
import amat.laundry.ui.screen.transaction.AddTransactionActivity
import amat.laundry.ui.theme.BGCashFlow
import amat.laundry.ui.theme.BackgroundGrey
import amat.laundry.ui.theme.Blue
import amat.laundry.ui.theme.FontBlack
import amat.laundry.ui.theme.FontGrey
import amat.laundry.ui.theme.FontWhite
import amat.laundry.ui.theme.GreenDark
import amat.laundry.ui.theme.GreyLight
import amat.laundry.ui.theme.GreyLight2
import amat.laundry.ui.theme.TealGreen
import android.content.Context
import android.content.Intent
import android.widget.Button
import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
                Injection.provideDetailTransactionRepository(context),
                Injection.provideCashFlowCategoryRepository(context),
                Injection.provideCashFlowRepository(context)
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundGrey)
    ) {

        viewModel.stateUser.collectAsState(initial = UiState.Loading).value.let { uiState ->
            when (uiState) {
                is UiState.Error -> {

                }

                UiState.Loading -> {

                }

                is UiState.Success -> {

                    Row(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
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
                                            color = FontWhite,
                                            radius = this.size.maxDimension
                                        )
                                    },
                                text = uiState.data.businessName.substring(0, 1),
                                color = FontBlack,
                                style = TextStyle(fontSize = 24.sp)
                            )

                            Column(Modifier.padding(horizontal = 4.dp)) {
                                Text(
                                    uiState.data.businessName,
                                    style = TextStyle(fontSize = 16.sp),
                                    color = FontBlack,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                                Text(
                                    uiState.data.address,
                                    style = TextStyle(fontSize = 14.sp),
                                    color = FontGrey,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                                Text(
                                    uiState.data.phoneNumber,
                                    style = TextStyle(fontSize = 14.sp),
                                    color = FontGrey,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }

                        }

//                            Text(
//                                dateToDisplayMidFormat(viewModel.stateUi.collectAsState().value.currentDate),
//                                style = TextStyle(
//                                    fontSize = 16.sp,
//                                    color = FontBlack,
//                                ),
//                                modifier = Modifier.padding(bottom = 2.dp, end = 8.dp)
//                            )

                    }


                }
            }
        }

        Column() {
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                backgroundColor = Blue
            ) {
                Column {
                    Text("Saldo")
                    Text("Saldo")
                }
            }
        }

//        Box(
//            modifier = Modifier.fillMaxSize()
//        ) {
//            viewModel.stateList.collectAsState(initial = UiState.Loading).value.let { uiState ->
//                when (uiState) {
//                    is UiState.Error -> {
//                        ErrorLayout(errorMessage = uiState.errorMessage) {
//                            viewModel.getDataTransaction()
//                        }
//                    }
//
//                    UiState.Loading -> {
//                        LoadingLayout()
//                    }
//
//                    is UiState.Success -> {
//                        ListTransactionView(
//                            uiState.data, viewModel
//                        )
//                    }
//                }
//            }
//
//            FloatingActionButton(
//                onClick = {
//                    if (viewModel.checkLimitApp()) {
//                        showBottomLimitApp(context)
//                    } else {
//                        val intent = Intent(context, AddTransactionActivity::class.java)
//                        context.startActivity(intent)
//                    }
//                },
//                modifier = Modifier
//                    .align(Alignment.BottomEnd)
//                    .padding(8.dp),
//                backgroundColor = GreenDark
//            ) {
//                Icon(
//                    Icons.Filled.Add,
//                    "",
//                    modifier = Modifier.size(30.dp),
//                    tint = Color.White,
//                )
//            }
//        }
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 4.dp),
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(color = Blue)
                    )
                    Text(
                        "Transaksi Hari Ini", style = TextStyle(
                            fontSize = 16.sp,
                            color = FontBlack,
                        )
                    )
                }
                Text(
                    currencyFormatterStringViewZero(viewModel.stateUi.collectAsState().value.totalTransactionToday),
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = FontBlack,
                        fontWeight = FontWeight.Medium
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 4.dp),
                        imageVector = Icons.Default.RemoveCircle,
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(color = BGCashFlow)
                    )
                    Text(
                        "Pengeluaran Hari Ini", style = TextStyle(
                            fontSize = 16.sp,
                            color = FontBlack,
                        )
                    )
                }
                Text(
                    currencyFormatterStringViewZero(viewModel.stateUi.collectAsState().value.totalCashFlowToday),
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = FontBlack,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
        items(data.listCashFlowToday) { item ->
            HomeItemSmall(
                categoryName = item.categoryName,
                categoryUnit = item.categoryUnit,
                totalQty = item.totalQty,
                totalPrice = currencyFormatterStringViewZero(item.totalPrice)
            )
        }
        item(span = { GridItemSpan(2) }) {
            Divider(
                color = GreyLight,
                thickness = 2.dp
            )
        }
        item(span = { GridItemSpan(2) }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 4.dp),
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(color = Blue)
                    )
                    Text(
                        "Transaksi Bulan ${dateToDisplayMonthYear(viewModel.stateUi.collectAsState().value.startDateMonth)}",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = FontBlack,
                        )
                    )
                }
                Text(
                    currencyFormatterStringViewZero(viewModel.stateUi.collectAsState().value.totalTransactionMonth),
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = FontBlack,
                        fontWeight = FontWeight.Medium
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
        item(span = { GridItemSpan(2) }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 4.dp),
                        imageVector = Icons.Default.RemoveCircle,
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(color = BGCashFlow)
                    )
                    Text(
                        "Pengeluaran ${dateToDisplayMonthYear(viewModel.stateUi.collectAsState().value.currentDate)}",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = FontBlack,
                        )
                    )
                }

                Text(
                    currencyFormatterStringViewZero(viewModel.stateUi.collectAsState().value.totalCashFlowMonth),
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = FontBlack,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
        items(data.listCashFlowMonth) { item ->
            HomeItemSmall(
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
package amat.laundry.ui.screen.home

import amat.laundry.R
import amat.laundry.currencyFormatterStringViewZero
import amat.laundry.dateToDisplayMidFormat
import amat.laundry.dateToDisplayMonthYear
import amat.laundry.di.Injection
import amat.laundry.sendWhatsApp
import amat.laundry.ui.common.OnLifecycleEvent
import amat.laundry.ui.common.UiState
import amat.laundry.ui.component.HomeItem
import amat.laundry.ui.component.HomeItemSmall
import amat.laundry.ui.screen.cashflow.CashFlowActivity
import amat.laundry.ui.screen.cashflowcategory.CashFlowCategoryActivity
import amat.laundry.ui.screen.cashier.CashierActivity
import amat.laundry.ui.screen.category.CategoryActivity
import amat.laundry.ui.screen.customer.AddCustomerActivity
import amat.laundry.ui.screen.customer.SearchCustomerActivity
import amat.laundry.ui.screen.printer.PrinterActivity
import amat.laundry.ui.screen.product.ProductActivity
import amat.laundry.ui.screen.transaction.AddTransactionActivity
import amat.laundry.ui.screen.transaction.SearchTransactionActivity
import amat.laundry.ui.screen.user.UpdateUserActivity
import amat.laundry.ui.theme.BGCashFlow
import amat.laundry.ui.theme.BackgroundGrey
import amat.laundry.ui.theme.Blue
import amat.laundry.ui.theme.ColorIncome
import amat.laundry.ui.theme.ColorRed
import amat.laundry.ui.theme.FontBlack
import amat.laundry.ui.theme.FontBlackSoft
import amat.laundry.ui.theme.FontGrey
import amat.laundry.ui.theme.FontWhite
import amat.laundry.ui.theme.GreenDark
import amat.laundry.ui.theme.GreyLight
import amat.laundry.ui.theme.GreyLight2
import amat.laundry.ui.theme.GreyLight3
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.widget.Button
import android.widget.TextView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.material.icons.filled.ArrowCircleUp
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropDownCircle
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Person4
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.filled.Wash
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
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
                viewModel.refresh()
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
                                    color = FontBlack,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                                Text(
                                    uiState.data.phoneNumber,
                                    style = TextStyle(fontSize = 14.sp),
                                    color = FontBlack,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }

                        }

                    }


                }
            }
        }

        Column {
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                backgroundColor = FontWhite,
                border = BorderStroke(1.dp, GreyLight)
            ) {
                Column(modifier = Modifier.padding(bottom = 8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Keuangan Hari Ini",
                            style = TextStyle(fontSize = 16.sp),
                            color = FontBlack,
                            modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)
                        )
                        Text(
                            dateToDisplayMidFormat(viewModel.stateUi.collectAsState().value.currentDate),
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = FontGrey,
                            ),
                            modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TopInfo(
                            modifier = Modifier.weight(1F),
                            "Masuk",
                            currencyFormatterStringViewZero(viewModel.stateUi.collectAsState().value.totalCashInDay),
                            Icons.Default.ArrowCircleDown,
                            ColorIncome
                        )
                        TopInfo(
                            modifier = Modifier.weight(1F),
                            "Keluar",
                            currencyFormatterStringViewZero(viewModel.stateUi.collectAsState().value.totalCashOutDay),
                            Icons.Default.ArrowCircleUp,
                            ColorRed
                        )
                        TopInfo(
                            modifier = Modifier.weight(1F),
                            "Saldo",
                            currencyFormatterStringViewZero(viewModel.stateUi.collectAsState().value.totalBalance),
                            Icons.Default.Wallet,
                            GreenDark
                        )
                    }

                    Text(
                        "Bulan Ini",
                        style = TextStyle(fontSize = 16.sp),
                        color = FontBlack,
                        modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TopInfo(
                            modifier = Modifier.weight(1F),
                            "Masuk",
                            currencyFormatterStringViewZero(viewModel.stateUi.collectAsState().value.totalCashInMonth),
                            Icons.Default.ArrowCircleDown,
                            ColorIncome
                        )
                        TopInfo(
                            modifier = Modifier.weight(1F),
                            "Keluar",
                            currencyFormatterStringViewZero(viewModel.stateUi.collectAsState().value.totalCashOutMonth),
                            Icons.Default.ArrowCircleUp,
                            ColorRed
                        )
                        Image(
                            modifier = Modifier
                                .weight(1F)
                                .height(32.dp)
                                .clickable {
                                    viewModel.refresh()
                                },
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(FontBlackSoft)
                        )
                    }
                }
            }


            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                HomeMenu(
                    modifier = Modifier.clickable {
                        val intent = Intent(context, AddTransactionActivity::class.java)
                        context.startActivity(intent)
                    },
                    "Tambah",
                    "Transaksi",
                    Icons.Default.Add,
                    GreenDark
                )
                HomeMenu(
                    modifier = Modifier.clickable {
                        val intent = Intent(context, SearchTransactionActivity::class.java)
                        context.startActivity(intent)
                    },
                    "Cari",
                    "Transaksi",
                    Icons.Default.Search,
                    GreenDark
                )
                HomeMenu(
                    modifier = Modifier.clickable {
                        val intent = Intent(context, AddCustomerActivity::class.java)
                        context.startActivity(intent)
                    },
                    "Tambah",
                    "Customer",
                    Icons.Default.PersonAdd,
                    GreenDark
                )
                HomeMenu(
                    modifier = Modifier.clickable {
                        val intent = Intent(context, SearchCustomerActivity::class.java)
                        context.startActivity(intent)
                    },
                    "Cari",
                    "Customer",
                    Icons.Default.PersonSearch,
                    GreenDark
                )
            }

            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                HomeMenu(
                    modifier = Modifier.clickable {
                        val intent = Intent(context, ProductActivity::class.java)
                        context.startActivity(intent)
                    },
                    "Produk",
                    "",
                    Icons.Default.Wash,
                    GreenDark
                )
                HomeMenu(
                    modifier = Modifier.clickable {
                        val intent = Intent(context, CashierActivity::class.java)
                        context.startActivity(intent)
                    },
                    "Kasir",
                    "",
                    Icons.Default.SupportAgent,
                    GreenDark
                )
                HomeMenu(
                    modifier = Modifier.clickable {
                        val intent = Intent(context, PrinterActivity::class.java)
                        context.startActivity(intent)
                    },
                    "Printer",
                    "",
                    Icons.Default.Print,
                    GreenDark
                )
                HomeMenu(
                    modifier = Modifier.clickable {
                        val intent = Intent(context, UpdateUserActivity::class.java)
                        context.startActivity(intent)
                    },
                    "Profil",
                    "",
                    Icons.Default.ManageAccounts,
                    GreenDark
                )
            }

            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                HomeMenu(
                    modifier = Modifier.clickable {
                        val intent = Intent(context, CashFlowActivity::class.java)
                        context.startActivity(intent)
                    },
                    "Alur",
                    "Kas",
                    Icons.Default.AccountBalanceWallet,
                    GreenDark
                )
                HomeMenu(
                    modifier = Modifier.clickable {
                        val intent = Intent(context, CashFlowCategoryActivity::class.java)
                        context.startActivity(intent)
                    },
                    "Kategori",
                    "Kas",
                    Icons.Default.Category,
                    GreenDark
                )
                HomeMenu(
                    modifier = Modifier.clickable {
                        val intent = Intent(context, CategoryActivity::class.java)
                        context.startActivity(intent)
                    },
                    "Kategori",
                    "Layanan",
                    Icons.Default.Category,
                    GreenDark
                )
                val numberCs = stringResource(R.string.number_cs)
                val messageCs =
                    stringResource(R.string.message_cs, stringResource(R.string.app_name))
                HomeMenu(
                    modifier = Modifier.clickable {
                        sendWhatsApp(
                            context,
                            numberCs,
                            messageCs,
                            typeWa = viewModel.getTypeWa(),
                        )
                    },
                    "Customer",
                    "Service",
                    Icons.Default.Call,
                    GreenDark
                )
            }

            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                backgroundColor = FontWhite,
                border = BorderStroke(1.dp, GreyLight)
            ) {
                Column(modifier = Modifier.padding(bottom = 8.dp)) {
                    Text(
                        "Status Laundry",
                        style = TextStyle(fontSize = 16.sp),
                        color = FontBlack,
                        modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TopInfo(
                            modifier = Modifier.weight(1F),
                            "Siap Ambil",
                            "50",
                            Icons.Default.ShoppingCartCheckout,
                            GreenDark
                        )
                        TopInfo(
                            modifier = Modifier.weight(1F),
                            "Deadline",
                            "222",
                            Icons.Default.Today,
                            ColorIncome
                        )
                        TopInfo(
                            modifier = Modifier.weight(1F),
                            "Terlambat",
                            "222",
                            Icons.Default.Timer,
                            ColorIncome
                        )
                    }
                }
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

@Composable
fun TopInfo(
    modifier: Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color
) {
    Row(
        modifier = modifier.padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            imageVector = icon,
            contentDescription = "",
            colorFilter = ColorFilter.tint(iconColor)
        )
        Column(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                title,
                style = TextStyle(fontSize = 14.sp),
                color = FontGrey,
            )
            Text(
                value,
                style = TextStyle(fontSize = 16.sp),
                color = FontBlack,
            )
        }
    }
}

@Composable
fun HomeMenu(
    modifier: Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color
) {
    Column(
        modifier = modifier.padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .padding(8.dp),
            backgroundColor = FontWhite,
            border = BorderStroke(1.dp, GreyLight)
        ) {
            Image(
                modifier = Modifier
                    .padding(8.dp),
                imageVector = icon,
                contentDescription = "",
                colorFilter = ColorFilter.tint(iconColor)
            )
        }
        Text(
            title,
            style = TextStyle(fontSize = 14.sp),
            color = FontGrey,
        )
        if (subtitle.isNotEmpty()) {
            Text(
                subtitle,
                style = TextStyle(fontSize = 14.sp),
                color = FontGrey,
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
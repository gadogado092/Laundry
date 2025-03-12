package amat.laundrysederhana.ui.screen.transaction

import amat.laundrysederhana.R
import amat.laundrysederhana.currencyFormatterStringViewZero
import amat.laundrysederhana.data.Category
import amat.laundrysederhana.data.ProductCart
import amat.laundrysederhana.di.Injection
import amat.laundrysederhana.ui.common.OnLifecycleEvent
import amat.laundrysederhana.ui.common.UiState
import amat.laundrysederhana.ui.component.CenterLayout
import amat.laundrysederhana.ui.component.ErrorLayout
import amat.laundrysederhana.ui.component.FilterItem
import amat.laundrysederhana.ui.component.LoadingLayout
import amat.laundrysederhana.ui.component.ProductCartItem
import amat.laundrysederhana.ui.theme.Blue
import amat.laundrysederhana.ui.theme.ErrorColor
import amat.laundrysederhana.ui.theme.FontBlack
import amat.laundrysederhana.ui.theme.FontWhite
import amat.laundrysederhana.ui.theme.GreenDark
import amat.laundrysederhana.ui.theme.LaundryAppTheme
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

class AddTransactionActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            LaundryAppTheme {
                AddTransactionScreen(context = context)
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
fun AddTransactionScreen(
    modifier: Modifier = Modifier,
    context: Context
) {

    val viewModel: AddTransactionViewModel =
        viewModel(
            factory = AddTransactionViewModelFactory(
                Injection.provideProductRepository(context),
                Injection.provideCategoryRepository(context),
                Injection.provideCartRepository(context)
            )
        )

    OnLifecycleEvent { owner, event ->
        // do stuff on event
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                viewModel.getProduct()
                viewModel.getTotalService()
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
                    text = stringResource(id = R.string.title_add_transaction),
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
            actions = {
                IconButton(
                    onClick = {
                        viewModel.deleteAllCart()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "",
                        tint = ErrorColor
                    )
                }
            }
        )

        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1F)
                    .fillMaxWidth()
            ) {

                ContentCategory(viewModel)

                viewModel.stateListProductCart.collectAsState(initial = UiState.Loading).value.let { uiState ->
                    when (uiState) {
                        is UiState.Error -> {
                            ErrorLayout(errorMessage = uiState.errorMessage) {
                                viewModel.getProduct()
                                viewModel.getTotalService()
                            }
                        }

                        UiState.Loading -> {
                            LoadingLayout()
                        }

                        is UiState.Success -> {
                            ListProductView(
                                listData = uiState.data,
                                onItemClick = { productId ->
                                    val intent = Intent(context, AddCartActivity::class.java)
                                    intent.putExtra("id", productId)
                                    context.startActivity(intent)
                                },
                                onClickDelete = { productId ->
                                    viewModel.delete(productId)
                                }
                            )
                        }

                    }

                }
            }

            //bottom area
            Card(
                modifier = Modifier
                    .padding(4.dp, 8.dp)
                    .fillMaxWidth()
                    .clickable {
                        val activity = (context as? Activity)
                        activity?.finish()
                        val intent = Intent(context, PaymentActivity::class.java)
                        context.startActivity(intent)
                    },
                colors = CardDefaults.cardColors(Blue),
                border = BorderStroke(0.5.dp, Blue),
            ) {
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    viewModel.stateUiCart.collectAsState(initial = UiState.Loading).value.let { uiState ->
                        when (uiState) {
                            is UiState.Error -> {
                                Text(
                                    "Error ${uiState.errorMessage}",
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            }

                            UiState.Loading -> {
                                Text("Loading", color = Color.White, fontSize = 16.sp)
                            }

                            is UiState.Success -> {
                                Text(
                                    "${uiState.data.totalService} Layanan",
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                                Row {
                                    Text(
                                        uiState.data.totalPrice,
                                        color = Color.White,
                                        fontSize = 16.sp
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Image(
                                        imageVector = Icons.Default.ShoppingBasket,
                                        contentDescription = "",
                                        colorFilter = ColorFilter.tint(
                                            Color.White
                                        ),
                                        modifier = Modifier
                                            .size(20.dp)
                                            .padding(bottom = 4.dp)
                                    )
                                }
                            }

                        }

                    }
                }
            }
        }
    }
}

@Composable
fun ContentCategory(viewModel: AddTransactionViewModel) {
    val categorySelected =
        viewModel.categorySelected.collectAsState(initial = Category("", "", "", false))
    viewModel.stateListCategory.collectAsState(initial = UiState.Loading).value.let { uiState ->
        when (uiState) {
            is UiState.Error -> {
                FilterItem(
                    title = uiState.errorMessage,
                    isSelected = false,
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                )
            }

            UiState.Loading -> {
                FilterItem(
                    title = "Loading",
                    isSelected = false,
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                )
            }

            is UiState.Success -> {
                val listStatus: List<Category> = uiState.data
                LazyRow(contentPadding = PaddingValues(vertical = 4.dp)) {
                    items(listStatus, key = { it.name }) { item ->
                        FilterItem(
                            title = item.name,
                            isSelected = item.id == categorySelected.value.id,
                            modifier = Modifier
                                .padding(horizontal = 4.dp, vertical = 4.dp)
                                .clickable {
                                    viewModel.updateCategorySelected(item)
                                }
                        )
                    }
                }
            }

        }
    }
}

@Composable
fun ListProductView(
    listData: List<ProductCart>,
    onItemClick: (String) -> Unit,
    onClickDelete: (String) -> Unit
) {
    if (listData.isEmpty()) {
        CenterLayout(
            content = {
                Text(
                    text = stringResource(
                        id = R.string.note_empty_data,
                        "Layanan atau Produk"
                    ),
                    color = FontBlack
                )
            }
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 64.dp)
        ) {
            items(listData) { data ->
                ProductCartItem(
                    modifier = Modifier.clickable {
                        onItemClick(data.productId)
                    },
                    productId = data.productId,
                    productName = data.productName,
                    productPrice = currencyFormatterStringViewZero(data.productPrice.toString()),
                    categoryName = data.categoryName,
                    qty = data.qty,
                    unit = data.unit,
                    note = data.note,
                    onClickDelete = onClickDelete
                )
            }
        }
    }

}


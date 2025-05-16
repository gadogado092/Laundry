package amat.laundry.ui.screen.customer

import amat.laundry.R
import amat.laundry.data.Customer
import amat.laundry.di.Injection
import amat.laundry.ui.common.OnLifecycleEvent
import amat.laundry.ui.common.UiState
import amat.laundry.ui.component.CenterLayout
import amat.laundry.ui.component.CustomSearchView
import amat.laundry.ui.component.CustomerItem
import amat.laundry.ui.component.LoadingLayout
import amat.laundry.ui.theme.FontBlack
import amat.laundry.ui.theme.GreenDark
import amat.laundry.ui.theme.LaundryAppTheme
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

class SearchCustomerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            LaundryAppTheme {
                SearchCustomerScreen(context)
                setResult(1)
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
fun SearchCustomerScreen(
    context: Context
) {

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val viewModel: SearchCustomerViewModel =
        viewModel(
            factory = SearchCustomerViewModelFactory(
                Injection.provideCustomerRepository(context)
            )
        )

    OnLifecycleEvent { owner, event ->
        // do stuff on event
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                viewModel.setSearch("")
            }

            Lifecycle.Event.ON_CREATE -> {
                focusRequester.requestFocus()
            }

            else -> { /* other stuff */
            }
        }
    }

    //START UI
    Column {
        CustomSearchView(
            modifierTextField = Modifier
                .focusRequester(focusRequester)
                .onFocusChanged {
                    if (it.isFocused) {
                        keyboardController?.show()
                    }
                },
            placeHolderText = "Nama atau Nomor",
            search = viewModel.searchValue.collectAsState().value,
            onValueChange = {
                viewModel.setSearch(it)
            },
            onClickBack = {
                val activity = (context as? Activity)
                activity?.finish()
            })

        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            viewModel.stateCustomer.collectAsState(initial = UiState.Loading).value.let { uiState ->
                when (uiState) {
                    is UiState.Error -> {
                        CenterLayout(
                            content = {
                                Text(
                                    text = uiState.errorMessage,
                                    color = FontBlack
                                )
                            }
                        )
                    }

                    UiState.Loading -> {
                        LoadingLayout(modifier = Modifier.fillMaxHeight())
                    }

                    is UiState.Success -> {
                        ListCustomerView(uiState.data, onItemClick = { id, name, isDelete ->
                            val intent = Intent(context, AddCustomerActivity::class.java)
                            intent.putExtra("id", id)
                            context.startActivity(intent)
                        })
                    }

                }
            }

            FloatingActionButton(
                onClick = {
                    val intent = Intent(context, AddCustomerActivity::class.java)
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
fun ListCustomerView(
    listData: List<Customer>,
    onItemClick: (String, String, Boolean) -> Unit
) {
    if (listData.isEmpty()) {
        CenterLayout(
            content = {
                Text(
                    text = stringResource(
                        id = R.string.note_empty_data,
                        "Customer"
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
                CustomerItem(
                    Modifier.clickable {
                        onItemClick(data.id, data.name, data.isDelete)
                    }, name = data.name, numberPhone = data.phoneNumber, note = data.note
                )
            }
        }
    }
}
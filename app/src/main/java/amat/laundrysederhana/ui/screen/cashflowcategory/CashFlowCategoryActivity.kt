package amat.laundrysederhana.ui.screen.cashflowcategory

import amat.laundrysederhana.R
import amat.laundrysederhana.data.CashFlowCategory
import amat.laundrysederhana.di.Injection
import amat.laundrysederhana.ui.common.OnLifecycleEvent
import amat.laundrysederhana.ui.common.UiState
import amat.laundrysederhana.ui.component.CashFlowCategoryItem
import amat.laundrysederhana.ui.component.CenterLayout
import amat.laundrysederhana.ui.component.ErrorLayout
import amat.laundrysederhana.ui.component.LoadingLayout
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

class CashFlowCategoryActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            LaundryAppTheme {
                CashFlowCategoryScreen(context)
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
fun CashFlowCategoryScreen(
    context: Context
) {

    val viewModel: CashFlowCategoryViewModel =
        viewModel(
            factory = CashFlowCategoryViewModelFactory(
                Injection.provideCashFlowCategoryRepository(context)
            )
        )

    OnLifecycleEvent { owner, event ->
        // do stuff on event
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                viewModel.getCategory()
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
                    text = stringResource(id = R.string.title_cash_flow_category),
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

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            viewModel.stateCategory.collectAsState(initial = UiState.Loading).value.let { uiState ->
                when (uiState) {
                    is UiState.Error -> {
                        ErrorLayout(
                            modifier = Modifier.fillMaxHeight(),
                            errorMessage = uiState.errorMessage
                        ) {
                            viewModel.getCategory()
                        }
                    }

                    UiState.Loading -> {
                        LoadingLayout(modifier = Modifier.fillMaxHeight())
                    }

                    is UiState.Success -> {
                        ListCashFlowCategoryView(uiState.data, onItemClick = { id ->
                            val intent = Intent(context, AddCashFlowCategoryActivity::class.java)
                            intent.putExtra("id", id)
                            context.startActivity(intent)
                        })
                    }

                }
            }

            FloatingActionButton(
                onClick = {
                    val intent = Intent(context, AddCashFlowCategoryActivity::class.java)
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
fun ListCashFlowCategoryView(
    listData: List<CashFlowCategory>,
    onItemClick: (String) -> Unit
) {
    if (listData.isEmpty()) {
        CenterLayout(
            content = {
                Text(
                    text = stringResource(
                        id = R.string.note_empty_data,
                        "Kategori Alur Kas"
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
                CashFlowCategoryItem(
                    Modifier.clickable {
                        onItemClick(data.id)
                    }, name = data.name
                )
            }
        }
    }
}
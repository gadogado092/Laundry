package amat.kelolakost.ui.screen.credit_debit

import amat.kelolakost.R
import amat.kelolakost.currencyFormatterStringViewZero
import amat.kelolakost.data.CashFlow
import amat.kelolakost.dateToDisplayMidFormat
import amat.kelolakost.di.Injection
import amat.kelolakost.ui.common.OnLifecycleEvent
import amat.kelolakost.ui.common.UiState
import amat.kelolakost.ui.component.CashFlowItem
import amat.kelolakost.ui.component.CenterLayout
import amat.kelolakost.ui.component.ErrorLayout
import amat.kelolakost.ui.component.LoadingLayout
import amat.kelolakost.ui.theme.FontBlack
import amat.kelolakost.ui.theme.FontWhite
import amat.kelolakost.ui.theme.GreenDark
import amat.kelolakost.ui.theme.KelolaKostTheme
import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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

class HistoryCreditDebitActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        val creditDebitId = intent.getStringExtra("creditDebitId")

        setContent {
            val context = LocalContext.current
            KelolaKostTheme {
                HistoryCreditDebitScreen(context = context, creditDebitId = creditDebitId)
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
fun HistoryCreditDebitScreen(
    modifier: Modifier = Modifier,
    context: Context,
    creditDebitId: String?,
) {

    val myViewModel: HistoryCreditDebitViewModel =
        viewModel(
            factory = HistoryCreditDebitViewModelFactory(
                Injection.provideCashFlowRepository(
                    context
                )
            )
        )

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                if (creditDebitId != null) {
                    myViewModel.getCreditDebitHistory(creditDebitId)
                }
            }

            else -> {

            }

        }
    }

    //START UI
    Column(modifier = modifier) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.title_history),
                    color = FontWhite,
                    fontSize = 20.sp
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
            }
        )

        myViewModel.stateListCashFlow.collectAsState(initial = UiState.Loading).value.let { uiState ->
            when (uiState) {
                is UiState.Error -> {
                    ErrorLayout(errorMessage = uiState.errorMessage) {
                        if (creditDebitId != null) {
                            myViewModel.getCreditDebitHistory(creditDebitId)
                        }
                    }
                }

                UiState.Loading -> LoadingLayout()
                is UiState.Success -> {
                    ListHistory(uiState.data)
                }
            }

        }

    }
}

@Composable
fun ListHistory(data: List<CashFlow>) {
    if (data.isEmpty()) {
        CenterLayout(
            content = {
                Text(
                    text = stringResource(
                        id = R.string.note_empty_data,
                        "Riwayat"
                    ),
                    color = FontBlack
                )
            }
        )
    } else {
        LazyColumn(contentPadding = PaddingValues(bottom = 8.dp)) {
            items(data) { item ->
                CashFlowItem(
                    nominal = currencyFormatterStringViewZero(item.nominal),
                    typePayment = item.typePayment,
                    createAt = dateToDisplayMidFormat(item.createAt),
                    type = item.type,
                    note = item.note
                )
            }
        }
    }
}
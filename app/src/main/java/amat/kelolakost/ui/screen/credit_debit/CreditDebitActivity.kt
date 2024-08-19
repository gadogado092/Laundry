package amat.kelolakost.ui.screen.credit_debit

import amat.kelolakost.R
import amat.kelolakost.currencyFormatterStringViewZero
import amat.kelolakost.data.CreditDebitHome
import amat.kelolakost.dateToDisplayMidFormat
import amat.kelolakost.di.Injection
import amat.kelolakost.ui.common.OnLifecycleEvent
import amat.kelolakost.ui.common.UiState
import amat.kelolakost.ui.component.CenterLayout
import amat.kelolakost.ui.component.CreditDebitItem
import amat.kelolakost.ui.component.ErrorLayout
import amat.kelolakost.ui.component.LoadingLayout
import amat.kelolakost.ui.theme.FontBlack
import amat.kelolakost.ui.theme.FontWhite
import amat.kelolakost.ui.theme.GreenDark
import amat.kelolakost.ui.theme.KelolaKostTheme
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Text
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
import com.google.android.material.bottomsheet.BottomSheetDialog

class CreditDebitActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            KelolaKostTheme {
                CreditDebitScreen(context = context)
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
fun CreditDebitScreen(
    modifier: Modifier = Modifier,
    context: Context
) {

    val myViewModel: CreditDebitViewModel =
        viewModel(
            factory = CreditDebitViewModelFactory(
                Injection.provideCreditDebitRepository(context)
            )
        )

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                myViewModel.getAllCreditDebit()
            }

            else -> {

            }
        }
    }

    if (!myViewModel.isProsesDeleteSuccess.collectAsState().value.isError) {
        Toast.makeText(
            context,
            stringResource(id = R.string.success_delete_payment_credit_debit),
            Toast.LENGTH_SHORT
        )
            .show()
        myViewModel.getAllCreditDebit()
    } else {
        if (myViewModel.isProsesDeleteSuccess.collectAsState().value.errorMessage.isNotEmpty()) {
            Toast.makeText(
                context,
                myViewModel.isProsesDeleteSuccess.collectAsState().value.errorMessage,
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    //START UI
    Column(modifier = modifier) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.title_debt_credit),
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
            }
        )
        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            myViewModel.stateListCreditDebit.collectAsState(initial = UiState.Loading).value.let { uiState ->
                when (uiState) {
                    is UiState.Error -> {
                        ErrorLayout(errorMessage = uiState.errorMessage) {
                            myViewModel.getAllCreditDebit()
                        }
                    }

                    UiState.Loading -> {
                        LoadingLayout()
                    }

                    is UiState.Success -> {
                        ListCreditDebit(uiState.data,
                            onClickHistory = { creditDebitId ->
                                val intent = Intent(context, HistoryCreditDebitActivity::class.java)
                                intent.putExtra("creditDebitId", creditDebitId)
                                context.startActivity(intent)
                            },
                            onClickPay = { creditDebitId ->
                                val intent = Intent(context, PaymentCreditDebitActivity::class.java)
                                intent.putExtra("creditDebitId", creditDebitId)
                                context.startActivity(intent)
                            },
                            onClickRemove = { creditDebitId ->
                                showBottomConfirm(
                                    context,
                                    myViewModel,
                                    creditDebitId
                                )
                            })
                    }
                }
            }

            FloatingActionButton(
                onClick = {
                    val intent = Intent(context, AddCreditDebitActivity::class.java)
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
fun ListCreditDebit(
    data: List<CreditDebitHome>,
    onClickHistory: (String) -> Unit,
    onClickPay: (String) -> Unit,
    onClickRemove: (String) -> Unit
) {
    if (data.isEmpty()) {
        CenterLayout(
            content = {
                Text(
                    text = stringResource(
                        id = R.string.note_empty_data,
                        "Hutang Piutang"
                    ), color = FontBlack
                )
            }
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 64.dp)
        ) {
            items(data) { item ->
                CreditDebitItem(
                    creditDebitId = item.creditDebitId,
                    creditDebitName = item.customerCreditDebitName,
                    remaining = currencyFormatterStringViewZero(item.remaining.toString()),
                    dueDate = dateToDisplayMidFormat(item.dueDate),
                    status = item.status,
                    onClickHistory = onClickHistory,
                    onClickPay = onClickPay,
                    onClickRemove = onClickRemove
                )
            }
        }
    }
}

private fun showBottomConfirm(
    context: Context,
    creditDebitViewModel: CreditDebitViewModel,
    creditDebitId: String,
) {
    val bottomSheetDialog = BottomSheetDialog(context)
    bottomSheetDialog.setContentView(R.layout.bottom_sheet_confirm)
    val message = bottomSheetDialog.findViewById<TextView>(R.id.text_message)
    val buttonOk = bottomSheetDialog.findViewById<Button>(R.id.ok_button)

    val messageString =
        "Hapus data Hutang atau Piutang?"

    message?.text = messageString

    buttonOk?.setOnClickListener {
        bottomSheetDialog.dismiss()
        creditDebitViewModel.delete(creditDebitId)
    }
    bottomSheetDialog.show()

}
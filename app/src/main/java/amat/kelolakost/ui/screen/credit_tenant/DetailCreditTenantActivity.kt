package amat.kelolakost.ui.screen.credit_tenant

import amat.kelolakost.R
import amat.kelolakost.currencyFormatterStringViewZero
import amat.kelolakost.data.CreditTenant
import amat.kelolakost.data.CreditTenantHome
import amat.kelolakost.dateToDisplayMidFormat
import amat.kelolakost.di.Injection
import amat.kelolakost.ui.common.OnLifecycleEvent
import amat.kelolakost.ui.common.UiState
import amat.kelolakost.ui.component.CenterLayout
import amat.kelolakost.ui.component.ComboBox
import amat.kelolakost.ui.component.DetailCreditTenantItem
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
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
import com.google.android.material.bottomsheet.BottomSheetDialog

class DetailCreditTenantActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        val tenantId = intent.getStringExtra("tenantId")

        setContent {
            val context = LocalContext.current
            KelolaKostTheme {
                DetailCreditTenantScreen(context = context, tenantId = tenantId)
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
fun DetailCreditTenantScreen(
    modifier: Modifier = Modifier,
    context: Context,
    tenantId: String?
) {

    val myViewModel: DetailCreditTenantViewModel =
        viewModel(
            factory = DetailCreditTenantViewModelFactory(
                Injection.provideCreditTenantRepository(
                    context
                )
            )
        )

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                if (tenantId != null) {
                    myViewModel.getCreditTenant(tenantId)
                    myViewModel.getAllCreditTenant(tenantId)
                }
            }

            else -> {

            }
        }
    }

    if (!myViewModel.isProsesDeleteSuccess.collectAsState().value.isError) {
        Toast.makeText(
            context,
            stringResource(id = R.string.success_delete_payment_debt),
            Toast.LENGTH_SHORT
        )
            .show()
        if (tenantId != null) {
            myViewModel.getAllCreditTenant(tenantId)
        }
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
                    text = stringResource(id = R.string.title_detail_credit_tenant),
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

        myViewModel.stateCreditTenant.collectAsState(initial = UiState.Loading).value.let { uiState ->
            when (uiState) {
                is UiState.Error -> {
                    ErrorLayout(errorMessage = uiState.errorMessage) {
                        if (tenantId != null) {
                            myViewModel.getCreditTenant(tenantId)
                        }
                    }
                }

                UiState.Loading -> LoadingLayout()
                is UiState.Success -> {
                    ContentHeaderCreditTenant(context, uiState.data, myViewModel)
                }
            }

        }

    }
}

@Composable
fun ContentHeaderCreditTenant(
    context: Context,
    data: CreditTenantHome,
    myViewModel: DetailCreditTenantViewModel
) {
    if (data.tenantId.isEmpty()) {
        Text(
            text = stringResource(
                id = R.string.note_empty_data
            )
        )
    } else {
        Column(modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)) {
            ComboBox(
                modifier = Modifier.padding(bottom = 8.dp),
                title = stringResource(id = R.string.subtitle_tenant),
                value = data.tenantName
            )
            ComboBox(
                modifier = Modifier.padding(bottom = 8.dp),
                title = stringResource(id = R.string.subtitle_number_phone),
                value = data.tenantName
            )
            ComboBox(
                modifier = Modifier.padding(bottom = 8.dp),
                title = stringResource(id = R.string.total_debt),
                value = currencyFormatterStringViewZero(data.total)
            )
            Text(text = "List Piutang Penyewa", fontSize = 16.sp, color = FontBlack)
        }
        myViewModel.stateListCreditTenant.collectAsState(initial = UiState.Loading).value.let { uiState ->
            when (uiState) {
                is UiState.Error -> {
                    ErrorLayout(errorMessage = uiState.errorMessage) {
                        myViewModel.getCreditTenant(data.tenantId)
                    }
                }

                UiState.Loading -> LoadingLayout()
                is UiState.Success -> {
                    ListCreditTenant(uiState.data,
                        onClickHistory = { creditTenantId ->
                            val intent = Intent(context, HistoryCreditTenantActivity::class.java)
                            intent.putExtra("creditTenantId", creditTenantId)
                            context.startActivity(intent)
                        },
                        onClickPay = { creditTenantId ->
                            val intent = Intent(context, PaymentCreditTenantActivity::class.java)
                            intent.putExtra("creditTenantId", creditTenantId)
                            context.startActivity(intent)
                        },
                        onClickRemove = {creditTenantId->
                            showBottomConfirm(context, myViewModel, creditTenantId)
                        }
                    )
                }
            }

        }
    }
}

@Composable
fun ListCreditTenant(
    data: List<CreditTenant>, onClickHistory: (String) -> Unit,
    onClickPay: (String) -> Unit,
    onClickRemove: (String) -> Unit
) {
    if (data.isEmpty()) {
        CenterLayout(
            content = {
                Text(
                    text = stringResource(
                        id = R.string.note_empty_data,
                        ""
                    )
                )
            }
        )
    } else {
        LazyColumn(contentPadding = PaddingValues(bottom = 8.dp)) {
            items(data) { item ->
                DetailCreditTenantItem(
                    creditTenantId = item.id,
                    remainingDebt = currencyFormatterStringViewZero(item.remainingDebt.toString()),
                    note = item.note,
                    date = if (item.createAt.isEmpty()) "" else dateToDisplayMidFormat(item.createAt),
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
    detailCreditTenantViewModel: DetailCreditTenantViewModel,
    creditTenantId:String,
) {
    val bottomSheetDialog = BottomSheetDialog(context)
    bottomSheetDialog.setContentView(R.layout.bottom_sheet_confirm)
    val message = bottomSheetDialog.findViewById<TextView>(R.id.text_message)
    val buttonOk = bottomSheetDialog.findViewById<Button>(R.id.ok_button)

    val messageString =
        "Hapus Hutang Penyewa?"

    message?.text = messageString

    buttonOk?.setOnClickListener {
        bottomSheetDialog.dismiss()
        detailCreditTenantViewModel.delete(creditTenantId)
    }
    bottomSheetDialog.show()

}
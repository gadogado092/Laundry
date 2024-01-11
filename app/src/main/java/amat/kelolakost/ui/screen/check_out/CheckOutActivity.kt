package amat.kelolakost.ui.screen.check_out

import amat.kelolakost.R
import amat.kelolakost.currencyFormatterStringViewZero
import amat.kelolakost.dateToDisplayMidFormat
import amat.kelolakost.di.Injection
import amat.kelolakost.generateLimitText
import amat.kelolakost.ui.common.OnLifecycleEvent
import amat.kelolakost.ui.component.BoxRectangle
import amat.kelolakost.ui.component.ComboBox
import amat.kelolakost.ui.component.DateLayout
import amat.kelolakost.ui.component.MyOutlinedTextField
import amat.kelolakost.ui.theme.ColorRed
import amat.kelolakost.ui.theme.FontBlack
import amat.kelolakost.ui.theme.FontWhite
import amat.kelolakost.ui.theme.GreenDark
import amat.kelolakost.ui.theme.KelolaKostTheme
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.RadioButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class CheckOutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        val unitId = intent.getStringExtra("unitId")

        setContent {
            val context = LocalContext.current
            KelolaKostTheme {
                var unitIdScreen = ""
                if (unitId != null) {
                    unitIdScreen = unitId
                }
                CheckOutScreen(context = context, unitId = unitIdScreen)
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
fun CheckOutScreen(
    modifier: Modifier = Modifier,
    context: Context,
    unitId: String
) {
    val checkOutViewModel: CheckOutViewModel =
        viewModel(
            factory = CheckOutViewModelFactory(
                Injection.provideUnitRepository(context),
                Injection.provideCreditTenantRepository(context),
                Injection.provideUserRepository(context),
                Injection.provideCashFlowRepository(context)
            )
        )

    OnLifecycleEvent { owner, event ->
        // do stuff on event
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                checkOutViewModel.getDetail(unitId)
            }

            else -> {}
        }
    }

    if (!checkOutViewModel.isCheckOutSuccess.collectAsState().value.isError) {
        Toast.makeText(context, stringResource(id = R.string.success_check_out), Toast.LENGTH_SHORT)
            .show()
        val activity = (context as? Activity)
        activity?.finish()
    } else {
        if (checkOutViewModel.isCheckOutSuccess.collectAsState().value.errorMessage.isNotEmpty()) {
            Toast.makeText(
                context,
                checkOutViewModel.isCheckOutSuccess.collectAsState().value.errorMessage,
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
                    text = stringResource(id = R.string.check_out),
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ComboBox(
                modifier = Modifier.padding(bottom = 8.dp),
                title = stringResource(id = R.string.subtitle_tenant),
                value = checkOutViewModel.checkOutUi.collectAsState().value.tenantName
            )
            ComboBox(
                modifier = Modifier.padding(bottom = 8.dp),
                title = stringResource(id = R.string.location_unit),
                value = checkOutViewModel.checkOutUi.collectAsState().value.kostName
            )
            ComboBox(
                modifier = Modifier.padding(bottom = 8.dp),
                title = stringResource(id = R.string.subtitle_unit),
                value = "${checkOutViewModel.checkOutUi.collectAsState().value.unitName} - ${checkOutViewModel.checkOutUi.collectAsState().value.unitTypeName}"
            )
            Text(
                modifier = Modifier.padding(bottom = 8.dp),
                text = "Batas Keluar",
                style = TextStyle(color = FontBlack),
                fontSize = 16.sp
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DateLayout(
                    modifier = Modifier.fillMaxWidth(0.75F),
                    contentHorizontalArrangement = Arrangement.Center,
                    value = if (checkOutViewModel.checkOutUi.collectAsState().value.limitCheckOut.isNotEmpty())
                        "${dateToDisplayMidFormat(checkOutViewModel.checkOutUi.collectAsState().value.limitCheckOut)} - ${
                            generateLimitText(
                                checkOutViewModel.checkOutUi.collectAsState().value.limitCheckOut
                            )
                        }" else "-"
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(id = R.string.debt_tenant),
                        style = TextStyle(color = FontBlack),
                        fontSize = 16.sp
                    )
                    Text(
                        text = "* lakukan pembayaran hutang melalui menu lainnya",
                        style = TextStyle(color = FontBlack),
                        fontSize = 12.sp
                    )
                }
                BoxRectangle(
                    title = currencyFormatterStringViewZero(checkOutViewModel.checkOutUi.collectAsState().value.debtTenant.toString()),
                    fontSize = 14.sp,
                    backgroundColor = ColorRed
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.subtitle_price_guarantee_back),
                    style = TextStyle(color = FontBlack),
                    fontSize = 16.sp
                )
                BoxRectangle(
                    title = currencyFormatterStringViewZero(checkOutViewModel.checkOutUi.collectAsState().value.priceGuarantee.toString()),
                    fontSize = 14.sp
                )
            }

            Text(
                text = "Status Unit Setelah Check Out",
                style = TextStyle(color = FontBlack),
                fontSize = 16.sp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        checkOutViewModel.setStatusAfterCheckOut("Siap Digunakan")
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (checkOutViewModel.checkOutUi.collectAsState().value.statusAfterCheckOut == "Siap Digunakan"),
                    onClick = { checkOutViewModel.setStatusAfterCheckOut("Siap Digunakan") }
                )
                Text(
                    text = "Siap Digunakan", style = TextStyle(color = FontBlack),
                    fontSize = 16.sp
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        checkOutViewModel.setStatusAfterCheckOut("Pembersihan")
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (checkOutViewModel.checkOutUi.collectAsState().value.statusAfterCheckOut == "Pembersihan"),
                    onClick = { checkOutViewModel.setStatusAfterCheckOut("Pembersihan") }
                )
                Text(
                    text = "Pembersihan", style = TextStyle(color = FontBlack),
                    fontSize = 16.sp
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        checkOutViewModel.setStatusAfterCheckOut("Perbaikan")
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (checkOutViewModel.checkOutUi.collectAsState().value.statusAfterCheckOut == "Perbaikan"),
                    onClick = { checkOutViewModel.setStatusAfterCheckOut("Perbaikan") }
                )
                Text(
                    text = "Perbaikan", style = TextStyle(color = FontBlack),
                    fontSize = 16.sp
                )
            }

            if (checkOutViewModel.checkOutUi.collectAsState().value.statusAfterCheckOut != "Siap Digunakan") {
                MyOutlinedTextField(
                    label = "Catatan Perbaikan/Pembersihan Unit",
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    value = checkOutViewModel.checkOutUi.collectAsState().value.noteMaintenance,
                    onValueChange = {
                        checkOutViewModel.setNoteMaintenance(it)
                    },
                    isError = checkOutViewModel.isNoteMainTenanceValid.collectAsState().value.isError,
                    errorMessage = checkOutViewModel.isNoteMainTenanceValid.collectAsState().value.errorMessage,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            Button(
                onClick = {
                    if (checkOutViewModel.checkLimitApp()) {
                        showBottomLimitApp(context)
                    } else {
                        if (checkOutViewModel.dataIsComplete()) {
                            showBottomConfirm(context, checkOutViewModel)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = GreenDark)
            ) {
                Text(text = stringResource(id = R.string.process), color = FontWhite)
            }


        }

    }
}

private fun showBottomConfirm(
    context: Context,
    checkOutViewModel: CheckOutViewModel
) {
    val bottomSheetDialog = BottomSheetDialog(context)
    bottomSheetDialog.setContentView(R.layout.bottom_sheet_confirm)
    val message = bottomSheetDialog.findViewById<TextView>(R.id.text_message)
    val buttonOk = bottomSheetDialog.findViewById<Button>(R.id.ok_button)

    var messageString =
        "Proses Check-Out penyewa ${checkOutViewModel.checkOutUi.value.tenantName} unit ${checkOutViewModel.checkOutUi.value.unitName}?"

    if (checkOutViewModel.checkOutUi.value.priceGuarantee > 0) {
        messageString += "\nJangan lupa kembalikan uang jaminan sebesar ${
            currencyFormatterStringViewZero(
                checkOutViewModel.checkOutUi.value.priceGuarantee.toString()
            )
        }"
    }

    message?.text = messageString

    buttonOk?.setOnClickListener {
        bottomSheetDialog.dismiss()
        checkOutViewModel.prosesCheckOut()
    }
    bottomSheetDialog.show()

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

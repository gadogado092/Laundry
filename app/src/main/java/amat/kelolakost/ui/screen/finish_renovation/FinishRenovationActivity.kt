package amat.kelolakost.ui.screen.finish_renovation

import amat.kelolakost.R
import amat.kelolakost.dateToDisplayMidFormat
import amat.kelolakost.di.Injection
import amat.kelolakost.generateLimitText
import amat.kelolakost.ui.common.OnLifecycleEvent
import amat.kelolakost.ui.component.ComboBox
import amat.kelolakost.ui.component.DateLayout
import amat.kelolakost.ui.component.MyOutlinedTextField
import amat.kelolakost.ui.component.MyOutlinedTextFieldCurrency
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class FinishRenovationActivity : ComponentActivity() {
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

                FinishRenovationScreen(context = context, unitId = unitIdScreen)
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
fun FinishRenovationScreen(
    modifier: Modifier = Modifier,
    context: Context,
    unitId: String
) {

    val finishRenovationViewModel: FinishRenovationViewModel =
        viewModel(
            factory = FinishRenovationViewModelFactory(
                Injection.provideUnitRepository(context),
                Injection.provideUserRepository(context),
                Injection.provideCashFlowRepository(context)
            )
        )

    OnLifecycleEvent { owner, event ->
        // do stuff on event
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                finishRenovationViewModel.getDetail(unitId)
            }

            else -> {}
        }
    }

    if (!finishRenovationViewModel.isProsesSuccess.collectAsState().value.isError) {
        Toast.makeText(context, stringResource(id = R.string.success_report), Toast.LENGTH_SHORT)
            .show()
        val activity = (context as? Activity)
        activity?.finish()
    } else {
        if (finishRenovationViewModel.isProsesSuccess.collectAsState().value.errorMessage.isNotEmpty()) {
            Toast.makeText(
                context,
                finishRenovationViewModel.isProsesSuccess.collectAsState().value.errorMessage,
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
                    text = stringResource(id = R.string.finish_renovation),
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
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            ComboBox(
                modifier = Modifier.padding(bottom = 8.dp),
                title = stringResource(id = R.string.location_unit),
                value = finishRenovationViewModel.finishRenovationUi.collectAsState().value.kostName
            )
            ComboBox(
                modifier = Modifier.padding(bottom = 8.dp),
                title = stringResource(id = R.string.subtitle_unit),
                value = "${finishRenovationViewModel.finishRenovationUi.collectAsState().value.unitName} - ${finishRenovationViewModel.finishRenovationUi.collectAsState().value.unitTypeName}"
            )
            Text(
                modifier = Modifier.padding(bottom = 8.dp),
                text = "Tanggal Pekerjaan Selesai",
                style = TextStyle(color = FontBlack),
                fontSize = 16.sp
            )
            DateLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                value = if (finishRenovationViewModel.finishRenovationUi.collectAsState().value.finishDate.isNotEmpty())
                    "${dateToDisplayMidFormat(finishRenovationViewModel.finishRenovationUi.collectAsState().value.finishDate)} - ${
                        generateLimitText(
                            finishRenovationViewModel.finishRenovationUi.collectAsState().value.finishDate
                        )
                    }" else "-"
            )
            MyOutlinedTextField(
                label = "Catatan Perbaikan/Pembersihan",
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                value = finishRenovationViewModel.finishRenovationUi.collectAsState().value.noteMaintenance,
                onValueChange = {
                    finishRenovationViewModel.setNoteMaintenance(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
            )
            MyOutlinedTextFieldCurrency(
                label = "Biaya Perbaikan/Pembersihan",
                value = finishRenovationViewModel.finishRenovationUi.collectAsState().value.costMaintenance.replace(
                    ".",
                    ""
                ),
                onValueChange = {
                    finishRenovationViewModel.setCostMaintenance(it)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth(),
                currencyValue = finishRenovationViewModel.finishRenovationUi.collectAsState().value.costMaintenance,
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.payment_finish_via),
                    style = TextStyle(color = FontBlack),
                    fontSize = 16.sp
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .weight(1F)
                            .clickable {
                                finishRenovationViewModel.setPaymentType(true)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (finishRenovationViewModel.finishRenovationUi.collectAsState().value.isCash),
                            onClick = { finishRenovationViewModel.setPaymentType(true) }
                        )
                        Text(
                            text = "Cash", style = TextStyle(color = FontBlack),
                            fontSize = 16.sp
                        )
                    }
                    Row(
                        modifier = Modifier
                            .weight(1F)
                            .clickable {
                                finishRenovationViewModel.setPaymentType(false)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (!finishRenovationViewModel.finishRenovationUi.collectAsState().value.isCash),
                            onClick = { finishRenovationViewModel.setPaymentType(false) }
                        )
                        Text(
                            text = "Transfer", style = TextStyle(color = FontBlack),
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Button(
                onClick = {
                    if (finishRenovationViewModel.dataIsComplete()) {
                        showBottomConfirm(context, finishRenovationViewModel)
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
    finishRenovationViewModel: FinishRenovationViewModel
) {
    val bottomSheetDialog = BottomSheetDialog(context)
    bottomSheetDialog.setContentView(R.layout.bottom_sheet_confirm)
    val message = bottomSheetDialog.findViewById<TextView>(R.id.text_message)
    val buttonOk = bottomSheetDialog.findViewById<Button>(R.id.ok_button)

    val messageString =
        "Proses input laporan pengerjaan kost ${finishRenovationViewModel.finishRenovationUi.value.kostName} " +
                "pada unit ${finishRenovationViewModel.finishRenovationUi.value.unitName}-${finishRenovationViewModel.finishRenovationUi.value.unitTypeName}?"


    message?.text = messageString

    buttonOk?.setOnClickListener {
        bottomSheetDialog.dismiss()
        finishRenovationViewModel.proses()
    }
    bottomSheetDialog.show()

}
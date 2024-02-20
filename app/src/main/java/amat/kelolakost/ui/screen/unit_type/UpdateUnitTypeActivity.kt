package amat.kelolakost.ui.screen.unit_type

import amat.kelolakost.R
import amat.kelolakost.di.Injection
import amat.kelolakost.ui.common.OnLifecycleEvent
import amat.kelolakost.ui.component.MyOutlinedTextField
import amat.kelolakost.ui.component.MyOutlinedTextFieldCurrency
import amat.kelolakost.ui.theme.FontWhite
import amat.kelolakost.ui.theme.GreenDark
import amat.kelolakost.ui.theme.GreyLight
import amat.kelolakost.ui.theme.GreyLight3
import amat.kelolakost.ui.theme.KelolaKostTheme
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedButton
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

class UpdateUnitTypeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        val id = intent.getStringExtra("id")

        setContent {
            val context = LocalContext.current
            KelolaKostTheme {
                if (id != null) {
                    UpdateUnitTypeScreen(context, id)
                } else {
                    UpdateUnitTypeScreen(context, "")
                }
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
fun UpdateUnitTypeScreen(
    context: Context,
    id: String,
) {
    val updateUnitTypeViewModel: UpdateUnitTypeViewModel =
        viewModel(
            factory = UpdateUnitTypeViewModelFactory(
                Injection.provideUnitTypeRepository(
                    context
                )
            )
        )

    if (!updateUnitTypeViewModel.isUpdateSuccess.collectAsState().value.isError) {
        Toast.makeText(
            context,
            "Proses Sukses",
            Toast.LENGTH_SHORT
        )
            .show()
        val activity = (context as? Activity)
        activity?.finish()
    } else {
        if (updateUnitTypeViewModel.isUpdateSuccess.collectAsState().value.errorMessage.isNotEmpty()) {
            Toast.makeText(
                context,
                updateUnitTypeViewModel.isUpdateSuccess.collectAsState().value.errorMessage,
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    OnLifecycleEvent { owner, event ->
        // do stuff on event
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                updateUnitTypeViewModel.getDetail(id)
            }

            else -> {}
        }
    }

    Column {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.title_update_unit_type),
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
            MyOutlinedTextField(
                label = "Nama Tipe",
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                value = updateUnitTypeViewModel.unitTypeUi.collectAsState().value.name,
                onValueChange = {
                    updateUnitTypeViewModel.setName(it)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                isError = updateUnitTypeViewModel.isNameValid.collectAsState().value.isError,
                errorMessage = updateUnitTypeViewModel.isNameValid.collectAsState().value.errorMessage
            )
            MyOutlinedTextField(
                label = "Keterangan Tambahan",
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                value = updateUnitTypeViewModel.unitTypeUi.collectAsState().value.note,
                onValueChange = {
                    updateUnitTypeViewModel.setNote(it)
                },
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth(),
                singleLine = false
            )
            MyOutlinedTextFieldCurrency(
                label = stringResource(id = R.string.subtitle_price_guarantee),
                value = updateUnitTypeViewModel.unitTypeUi.collectAsState().value.priceGuarantee.replace(
                    ".",
                    ""
                ),
                onValueChange = {
                    updateUnitTypeViewModel.setPriceGuarantee(it)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth(),
                currencyValue = updateUnitTypeViewModel.unitTypeUi.collectAsState().value.priceGuarantee
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = stringResource(id = R.string.subtitle_price_unit))
            Spacer(modifier = Modifier.height(2.dp))
            MyOutlinedTextFieldCurrency(
                label = stringResource(id = R.string.subtitle_price_day),
                value = updateUnitTypeViewModel.unitTypeUi.collectAsState().value.priceDay.replace(
                    ".",
                    ""
                ),
                onValueChange = {
                    updateUnitTypeViewModel.setPriceDay(it)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                currencyValue = updateUnitTypeViewModel.unitTypeUi.collectAsState().value.priceDay
            )
            MyOutlinedTextFieldCurrency(
                label = stringResource(id = R.string.subtitle_price_week),
                value = updateUnitTypeViewModel.unitTypeUi.collectAsState().value.priceWeek.replace(
                    ".",
                    ""
                ),
                onValueChange = {
                    updateUnitTypeViewModel.setPriceWeek(it)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth(),
                currencyValue = updateUnitTypeViewModel.unitTypeUi.collectAsState().value.priceWeek
            )
            MyOutlinedTextFieldCurrency(
                label = stringResource(id = R.string.subtitle_price_month),
                value = updateUnitTypeViewModel.unitTypeUi.collectAsState().value.priceMonth.replace(
                    ".",
                    ""
                ),
                onValueChange = {
                    updateUnitTypeViewModel.setPriceMonth(it)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                currencyValue = updateUnitTypeViewModel.unitTypeUi.collectAsState().value.priceMonth
            )
            MyOutlinedTextFieldCurrency(
                label = stringResource(id = R.string.subtitle_price_three_month),
                value = updateUnitTypeViewModel.unitTypeUi.collectAsState().value.priceThreeMonth.replace(
                    ".",
                    ""
                ),
                onValueChange = {
                    updateUnitTypeViewModel.setPriceThreeMonth(it)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                currencyValue = updateUnitTypeViewModel.unitTypeUi.collectAsState().value.priceThreeMonth
            )
            MyOutlinedTextFieldCurrency(
                label = stringResource(id = R.string.subtitle_price_six_month),
                value = updateUnitTypeViewModel.unitTypeUi.collectAsState().value.priceSixMonth.replace(
                    ".",
                    ""
                ),
                onValueChange = {
                    updateUnitTypeViewModel.setPriceSixMonth(it)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                currencyValue = updateUnitTypeViewModel.unitTypeUi.collectAsState().value.priceSixMonth
            )
            MyOutlinedTextFieldCurrency(
                label = stringResource(id = R.string.subtitle_price_year),
                value = updateUnitTypeViewModel.unitTypeUi.collectAsState().value.priceYear.replace(
                    ".",
                    ""
                ),
                onValueChange = {
                    updateUnitTypeViewModel.setPriceYear(it)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                currencyValue = updateUnitTypeViewModel.unitTypeUi.collectAsState().value.priceYear
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
            {
                OutlinedButton(
                    modifier = Modifier
                        .weight(1F),
                    onClick = {
                        if (updateUnitTypeViewModel.isCanDelete()) {
                            showBottomConfirm(context, updateUnitTypeViewModel)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = GreyLight
                    )
                ) {
                    Text(text = stringResource(id = R.string.delete), color = GreyLight3)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    modifier = Modifier
                        .weight(1F),
                    onClick = {
                        updateUnitTypeViewModel.prosesUpdate()
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = GreenDark)
                ) {
                    Text(text = stringResource(id = R.string.update), color = FontWhite)
                }
            }

        }
    }
}

private fun showBottomConfirm(
    context: Context,
    updateUnitTypeViewModel: UpdateUnitTypeViewModel
) {
    val bottomSheetDialog = BottomSheetDialog(context)
    bottomSheetDialog.setContentView(R.layout.bottom_sheet_confirm)
    val message = bottomSheetDialog.findViewById<TextView>(R.id.text_message)
    val buttonOk = bottomSheetDialog.findViewById<Button>(R.id.ok_button)

    val messageString =
        "Lanjutkan Hapus Tipe Unit?"

    message?.text = messageString

    buttonOk?.setOnClickListener {
        bottomSheetDialog.dismiss()
        updateUnitTypeViewModel.deleteUnitType()
    }
    bottomSheetDialog.show()

}
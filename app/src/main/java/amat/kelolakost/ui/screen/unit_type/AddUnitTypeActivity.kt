package amat.kelolakost.ui.screen.unit_type

import amat.kelolakost.R
import amat.kelolakost.di.Injection
import amat.kelolakost.ui.component.MyOutlinedTextField
import amat.kelolakost.ui.component.MyOutlinedTextFieldCurrency
import amat.kelolakost.ui.theme.FontWhite
import amat.kelolakost.ui.theme.GreenDark
import amat.kelolakost.ui.theme.KelolaKostTheme
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.viewmodel.compose.viewModel

class AddUnitTypeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            KelolaKostTheme {
                AddKostScreen(context)
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
fun AddKostScreen(
    context: Context,
    modifier: Modifier = Modifier
) {
    val addUnitTypeViewModel: AddUnitTypeViewModel =
        viewModel(factory = AddUnitTypeViewModelFactory(Injection.provideUnitTypeRepository(context)))

    if (!addUnitTypeViewModel.isInsertSuccess.collectAsState().value.isError) {
        Toast.makeText(context, stringResource(id = R.string.success_add_data), Toast.LENGTH_SHORT)
            .show()
        val activity = (context as? Activity)
        activity?.finish()
    } else {
        if (addUnitTypeViewModel.isInsertSuccess.collectAsState().value.errorMessage.isNotEmpty()) {
            Toast.makeText(
                context,
                addUnitTypeViewModel.isInsertSuccess.collectAsState().value.errorMessage,
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    Column {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.title_add_unit_type),
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
                value = addUnitTypeViewModel.unitTypeUi.collectAsState().value.name,
                onValueChange = {
                    addUnitTypeViewModel.setName(it)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                isError = addUnitTypeViewModel.isNameValid.collectAsState().value.isError,
                errorMessage = addUnitTypeViewModel.isNameValid.collectAsState().value.errorMessage
            )
            MyOutlinedTextField(
                label = "Keterangan Tambahan",
                value = addUnitTypeViewModel.unitTypeUi.collectAsState().value.note,
                onValueChange = {
                    addUnitTypeViewModel.setNote(it)
                },
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth(),
                singleLine = false
            )
            MyOutlinedTextFieldCurrency(
                label = stringResource(id = R.string.subtitle_price_year),
                value = addUnitTypeViewModel.unitTypeUi.collectAsState().value.priceGuarantee.replace(
                    ".",
                    ""
                ),
                onValueChange = {
                    addUnitTypeViewModel.setPriceGuarantee(it)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth(),
                currencyValue = addUnitTypeViewModel.unitTypeUi.collectAsState().value.priceGuarantee
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = stringResource(id = R.string.subtitle_price_unit))
            Spacer(modifier = Modifier.height(2.dp))
            MyOutlinedTextFieldCurrency(
                label = stringResource(id = R.string.subtitle_price_day),
                value = addUnitTypeViewModel.unitTypeUi.collectAsState().value.priceDay.replace(
                    ".",
                    ""
                ),
                onValueChange = {
                    addUnitTypeViewModel.setPriceDay(it)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                currencyValue = addUnitTypeViewModel.unitTypeUi.collectAsState().value.priceDay
            )
            MyOutlinedTextFieldCurrency(
                label = stringResource(id = R.string.subtitle_price_week),
                value = addUnitTypeViewModel.unitTypeUi.collectAsState().value.priceWeek.replace(
                    ".",
                    ""
                ),
                onValueChange = {
                    addUnitTypeViewModel.setPriceWeek(it)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth(),
                currencyValue = addUnitTypeViewModel.unitTypeUi.collectAsState().value.priceWeek
            )
            MyOutlinedTextFieldCurrency(
                label = stringResource(id = R.string.subtitle_price_month),
                value = addUnitTypeViewModel.unitTypeUi.collectAsState().value.priceMonth.replace(
                    ".",
                    ""
                ),
                onValueChange = {
                    addUnitTypeViewModel.setPriceMonth(it)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                currencyValue = addUnitTypeViewModel.unitTypeUi.collectAsState().value.priceMonth
            )
            MyOutlinedTextFieldCurrency(
                label = stringResource(id = R.string.subtitle_price_three_month),
                value = addUnitTypeViewModel.unitTypeUi.collectAsState().value.priceThreeMonth.replace(
                    ".",
                    ""
                ),
                onValueChange = {
                    addUnitTypeViewModel.setPriceThreeMonth(it)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                currencyValue = addUnitTypeViewModel.unitTypeUi.collectAsState().value.priceThreeMonth
            )
            MyOutlinedTextFieldCurrency(
                label = stringResource(id = R.string.subtitle_price_six_month),
                value = addUnitTypeViewModel.unitTypeUi.collectAsState().value.priceSixMonth.replace(
                    ".",
                    ""
                ),
                onValueChange = {
                    addUnitTypeViewModel.setPriceSixMonth(it)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                currencyValue = addUnitTypeViewModel.unitTypeUi.collectAsState().value.priceSixMonth
            )
            MyOutlinedTextFieldCurrency(
                label = stringResource(id = R.string.subtitle_price_year),
                value = addUnitTypeViewModel.unitTypeUi.collectAsState().value.priceYear.replace(
                    ".",
                    ""
                ),
                onValueChange = {
                    addUnitTypeViewModel.setPriceYear(it)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                currencyValue = addUnitTypeViewModel.unitTypeUi.collectAsState().value.priceYear
            )
            Button(
                onClick = {
                    addUnitTypeViewModel.prosesInsert()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = GreenDark)
            ) {
                Text(text = stringResource(id = R.string.save), color = FontWhite)
            }
        }
    }
}
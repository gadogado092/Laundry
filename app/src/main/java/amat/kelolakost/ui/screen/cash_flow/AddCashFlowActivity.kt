package amat.kelolakost.ui.screen.cash_flow

import amat.kelolakost.KostAdapter
import amat.kelolakost.R
import amat.kelolakost.TenantAdapter
import amat.kelolakost.convertDateToDay
import amat.kelolakost.convertDateToMonth
import amat.kelolakost.convertDateToYear
import amat.kelolakost.data.Kost
import amat.kelolakost.data.Tenant
import amat.kelolakost.data.UnitAdapter
import amat.kelolakost.dateToDisplayMidFormat
import amat.kelolakost.di.Injection
import amat.kelolakost.ui.common.UiState
import amat.kelolakost.ui.component.ComboBox
import amat.kelolakost.ui.component.DateLayout
import amat.kelolakost.ui.component.InformationBox
import amat.kelolakost.ui.component.MyOutlinedTextField
import amat.kelolakost.ui.component.MyOutlinedTextFieldCurrency
import amat.kelolakost.ui.screen.kost.AddKostActivity
import amat.kelolakost.ui.screen.tenant.AddTenantActivity
import amat.kelolakost.ui.screen.unit.AddUnitActivity
import amat.kelolakost.ui.theme.FontBlack
import amat.kelolakost.ui.theme.FontWhite
import amat.kelolakost.ui.theme.GreenDark
import amat.kelolakost.ui.theme.KelolaKostTheme
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.DatePicker
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog

class AddCashFlowActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            KelolaKostTheme {
                AddCashFlowScreen(context = context)
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
fun AddCashFlowScreen(
    modifier: Modifier = Modifier,
    context: Context
) {
    val myViewModel: AddCashFlowViewModel =
        viewModel(
            factory = AddCashFlowViewModelFactory(
                Injection.provideTenantRepository(context),
                Injection.provideUnitRepository(context),
                Injection.provideKostRepository(context),
                Injection.provideCashFlowRepository(context)
            )
        )

    //catch get Tenant result
    myViewModel.stateListTenant.collectAsState(initial = UiState.Error("")).value.let { uiState ->
        when (uiState) {
            is UiState.Error -> {
                if (uiState.errorMessage.isNotEmpty()) {
                    Toast.makeText(
                        context,
                        uiState.errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            UiState.Loading -> {
                Toast.makeText(
                    context,
                    "Loading Data Penyewa/Tenant",
                    Toast.LENGTH_SHORT
                ).show()
            }

            is UiState.Success -> {
                showBottomSheetTenant(
                    addCashFlowViewModel = myViewModel,
                    context = context,
                    uiState.data
                )
            }
        }
    }

    //catch get Kost result
    myViewModel.stateListKost.collectAsState(initial = UiState.Error("")).value.let { uiState ->
        when (uiState) {
            is UiState.Error -> {
                if (uiState.errorMessage.isNotEmpty()) {
                    Toast.makeText(
                        context,
                        uiState.errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            UiState.Loading -> {
                Toast.makeText(
                    context,
                    "Loading Data Kost",
                    Toast.LENGTH_SHORT
                ).show()
            }

            is UiState.Success -> {
                showBottomSheetKost(
                    addCashFlowViewModel = myViewModel,
                    context = context,
                    uiState.data
                )
            }
        }
    }

    //catch get Unit result
    myViewModel.stateListUnit.collectAsState(initial = UiState.Error("")).value.let { uiState ->
        when (uiState) {
            is UiState.Error -> {
                if (uiState.errorMessage.isNotEmpty()) {
                    Toast.makeText(
                        context,
                        uiState.errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            UiState.Loading -> {
                Toast.makeText(
                    context,
                    "Loading Data Unit/Kamar",
                    Toast.LENGTH_SHORT
                ).show()
            }

            is UiState.Success -> {
                showBottomSheetUnit(
                    addCashFlowViewModel = myViewModel,
                    context = context,
                    uiState.data
                )
            }
        }
    }

    if (!myViewModel.isProsesSuccess.collectAsState().value.isError) {
        Toast.makeText(context, stringResource(id = R.string.success_add_data), Toast.LENGTH_SHORT)
            .show()
        val activity = (context as? Activity)
        activity?.finish()
    } else {
        if (myViewModel.isProsesSuccess.collectAsState().value.errorMessage.isNotEmpty()) {
            Toast.makeText(
                context,
                myViewModel.isProsesSuccess.collectAsState().value.errorMessage,
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
                    text = stringResource(id = R.string.add_cash_flow),
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
            Text(
                text = stringResource(id = R.string.type_cash_flow),
                style = TextStyle(color = FontBlack),
                fontSize = 16.sp
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .weight(1F)
                        .clickable {
                            myViewModel.setCashFlowType(true)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (myViewModel.stateUi.collectAsState().value.isCashOut),
                        onClick = { myViewModel.setCashFlowType(true) }
                    )
                    Text(
                        text = "Keluar", style = TextStyle(color = FontBlack),
                        fontSize = 16.sp
                    )
                }
                Row(
                    modifier = Modifier
                        .weight(1F)
                        .clickable {
                            myViewModel.setCashFlowType(false)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (!myViewModel.stateUi.collectAsState().value.isCashOut),
                        onClick = { myViewModel.setCashFlowType(false) }
                    )
                    Text(
                        text = "Masuk", style = TextStyle(color = FontBlack),
                        fontSize = 16.sp
                    )
                }
            }

            InformationBox(
                modifier = Modifier.padding(bottom = 8.dp),
                value = stringResource(id = R.string.info_add_cash_flow)
            )
            ComboBox(
                title = stringResource(id = R.string.subtitle_tenant),
                value = myViewModel.stateUi.collectAsState().value.tenantName
            ) {
                myViewModel.getTenant()
            }
            ComboBox(
                title = stringResource(id = R.string.location_unit),
                value = myViewModel.stateUi.collectAsState().value.kostName,
                isError = myViewModel.isKostSelectedValid.collectAsState().value.isError,
                errorMessage = myViewModel.isKostSelectedValid.collectAsState().value.errorMessage
            ) {
                myViewModel.getKost()
            }
            ComboBox(
                title = stringResource(id = R.string.subtitle_unit),
                value = "${myViewModel.stateUi.collectAsState().value.unitName} - ${myViewModel.stateUi.collectAsState().value.unitTypeName}"
            ) {
                myViewModel.getUnit()
            }
            MyOutlinedTextFieldCurrency(
                label = "Nominal",
                value = myViewModel.stateUi.collectAsState().value.nominal.replace(
                    ".",
                    ""
                ),
                onValueChange = {
                    myViewModel.setNominal(it)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth(),
                isError = myViewModel.isNominalValid.collectAsState().value.isError,
                errorMessage = myViewModel.isNominalValid.collectAsState().value.errorMessage,
                currencyValue = myViewModel.stateUi.collectAsState().value.nominal
            )
            MyOutlinedTextField(
                label = "Keterangan",
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                value = myViewModel.stateUi.collectAsState().value.note,
                onValueChange = {
                    myViewModel.setNote(it)
                },
                isError = myViewModel.isNoteValid.collectAsState().value.isError,
                errorMessage = myViewModel.isNoteValid.collectAsState().value.errorMessage,
                modifier = Modifier
                    .fillMaxWidth()
            )

            DateLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePickerPayment(context, myViewModel) },
                title = "Tanggal Pembayaran",
                value = if (myViewModel.stateUi.collectAsState().value.createAt.isNotEmpty())
                    dateToDisplayMidFormat(myViewModel.stateUi.collectAsState().value.createAt) else "-",
                isEnable = true
            )

            Text(
                text = stringResource(id = R.string.payment_via),
                style = TextStyle(color = FontBlack),
                fontSize = 16.sp
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .weight(1F)
                        .clickable {
                            myViewModel.setPaymentType(true)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (myViewModel.stateUi.collectAsState().value.isCash),
                        onClick = { myViewModel.setPaymentType(true) }
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
                            myViewModel.setPaymentType(false)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (!myViewModel.stateUi.collectAsState().value.isCash),
                        onClick = { myViewModel.setPaymentType(false) }
                    )
                    Text(
                        text = "Transfer", style = TextStyle(color = FontBlack),
                        fontSize = 16.sp
                    )
                }
            }

            Button(
                onClick = {
                    if (myViewModel.dataIsComplete()) {
                        showBottomConfirm(context, myViewModel)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = GreenDark)
            ) {
                Text(text = stringResource(id = R.string.process), color = FontWhite)
            }

        }

    }
}

fun showBottomSheetTenant(
    addCashFlowViewModel: AddCashFlowViewModel,
    context: Context,
    data: List<Tenant>
) {
    val bottomSheetDialog = BottomSheetDialog(context)
    bottomSheetDialog.setContentView(R.layout.bottom_sheet_select_list)
    val title = bottomSheetDialog.findViewById<TextView>(R.id.text_title)
    val textEmpty = bottomSheetDialog.findViewById<TextView>(R.id.text_empty)
    val buttonAdd = bottomSheetDialog.findViewById<Button>(R.id.button_add)
    val recyclerView = bottomSheetDialog.findViewById<RecyclerView>(R.id.recyclerView)

    title?.setText(R.string.subtitle_tenant)
    buttonAdd?.setText(R.string.add)

    if (data.isEmpty()) {
        textEmpty?.visibility = View.VISIBLE
    }

    buttonAdd?.setOnClickListener {
        val intent = Intent(context, AddTenantActivity::class.java)
        context.startActivity(intent)
        bottomSheetDialog.dismiss()
    }

    val adapter = TenantAdapter {
        addCashFlowViewModel.setTenantSelected(it.id, it.name)
        bottomSheetDialog.dismiss()
    }

    with(recyclerView) {
        this?.setHasFixedSize(true)
        this?.layoutManager =
            LinearLayoutManager(context)
        this?.adapter = adapter
    }

    adapter.setData(data)
    bottomSheetDialog.show()
}

fun showBottomSheetKost(
    addCashFlowViewModel: AddCashFlowViewModel,
    context: Context,
    data: List<Kost>
) {
    val bottomSheetDialog = BottomSheetDialog(context)
    bottomSheetDialog.setContentView(R.layout.bottom_sheet_select_list)
    val title = bottomSheetDialog.findViewById<TextView>(R.id.text_title)
    val textEmpty = bottomSheetDialog.findViewById<TextView>(R.id.text_empty)
    val buttonAdd = bottomSheetDialog.findViewById<Button>(R.id.button_add)
    val recyclerView = bottomSheetDialog.findViewById<RecyclerView>(R.id.recyclerView)

    title?.setText(R.string.location_unit)
    buttonAdd?.setText(R.string.add)

    if (data.isEmpty()) {
        textEmpty?.visibility = View.VISIBLE
    }

    buttonAdd?.setOnClickListener {
        val intent = Intent(context, AddKostActivity::class.java)
        context.startActivity(intent)
        bottomSheetDialog.dismiss()
    }

    val adapter = KostAdapter {
        addCashFlowViewModel.setKostSelected(it.id, it.name)
        bottomSheetDialog.dismiss()
    }

    with(recyclerView) {
        this?.setHasFixedSize(true)
        this?.layoutManager =
            LinearLayoutManager(context)
        this?.adapter = adapter
    }

    adapter.setData(data)
    bottomSheetDialog.show()
}

fun showBottomSheetUnit(
    addCashFlowViewModel: AddCashFlowViewModel,
    context: Context,
    data: List<UnitAdapter>
) {
    val bottomSheetDialog = BottomSheetDialog(context)
    bottomSheetDialog.setContentView(R.layout.bottom_sheet_select_list)
    val title = bottomSheetDialog.findViewById<TextView>(R.id.text_title)
    val textEmpty = bottomSheetDialog.findViewById<TextView>(R.id.text_empty)
    val buttonAdd = bottomSheetDialog.findViewById<Button>(R.id.button_add)
    val recyclerView = bottomSheetDialog.findViewById<RecyclerView>(R.id.recyclerView)

    title?.setText(R.string.subtitle_unit)
    buttonAdd?.setText(R.string.add)

    if (data.isEmpty()) {
        textEmpty?.visibility = View.VISIBLE
    }

    buttonAdd?.setOnClickListener {
        val intent = Intent(context, AddUnitActivity::class.java)
        context.startActivity(intent)
        bottomSheetDialog.dismiss()
    }

    val adapter = amat.kelolakost.UnitAdapter {
        addCashFlowViewModel.setUnitSelected(it.id, it.name, it.unitTypeName)
        bottomSheetDialog.dismiss()
    }

    with(recyclerView) {
        this?.setHasFixedSize(true)
        this?.layoutManager =
            LinearLayoutManager(context)
        this?.adapter = adapter
    }

    adapter.setData(data)
    bottomSheetDialog.show()
}

fun showDatePickerPayment(context: Context, viewModel: AddCashFlowViewModel) {
    val mYear: Int = convertDateToYear(viewModel.stateUi.value.createAt).toInt()
    val mMonth: Int = convertDateToMonth(viewModel.stateUi.value.createAt).toInt() - 1
    val mDay: Int = convertDateToDay(viewModel.stateUi.value.createAt).toInt()

    val mDatePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            val dateSelected = "$year-${month + 1}-$day"
            viewModel.setPaymentDate(dateSelected)
        }, mYear, mMonth, mDay
    )
    mDatePickerDialog.show()
}

private fun showBottomConfirm(
    context: Context,
    addCashFlowViewModel: AddCashFlowViewModel
) {
    val bottomSheetDialog = BottomSheetDialog(context)
    bottomSheetDialog.setContentView(R.layout.bottom_sheet_confirm)
    val message = bottomSheetDialog.findViewById<TextView>(R.id.text_message)
    val buttonOk = bottomSheetDialog.findViewById<Button>(R.id.ok_button)

    val messageString =
        "Tambah Data Alur Kas?"

    message?.text = messageString

    buttonOk?.setOnClickListener {
        bottomSheetDialog.dismiss()
        addCashFlowViewModel.process()
    }
    bottomSheetDialog.show()

}
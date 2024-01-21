package amat.kelolakost.ui.screen.move

import amat.kelolakost.R
import amat.kelolakost.currencyFormatterStringViewZero
import amat.kelolakost.data.UnitAdapter
import amat.kelolakost.dateToDisplayMidFormat
import amat.kelolakost.di.Injection
import amat.kelolakost.generateLimitText
import amat.kelolakost.ui.common.OnLifecycleEvent
import amat.kelolakost.ui.common.UiState
import amat.kelolakost.ui.component.BoxRectangle
import amat.kelolakost.ui.component.ComboBox
import amat.kelolakost.ui.component.DateLayout
import amat.kelolakost.ui.component.MyOutlinedTextField
import amat.kelolakost.ui.component.MyOutlinedTextFieldCurrency
import amat.kelolakost.ui.screen.bill.BillActivity
import amat.kelolakost.ui.screen.unit.AddUnitActivity
import amat.kelolakost.ui.theme.ColorRed
import amat.kelolakost.ui.theme.FontBlack
import amat.kelolakost.ui.theme.FontWhite
import amat.kelolakost.ui.theme.GreenDark
import amat.kelolakost.ui.theme.GreyLight
import amat.kelolakost.ui.theme.KelolaKostTheme
import amat.kelolakost.ui.theme.TealGreen
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
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
import androidx.compose.material.Divider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog

class MoveActivity : ComponentActivity() {
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
                MoveScreen(context = context, unitId = unitIdScreen)
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
fun MoveScreen(
    modifier: Modifier = Modifier,
    context: Context,
    unitId: String
) {

    val moveViewModel: MoveViewModel =
        viewModel(
            factory = MoveViewModelFactory(
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
                moveViewModel.getDetail(unitId)
            }

            else -> {}
        }
    }
    //catch get Unit result
    moveViewModel.stateListUnit.collectAsState(initial = UiState.Error("")).value.let { uiState ->
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
                    moveViewModel = moveViewModel,
                    context = context,
                    uiState.data
                )
            }
        }
    }

    if (!moveViewModel.isMoveSuccess.collectAsState().value.isError) {
        Toast.makeText(context, stringResource(id = R.string.success_move_unit), Toast.LENGTH_SHORT)
            .show()
        if (moveViewModel.moveUi.collectAsState().value.moveType != "Gratis") {
            val intent = Intent(context, BillActivity::class.java)
            intent.putExtra("object", moveViewModel.getBill())
            context.startActivity(intent)
        }
        val activity = (context as? Activity)
        activity?.finish()
    } else {
        if (moveViewModel.isMoveSuccess.collectAsState().value.errorMessage.isNotEmpty()) {
            Toast.makeText(
                context,
                moveViewModel.isMoveSuccess.collectAsState().value.errorMessage,
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
                    text = stringResource(id = R.string.move_unit),
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
                value = moveViewModel.moveUi.collectAsState().value.tenantName
            )
            ComboBox(
                modifier = Modifier.padding(bottom = 8.dp),
                title = stringResource(id = R.string.location_unit),
                value = moveViewModel.moveUi.collectAsState().value.kostName
            )
            ComboBox(
                modifier = Modifier.padding(bottom = 8.dp),
                title = stringResource(id = R.string.subtitle_unit),
                value = "${moveViewModel.moveUi.collectAsState().value.unitName} - ${moveViewModel.moveUi.collectAsState().value.unitTypeName}"
            )
            Text(
                modifier = Modifier.padding(bottom = 8.dp),
                text = "Batas Keluar",
                style = TextStyle(color = FontBlack),
                fontSize = 16.sp
            )
            DateLayout(
                modifier = Modifier.fillMaxWidth(),
                value = if (moveViewModel.moveUi.collectAsState().value.limitCheckOut.isNotEmpty())
                    "${dateToDisplayMidFormat(moveViewModel.moveUi.collectAsState().value.limitCheckOut)} - ${
                        generateLimitText(
                            moveViewModel.moveUi.collectAsState().value.limitCheckOut
                        )
                    }" else "-"
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
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
                    title = currencyFormatterStringViewZero(moveViewModel.moveUi.collectAsState().value.currentDebtTenant.toString()),
                    fontSize = 14.sp,
                    backgroundColor = ColorRed
                )
            }

            Text(
                modifier = Modifier.padding(bottom = 4.dp),
                text = "Tanggal Pindah",
                style = TextStyle(color = FontBlack),
                fontSize = 16.sp
            )
            DateLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                value = if (moveViewModel.moveUi.collectAsState().value.moveDate.isNotEmpty()) dateToDisplayMidFormat(
                    moveViewModel.moveUi.collectAsState().value.moveDate
                ) else "-"
            )

            Text(
                text = "Status Unit Setelah Check Out",
                style = TextStyle(color = FontBlack),
                fontSize = 16.sp
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        moveViewModel.setStatusAfterCheckOut("Siap Digunakan")
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (moveViewModel.moveUi.collectAsState().value.statusAfterCheckOut == "Siap Digunakan"),
                    onClick = { moveViewModel.setStatusAfterCheckOut("Siap Digunakan") }
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
                        moveViewModel.setStatusAfterCheckOut("Pembersihan")
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (moveViewModel.moveUi.collectAsState().value.statusAfterCheckOut == "Pembersihan"),
                    onClick = { moveViewModel.setStatusAfterCheckOut("Pembersihan") }
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
                        moveViewModel.setStatusAfterCheckOut("Perbaikan")
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (moveViewModel.moveUi.collectAsState().value.statusAfterCheckOut == "Perbaikan"),
                    onClick = { moveViewModel.setStatusAfterCheckOut("Perbaikan") }
                )
                Text(
                    text = "Perbaikan", style = TextStyle(color = FontBlack),
                    fontSize = 16.sp
                )
            }
            if (moveViewModel.moveUi.collectAsState().value.statusAfterCheckOut != "Siap Digunakan") {
                MyOutlinedTextField(
                    label = "Catatan Perbaikan/Pembersihan Unit",
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    value = moveViewModel.moveUi.collectAsState().value.noteMaintenance,
                    onValueChange = {
                        moveViewModel.setNoteMaintenance(it)
                    },
                    isError = moveViewModel.isNoteMaintenanceValid.collectAsState().value.isError,
                    errorMessage = moveViewModel.isNoteMaintenanceValid.collectAsState().value.errorMessage,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = GreyLight,
                thickness = 2.dp
            )

            ComboBox(
                title = stringResource(id = R.string.subtitle_unit_move),
                value = "${moveViewModel.moveUi.collectAsState().value.unitNameMove} - ${moveViewModel.moveUi.collectAsState().value.unitTypeNameMove}",
                isError = moveViewModel.isUnitMoveSelectedValid.collectAsState().value.isError,
                errorMessage = moveViewModel.isUnitMoveSelectedValid.collectAsState().value.errorMessage
            ) {
                moveViewModel.getUnit()
            }

            Text(
                text = "Jenis Perpindahan",
                style = TextStyle(color = FontBlack),
                fontSize = 16.sp
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        moveViewModel.setMoveType("Gratis")
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (moveViewModel.moveUi.collectAsState().value.moveType == "Gratis"),
                    onClick = { moveViewModel.setMoveType("Gratis") }
                )
                Text(
                    text = "Gratis", style = TextStyle(color = FontBlack),
                    fontSize = 16.sp
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        moveViewModel.setMoveType("Downgrade")
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (moveViewModel.moveUi.collectAsState().value.moveType == "Downgrade"),
                    onClick = { moveViewModel.setMoveType("Downgrade") }
                )
                Text(
                    text = "Downgrade Kualitas Unit", style = TextStyle(color = FontBlack),
                    fontSize = 16.sp
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        moveViewModel.setMoveType("Upgrade")
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (moveViewModel.moveUi.collectAsState().value.moveType == "Upgrade"),
                    onClick = { moveViewModel.setMoveType("Upgrade") }
                )
                Text(
                    text = "Upgrade Kualitas Unit", style = TextStyle(color = FontBlack),
                    fontSize = 16.sp
                )
            }

            if (moveViewModel.moveUi.collectAsState().value.moveType == "Downgrade") {
                MyOutlinedTextFieldCurrency(
                    label = "Nominal Pengembalian Dana",
                    value = moveViewModel.moveUi.collectAsState().value.nominal.replace(
                        ".",
                        ""
                    ),
                    onValueChange = {
                        moveViewModel.setNominal(it)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth(),
                    currencyValue = moveViewModel.moveUi.collectAsState().value.nominal,
                    isError = moveViewModel.isNominalValid.collectAsState().value.isError,
                    errorMessage = moveViewModel.isNominalValid.collectAsState().value.errorMessage
                )

                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
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
                                    moveViewModel.setPaymentType(true)
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (moveViewModel.moveUi.collectAsState().value.isCash),
                                onClick = { moveViewModel.setPaymentType(true) }
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
                                    moveViewModel.setPaymentType(false)
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (!moveViewModel.moveUi.collectAsState().value.isCash),
                                onClick = { moveViewModel.setPaymentType(false) }
                            )
                            Text(
                                text = "Transfer", style = TextStyle(color = FontBlack),
                                fontSize = 16.sp
                            )
                        }
                    }
                }

            }

            if (moveViewModel.moveUi.collectAsState().value.moveType == "Upgrade") {
                MyOutlinedTextFieldCurrency(
                    label = "Nominal Biaya Pindah Kamar",
                    value = moveViewModel.moveUi.collectAsState().value.nominal.replace(
                        ".",
                        ""
                    ),
                    onValueChange = {
                        moveViewModel.setNominal(it)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth(),
                    currencyValue = moveViewModel.moveUi.collectAsState().value.nominal,
                    isError = moveViewModel.isNominalValid.collectAsState().value.isError,
                    errorMessage = moveViewModel.isNominalValid.collectAsState().value.errorMessage
                )

                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
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
                                    moveViewModel.setPaymentType(true)
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (moveViewModel.moveUi.collectAsState().value.isCash),
                                onClick = { moveViewModel.setPaymentType(true) }
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
                                    moveViewModel.setPaymentType(false)
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (!moveViewModel.moveUi.collectAsState().value.isCash),
                                onClick = { moveViewModel.setPaymentType(false) }
                            )
                            Text(
                                text = "Transfer", style = TextStyle(color = FontBlack),
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(id = R.string.payment_method),
                        style = TextStyle(color = FontBlack),
                        fontSize = 16.sp
                    )
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .weight(1F)
                                .clickable {
                                    moveViewModel.setPaymentMethod(true)
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (moveViewModel.moveUi.collectAsState().value.isFullPayment),
                                onClick = { moveViewModel.setPaymentMethod(true) }
                            )
                            Text(
                                text = "Lunas", style = TextStyle(color = FontBlack),
                                fontSize = 16.sp
                            )
                        }
                        Row(
                            modifier = Modifier
                                .weight(1F)
                                .clickable {
                                    moveViewModel.setPaymentMethod(false)
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (!moveViewModel.moveUi.collectAsState().value.isFullPayment),
                                onClick = { moveViewModel.setPaymentMethod(false) }
                            )
                            Text(
                                text = "Cicil/Angsuran", style = TextStyle(color = FontBlack),
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                if (!moveViewModel.moveUi.collectAsState().value.isFullPayment) {
                    Column {
                        MyOutlinedTextFieldCurrency(
                            label = "Uang Muka",
                            value = moveViewModel.moveUi.collectAsState().value.downPayment.replace(
                                ".",
                                ""
                            ),
                            onValueChange = {
                                moveViewModel.setDownPayment(it)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth(),
                            currencyValue = moveViewModel.moveUi.collectAsState().value.downPayment,
                            isError = moveViewModel.isDownPaymentValid.collectAsState().value.isError,
                            errorMessage = moveViewModel.isDownPaymentValid.collectAsState().value.errorMessage
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(id = R.string.debt_tenant_move),
                                style = TextStyle(color = FontBlack),
                                fontSize = 16.sp
                            )
                            BoxRectangle(
                                title = currencyFormatterStringViewZero(moveViewModel.moveUi.collectAsState().value.debtTenantMove),
                                fontSize = 14.sp,
                                backgroundColor = ColorRed
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(id = R.string.total_debt),
                                style = TextStyle(color = FontBlack),
                                fontSize = 16.sp
                            )
                            BoxRectangle(
                                title = currencyFormatterStringViewZero(moveViewModel.moveUi.collectAsState().value.totalDebtTenant),
                                fontSize = 14.sp,
                                backgroundColor = ColorRed
                            )
                        }
                        Divider(
                            modifier = Modifier.padding(top = 8.dp),
                            color = GreyLight,
                            thickness = 2.dp
                        )
                    }
                }

                Row(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        text = "Simpan Dana ",
                        style = TextStyle(color = FontBlack),
                        fontSize = 18.sp
                    )
                    Text(
                        text = currencyFormatterStringViewZero(moveViewModel.moveUi.collectAsState().value.totalPayment),
                        style = TextStyle(color = TealGreen, fontWeight = FontWeight.Medium),
                        fontSize = 18.sp
                    )
                }
            }

            Button(
                onClick = {
                    if (moveViewModel.checkLimitApp()) {
                        showBottomLimitApp(context)
                    } else {
                        if (moveViewModel.dataIsComplete()) {
                            showBottomConfirm(context, moveViewModel)
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

fun showBottomSheetUnit(
    moveViewModel: MoveViewModel,
    context: Context,
    data: List<UnitAdapter>
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
        val intent = Intent(context, AddUnitActivity::class.java)
        context.startActivity(intent)
        bottomSheetDialog.dismiss()
    }

    val adapter = amat.kelolakost.UnitAdapter {
        moveViewModel.setUnitSelected(it.id, it.name, it.unitTypeName)
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

private fun showBottomConfirm(
    context: Context,
    moveViewModel: MoveViewModel
) {
    val bottomSheetDialog = BottomSheetDialog(context)
    bottomSheetDialog.setContentView(R.layout.bottom_sheet_confirm)
    val message = bottomSheetDialog.findViewById<TextView>(R.id.text_message)
    val buttonOk = bottomSheetDialog.findViewById<Button>(R.id.ok_button)

    val messageString =
        "Proses Pindah penyewa ${moveViewModel.moveUi.value.tenantName} dari Unit ${moveViewModel.moveUi.value.unitName}-${moveViewModel.moveUi.value.unitTypeName} " +
                "ke Unit ${moveViewModel.moveUi.value.unitNameMove}-${moveViewModel.moveUi.value.unitTypeNameMove}?"


    message?.text = messageString

    buttonOk?.setOnClickListener {
        bottomSheetDialog.dismiss()
        moveViewModel.prosesCheckOut()
    }
    bottomSheetDialog.show()

}
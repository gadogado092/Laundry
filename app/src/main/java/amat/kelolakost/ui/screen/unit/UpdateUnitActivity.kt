package amat.kelolakost.ui.screen.unit

import amat.kelolakost.KostAdapter
import amat.kelolakost.R
import amat.kelolakost.UnitTypeAdapter
import amat.kelolakost.data.Kost
import amat.kelolakost.data.UnitType
import amat.kelolakost.di.Injection
import amat.kelolakost.ui.common.OnLifecycleEvent
import amat.kelolakost.ui.common.UiState
import amat.kelolakost.ui.component.ComboBox
import amat.kelolakost.ui.component.MyOutlinedTextField
import amat.kelolakost.ui.screen.kost.AddKostActivity
import amat.kelolakost.ui.screen.unit_type.AddUnitTypeActivity
import amat.kelolakost.ui.theme.FontWhite
import amat.kelolakost.ui.theme.GreenDark
import amat.kelolakost.ui.theme.GreyLight
import amat.kelolakost.ui.theme.GreyLight3
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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

class UpdateUnitActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        val id = intent.getStringExtra("id")

        setContent {
            val context = LocalContext.current
            KelolaKostTheme {
                if (id != null) {
                    UpdateUnitScreen(context, id)
                } else {
                    UpdateUnitScreen(context, "")
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
fun UpdateUnitScreen(
    context: Context,
    id: String,
    modifier: Modifier = Modifier
) {
    val updateUnitViewModel: UpdateUnitViewModel =
        viewModel(
            factory = UpdateUnitViewModelFactory(
                Injection.provideUnitRepository(context),
                Injection.provideKostRepository(context),
                Injection.provideUnitTypeRepository(context),
                Injection.provideBookingRepository(context)
            )
        )

    if (!updateUnitViewModel.isUpdateSuccess.collectAsState().value.isError) {
        Toast.makeText(context, "Proses Sukses", Toast.LENGTH_SHORT)
            .show()
        val activity = (context as? Activity)
        activity?.finish()
    } else {
        if (updateUnitViewModel.isUpdateSuccess.collectAsState().value.errorMessage.isNotEmpty()) {
            Toast.makeText(
                context,
                updateUnitViewModel.isUpdateSuccess.collectAsState().value.errorMessage,
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    OnLifecycleEvent { owner, event ->
        // do stuff on event
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                updateUnitViewModel.getDetail(id)
            }

            else -> {}
        }
    }

    updateUnitViewModel.stateListKost.collectAsState(initial = UiState.Error("")).value.let { uiState ->
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
                    updateUnitViewModel = updateUnitViewModel,
                    context = context,
                    uiState.data
                )
            }
        }
    }

    updateUnitViewModel.stateListUnitType.collectAsState(initial = UiState.Error("")).value.let { uiState ->
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
                    "Loading Data Tipe Unit",
                    Toast.LENGTH_SHORT
                ).show()
            }

            is UiState.Success -> {
                showBottomSheetUnitType(
                    updateUnitViewModel = updateUnitViewModel,
                    context = context,
                    uiState.data
                )
            }
        }
    }

    Column {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.title_update_unit),
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
                label = "Nama Kamar/Unit",
                value = updateUnitViewModel.unitUi.collectAsState().value.name,
                onValueChange = {
                    updateUnitViewModel.setUnitName(it)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                isError = updateUnitViewModel.isUnitNameValid.collectAsState().value.isError,
                errorMessage = updateUnitViewModel.isUnitNameValid.collectAsState().value.errorMessage
            )
            MyOutlinedTextField(
                label = "Keterangan Tambahan",
                value = updateUnitViewModel.unitUi.collectAsState().value.note,
                onValueChange = {
                    updateUnitViewModel.setNote(it)
                },
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth(),
                singleLine = false
            )
            ComboBox(
                title = stringResource(id = R.string.location_unit),
                value = updateUnitViewModel.unitUi.collectAsState().value.kostName,
                isError = updateUnitViewModel.isKostSelectedValid.collectAsState().value.isError,
                errorMessage = updateUnitViewModel.isKostSelectedValid.collectAsState().value.errorMessage
            ) {
                updateUnitViewModel.getKost()
            }
            ComboBox(
                title = stringResource(id = R.string.title_type_unit),
                value = updateUnitViewModel.unitUi.collectAsState().value.unitTypeName,
                isError = updateUnitViewModel.isUnitTypeSelectedValid.collectAsState().value.isError,
                errorMessage = updateUnitViewModel.isUnitTypeSelectedValid.collectAsState().value.errorMessage,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                updateUnitViewModel.getUnitType()
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )
            {
                OutlinedButton(
                    modifier = Modifier
                        .weight(1F),
                    onClick = {
                        if (updateUnitViewModel.isCanDelete()) {
                            showBottomConfirm(context, updateUnitViewModel)
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
                        updateUnitViewModel.prosesUpdate()
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = GreenDark)
                ) {
                    Text(text = stringResource(id = R.string.update), color = FontWhite)
                }
            }
        }
    }
}

fun showBottomSheetKost(
    updateUnitViewModel: UpdateUnitViewModel,
    context: Context,
    data: List<Kost>
) {
    val bottomSheetDialog = BottomSheetDialog(context)
    bottomSheetDialog.setContentView(R.layout.bottom_sheet_select_list)
    val title = bottomSheetDialog.findViewById<TextView>(R.id.text_title)
    val buttonAdd = bottomSheetDialog.findViewById<Button>(R.id.button_add)
    val recyclerView = bottomSheetDialog.findViewById<RecyclerView>(R.id.recyclerView)

    title?.setText(R.string.location_unit)
    buttonAdd?.setText(R.string.add)

    buttonAdd?.setOnClickListener {
        val intent = Intent(context, AddKostActivity::class.java)
        context.startActivity(intent)
        bottomSheetDialog.dismiss()
    }

    val adapter = KostAdapter {
        updateUnitViewModel.setKostSelected(it.id, it.name)
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

fun showBottomSheetUnitType(
    updateUnitViewModel: UpdateUnitViewModel,
    context: Context,
    data: List<UnitType>
) {
    val bottomSheetDialog = BottomSheetDialog(context)
    bottomSheetDialog.setContentView(R.layout.bottom_sheet_select_list)
    val title = bottomSheetDialog.findViewById<TextView>(R.id.text_title)
    val buttonAdd = bottomSheetDialog.findViewById<Button>(R.id.button_add)
    val recyclerView = bottomSheetDialog.findViewById<RecyclerView>(R.id.recyclerView)

    title?.setText(R.string.title_type_unit)
    buttonAdd?.setText(R.string.add)

    buttonAdd?.setOnClickListener {
        val intent = Intent(context, AddUnitTypeActivity::class.java)
        context.startActivity(intent)
        bottomSheetDialog.dismiss()
    }

    val adapter = UnitTypeAdapter {
        updateUnitViewModel.setUnitTypeSelected(it.id, it.name)
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

private fun showBottomConfirm(
    context: Context,
    updateUnitViewModel: UpdateUnitViewModel
) {
    val bottomSheetDialog = BottomSheetDialog(context)
    bottomSheetDialog.setContentView(R.layout.bottom_sheet_confirm)
    val message = bottomSheetDialog.findViewById<TextView>(R.id.text_message)
    val buttonOk = bottomSheetDialog.findViewById<Button>(R.id.ok_button)

    val messageString =
        "Lanjutkan Hapus Unit?"

    message?.text = messageString

    buttonOk?.setOnClickListener {
        bottomSheetDialog.dismiss()
        updateUnitViewModel.deleteUnit()
    }
    bottomSheetDialog.show()

}
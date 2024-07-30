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
import androidx.compose.ui.text.input.KeyboardCapitalization
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

class AddUnitActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        val kostId = intent.getStringExtra("kostId")
        val kostName = intent.getStringExtra("kostName")

        setContent {
            val context = LocalContext.current
            KelolaKostTheme {

                var kostIdScreen = ""
                var kostNameScreen = ""
                if (kostName != null && kostId != null) {
                    kostIdScreen = kostId
                    kostNameScreen = kostName
                }
                AddUnitScreen(
                    context = context,
                    kostId = kostIdScreen,
                    kostName = kostNameScreen
                )
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
fun AddUnitScreen(
    modifier: Modifier = Modifier,
    context: Context,
    kostId: String = "",
    kostName: String = ""
) {
    val addUnitViewModel: AddUnitViewModel =
        viewModel(
            factory = AddUnitViewModelFactory(
                Injection.provideUnitRepository(context),
                Injection.provideKostRepository(context),
                Injection.provideUnitTypeRepository(context)
            )
        )

    if (!addUnitViewModel.isInsertSuccess.collectAsState().value.isError) {
        Toast.makeText(context, stringResource(id = R.string.success_add_data), Toast.LENGTH_SHORT)
            .show()
        val activity = (context as? Activity)
        activity?.finish()
    } else {
        if (addUnitViewModel.isInsertSuccess.collectAsState().value.errorMessage.isNotEmpty()) {
            Toast.makeText(
                context,
                addUnitViewModel.isInsertSuccess.collectAsState().value.errorMessage,
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    OnLifecycleEvent { owner, event ->
        // do stuff on event
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                if (kostId != "" && kostName != "") {
                    addUnitViewModel.setKostSelected(kostId, kostName)
                }

            }

            else -> {}
        }
    }

    addUnitViewModel.stateListKost.collectAsState(initial = UiState.Error("")).value.let { uiState ->
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
                    addUnitViewModel = addUnitViewModel,
                    context = context,
                    uiState.data
                )
            }
        }
    }

    addUnitViewModel.stateListUnitType.collectAsState(initial = UiState.Error("")).value.let { uiState ->
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
                    addUnitViewModel = addUnitViewModel,
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
                    text = stringResource(id = R.string.title_add_unit),
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
            MyOutlinedTextField(
                label = "Nama Kamar/Unit",
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                value = addUnitViewModel.unitUi.collectAsState().value.name,
                onValueChange = {
                    addUnitViewModel.setUnitName(it)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                isError = addUnitViewModel.isUnitNameValid.collectAsState().value.isError,
                errorMessage = addUnitViewModel.isUnitNameValid.collectAsState().value.errorMessage
            )
            MyOutlinedTextField(
                label = "Keterangan Tambahan",
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                value = addUnitViewModel.unitUi.collectAsState().value.note,
                onValueChange = {
                    addUnitViewModel.setNote(it)
                },
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth(),
                singleLine = false
            )
            ComboBox(
                title = stringResource(id = R.string.location_unit),
                value = addUnitViewModel.unitUi.collectAsState().value.kostName,
                isError = addUnitViewModel.isKostSelectedValid.collectAsState().value.isError,
                errorMessage = addUnitViewModel.isKostSelectedValid.collectAsState().value.errorMessage
            ) {
                addUnitViewModel.getKost()
            }
            ComboBox(
                title = stringResource(id = R.string.title_type_unit),
                value = addUnitViewModel.unitUi.collectAsState().value.unitTypeName,
                isError = addUnitViewModel.isUnitTypeSelectedValid.collectAsState().value.isError,
                errorMessage = addUnitViewModel.isUnitTypeSelectedValid.collectAsState().value.errorMessage,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                addUnitViewModel.getUnitType()
            }
            Button(
                onClick = {
                    addUnitViewModel.prosesInsert()
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

fun showBottomSheetKost(addUnitViewModel: AddUnitViewModel, context: Context, data: List<Kost>) {
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
        addUnitViewModel.setKostSelected(it.id, it.name)
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
    addUnitViewModel: AddUnitViewModel,
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
        addUnitViewModel.setUnitTypeSelected(it.id, it.name)
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
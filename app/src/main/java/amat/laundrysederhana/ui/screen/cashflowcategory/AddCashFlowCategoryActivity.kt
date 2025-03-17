package amat.laundrysederhana.ui.screen.cashflowcategory

import amat.laundrysederhana.R
import amat.laundrysederhana.di.Injection
import amat.laundrysederhana.ui.common.OnLifecycleEvent
import amat.laundrysederhana.ui.common.UiState
import amat.laundrysederhana.ui.component.ErrorLayout
import amat.laundrysederhana.ui.component.LoadingLayout
import amat.laundrysederhana.ui.component.MyOutlinedTextField
import amat.laundrysederhana.ui.theme.FontBlackSoft
import amat.laundrysederhana.ui.theme.FontWhite
import amat.laundrysederhana.ui.theme.GreenDark
import amat.laundrysederhana.ui.theme.GreyLight
import amat.laundrysederhana.ui.theme.LaundryAppTheme
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.google.android.material.bottomsheet.BottomSheetDialog

class AddCashFlowCategoryActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        val id = intent.getStringExtra("id")

        setContent {
            val context = LocalContext.current
            LaundryAppTheme {
                if (id != null) {
                    AddCashFlowCategoryScreen(context, id)
                } else {
                    AddCashFlowCategoryScreen(context, "")
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
fun AddCashFlowCategoryScreen(
    context: Context,
    id: String
) {

    val viewModel: AddCashFlowCategoryViewModel =
        viewModel(
            factory = AddCashFlowCategoryViewModelFactory(
                Injection.provideCashFlowCategoryRepository(context)
            )
        )

    OnLifecycleEvent { owner, event ->
        // do stuff on event
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                viewModel.getCategory(id)
            }

            else -> { /* other stuff */
            }
        }
    }

    if (!viewModel.isProsesFailed.collectAsState().value.isError) {
        Toast.makeText(context, "Berhasil Simpan Data", Toast.LENGTH_SHORT)
            .show()
        val activity = (context as? Activity)
        activity?.finish()
    } else {
        if (viewModel.isProsesFailed.collectAsState().value.errorMessage.isNotEmpty()) {
            Toast.makeText(
                context,
                viewModel.isProsesFailed.collectAsState().value.errorMessage,
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    if (!viewModel.isProsesDeleteFailed.collectAsState().value.isError) {
        Toast.makeText(context, "Berhasil Hapus Data", Toast.LENGTH_SHORT)
            .show()
        val activity = (context as? Activity)
        activity?.finish()
    } else {
        if (viewModel.isProsesDeleteFailed.collectAsState().value.errorMessage.isNotEmpty()) {
            Toast.makeText(
                context,
                viewModel.isProsesDeleteFailed.collectAsState().value.errorMessage,
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    //START UI
    Column {
        TopAppBar(
            title = {
                Text(
                    text = if (id == "") stringResource(id = R.string.add_category) else stringResource(
                        id = R.string.update_category
                    ),
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
            },
        )

        viewModel.stateCategory.collectAsState(initial = UiState.Loading).value.let { uiState ->
            when (uiState) {
                is UiState.Error -> {
                    ErrorLayout(
                        modifier = Modifier.fillMaxHeight(),
                        errorMessage = uiState.errorMessage
                    ) {
                        viewModel.getCategory(id)
                    }
                }

                UiState.Loading -> {
                    LoadingLayout(modifier = Modifier.fillMaxHeight())
                }

                is UiState.Success -> {
                    FormCategory(context, viewModel)
                }

            }
        }

    }

}

@Composable
fun FormCategory(context: Context, viewModel: AddCashFlowCategoryViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        MyOutlinedTextField(
            label = "Nama Kategori",
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            value = viewModel.stateUi.collectAsState().value.name,
            onValueChange = {
                viewModel.setCategoryName(it)
            },
            modifier = Modifier.fillMaxWidth(),
            isError = viewModel.isCategoryNameValid.collectAsState().value.isError,
            errorMessage = viewModel.isCategoryNameValid.collectAsState().value.errorMessage
        )

        MyOutlinedTextField(
            label = "Nama Satuan/Unit",
            value = viewModel.stateUi.collectAsState().value.unit,
            onValueChange = {
                viewModel.setUnitName(it)
            },
            modifier = Modifier.fillMaxWidth(),
            isError = viewModel.isUnitNameValid.collectAsState().value.isError,
            errorMessage = viewModel.isUnitNameValid.collectAsState().value.errorMessage
        )

        Button(
            onClick = {
                if (viewModel.dataIsComplete()) {
                    showBottomConfirm(
                        context,
                        viewModel
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = GreenDark)
        ) {
            Text(text = "Simpan", color = FontWhite)
        }

        if (viewModel.stateUi.collectAsState().value.id.isNotEmpty()) {
            Button(
                onClick = {
                    if (viewModel.dataDeleteIsComplete()) {
                        showBottomConfirmDelete(context, viewModel)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = GreyLight
                )
            ) {
                Text(text = "Hapus Data", color = FontBlackSoft)
            }
        }
    }
}

private fun showBottomConfirm(
    context: Context,
    viewModel: AddCashFlowCategoryViewModel
) {
    val bottomSheetDialog = BottomSheetDialog(context)
    bottomSheetDialog.setContentView(R.layout.bottom_sheet_confirm)
    val message = bottomSheetDialog.findViewById<TextView>(R.id.text_message)
    val buttonOk = bottomSheetDialog.findViewById<Button>(R.id.ok_button)

    val messageString =
        "Yakin Simpan Data?"

    message?.text = messageString

    buttonOk?.setOnClickListener {
        bottomSheetDialog.dismiss()
        viewModel.process()
    }
    bottomSheetDialog.show()

}

private fun showBottomConfirmDelete(
    context: Context,
    viewModel: AddCashFlowCategoryViewModel
) {
    val bottomSheetDialog = BottomSheetDialog(context)
    bottomSheetDialog.setContentView(R.layout.bottom_sheet_confirm)
    val message = bottomSheetDialog.findViewById<TextView>(R.id.text_message)
    val buttonOk = bottomSheetDialog.findViewById<Button>(R.id.ok_button)

    val messageString =
        "Yakin Hapus Data?"

    message?.text = messageString

    buttonOk?.setOnClickListener {
        bottomSheetDialog.dismiss()
        viewModel.processDelete()
    }
    bottomSheetDialog.show()

}
package amat.laundrysederhana.ui.screen.product

import amat.laundrysederhana.CategoryAdapter
import amat.laundrysederhana.R
import amat.laundrysederhana.currencyFormatterString
import amat.laundrysederhana.data.Category
import amat.laundrysederhana.di.Injection
import amat.laundrysederhana.ui.common.OnLifecycleEvent
import amat.laundrysederhana.ui.common.UiState
import amat.laundrysederhana.ui.component.ComboBox
import amat.laundrysederhana.ui.component.ErrorLayout
import amat.laundrysederhana.ui.component.LoadingLayout
import amat.laundrysederhana.ui.component.MyOutlinedTextField
import amat.laundrysederhana.ui.component.MyOutlinedTextFieldCurrency
import amat.laundrysederhana.ui.screen.category.AddCategoryActivity
import amat.laundrysederhana.ui.theme.FontBlackSoft
import amat.laundrysederhana.ui.theme.FontWhite
import amat.laundrysederhana.ui.theme.GreenDark
import amat.laundrysederhana.ui.theme.GreyLight
import amat.laundrysederhana.ui.theme.LaundryAppTheme
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

class AddProductActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        val id = intent.getStringExtra("id")

        setContent {
            val context = LocalContext.current
            LaundryAppTheme {
                if (id != null) {
                    AddProductScreen(context, id)
                } else {
                    AddProductScreen(context, "")
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
fun AddProductScreen(
    context: Context,
    id: String
) {
    val viewModel: AddProductViewModel =
        viewModel(
            factory = AddProductViewModelFactory(
                Injection.provideProductRepository(context),
                Injection.provideCategoryRepository(context)
            )
        )

    OnLifecycleEvent { owner, event ->
        // do stuff on event
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                viewModel.getProduct(id)
            }

            else -> { /* other stuff */
            }
        }
    }

    //catch get Kost result
    viewModel.stateListCategory.collectAsState(initial = UiState.Error("")).value.let { uiState ->
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
                showBottomSheetCategory(
                    viewModel = viewModel,
                    context = context,
                    uiState.data
                )
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
                    text = if (id == "") stringResource(id = R.string.add_product) else stringResource(
                        id = R.string.update_product
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

        viewModel.stateProduct.collectAsState(initial = UiState.Loading).value.let { uiState ->
            when (uiState) {
                is UiState.Error -> {
                    ErrorLayout(
                        modifier = Modifier.fillMaxHeight(),
                        errorMessage = uiState.errorMessage
                    ) {
                        viewModel.getProduct(id)
                    }
                }

                UiState.Loading -> {
                    LoadingLayout(modifier = Modifier.fillMaxHeight())
                }

                is UiState.Success -> {
                    FormProduct(context, viewModel)
                }

            }
        }

    }
}

@Composable
fun FormProduct(context: Context, viewModel: AddProductViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        MyOutlinedTextField(
            label = "Nama Layanan/Produk",
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            value = viewModel.stateUi.collectAsState().value.name,
            onValueChange = {
                viewModel.setProductName(it)
            },
            modifier = Modifier.fillMaxWidth(),
            isError = viewModel.isProductNameValid.collectAsState().value.isError,
            errorMessage = viewModel.isProductNameValid.collectAsState().value.errorMessage
        )
        Spacer(modifier = Modifier.height(8.dp))
        MyOutlinedTextFieldCurrency(
            label = "Harga Layanan/Produk",
            value = viewModel.stateUi.collectAsState().value.price,
            onValueChange = {
                viewModel.setProductPrice(it)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth(),
            isError = viewModel.isProductPriceValid.collectAsState().value.isError,
            errorMessage = viewModel.isProductPriceValid.collectAsState().value.errorMessage,
            currencyValue = currencyFormatterString(viewModel.stateUi.collectAsState().value.price)
        )
        Spacer(modifier = Modifier.height(8.dp))
        ComboBox(
            title = stringResource(id = R.string.title_category),
            value = viewModel.stateUi.collectAsState().value.categoryName,
            isError = viewModel.isCategorySelectedValid.collectAsState().value.isError,
            errorMessage = viewModel.isCategorySelectedValid.collectAsState().value.errorMessage
        ) {
            viewModel.getCategory()
        }

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
    viewModel: AddProductViewModel
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
    viewModel: AddProductViewModel
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

fun showBottomSheetCategory(
    viewModel: AddProductViewModel,
    context: Context,
    data: List<Category>
) {
    val bottomSheetDialog = BottomSheetDialog(context)
    bottomSheetDialog.setContentView(R.layout.bottom_sheet_select_list)
    val title = bottomSheetDialog.findViewById<TextView>(R.id.text_title)
    val textEmpty = bottomSheetDialog.findViewById<TextView>(R.id.text_empty)
    val buttonAdd = bottomSheetDialog.findViewById<Button>(R.id.button_add)
    val recyclerView = bottomSheetDialog.findViewById<RecyclerView>(R.id.recyclerView)

    title?.setText(R.string.title_category)
    buttonAdd?.setText(R.string.add)

    if (data.isEmpty()) {
        textEmpty?.visibility = View.VISIBLE
    }

    buttonAdd?.setOnClickListener {
        val intent = Intent(context, AddCategoryActivity::class.java)
        context.startActivity(intent)
        bottomSheetDialog.dismiss()
    }

    val adapter = CategoryAdapter {
        viewModel.setCategorySelected(it.id, it.name)
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
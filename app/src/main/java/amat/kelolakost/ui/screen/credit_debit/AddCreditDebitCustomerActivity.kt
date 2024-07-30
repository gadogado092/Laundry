package amat.kelolakost.ui.screen.credit_debit

import amat.kelolakost.R
import amat.kelolakost.di.Injection
import amat.kelolakost.ui.component.MyOutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.viewmodel.compose.viewModel

class AddCreditDebitCustomerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            KelolaKostTheme {
                AddCreditDebitCustomerScreen(context = context)
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
fun AddCreditDebitCustomerScreen(
    modifier: Modifier = Modifier,
    context: Context
) {

    val myViewModel: AddCreditDebitCustomerViewModel =
        viewModel(
            factory = AddCreditDebitCustomerViewModelFactory(
                Injection.provideCustomerCreditDebitRepository(
                    context
                )
            )
        )

    if (!myViewModel.isProsesSuccess.collectAsState().value.isError) {
        Toast.makeText(context, stringResource(id = R.string.success_add_data_customer), Toast.LENGTH_SHORT)
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
                    text = stringResource(id = R.string.title_add_debt_credit_customer),
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
                label = "Nama Pelanggan",
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                value = myViewModel.stateUi.collectAsState().value.name,
                onValueChange = {
                    myViewModel.setName(it)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                isError = myViewModel.isNameValid.collectAsState().value.isError,
                errorMessage = myViewModel.isNameValid.collectAsState().value.errorMessage
            )
            MyOutlinedTextField(
                label = "Nomor Handphone",
                value = myViewModel.stateUi.collectAsState().value.numberPhone,
                onValueChange = {
                    myViewModel.setNumberPhone(it)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = myViewModel.isNumberPhoneValid.collectAsState().value.isError,
                errorMessage = myViewModel.isNumberPhoneValid.collectAsState().value.errorMessage
            )
            MyOutlinedTextField(
                label = "Alamat Email",
                value = myViewModel.stateUi.collectAsState().value.email,
                onValueChange = {
                    myViewModel.setEmail(it)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = myViewModel.isEmailValid.collectAsState().value.isError,
                errorMessage = myViewModel.isEmailValid.collectAsState().value.errorMessage
            )
            MyOutlinedTextField(
                label = "Keterangan Tambahan",
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                value = myViewModel.stateUi.collectAsState().value.note,
                onValueChange = {
                    myViewModel.setNote(it)
                },
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth(),
                singleLine = false
            )

            Button(
                onClick = {
                    myViewModel.prosesInsert()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = GreenDark)
            ) {
                Text(text = stringResource(id = R.string.save), color = FontWhite)
            }

        }

    }
}
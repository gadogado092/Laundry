package amat.laundry.ui.screen.user

import amat.laundry.R
import amat.laundry.di.Injection
import amat.laundry.ui.common.OnLifecycleEvent
import amat.laundry.ui.common.UiState
import amat.laundry.ui.component.ErrorLayout
import amat.laundry.ui.component.InformationBox
import amat.laundry.ui.component.LoadingLayout
import amat.laundry.ui.component.MyOutlinedTextField
import amat.laundry.ui.theme.FontBlack
import amat.laundry.ui.theme.FontWhite
import amat.laundry.ui.theme.GreenDark
import amat.laundry.ui.theme.LaundryAppTheme
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

class UpdateUserActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            LaundryAppTheme {
                UpdateUserScreen(context)
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
fun UpdateUserScreen(
    context: Context
) {
    val updateUserViewModel: UpdateUserViewModel =
        viewModel(factory = UpdateUserViewModelFactory(Injection.provideUserRepository(context)))

    if (!updateUserViewModel.isProsesSuccess.collectAsState().value.isError) {
        Toast.makeText(
            context,
            stringResource(id = R.string.success_update_data),
            Toast.LENGTH_SHORT
        )
            .show()
        val activity = (context as? Activity)
        activity?.finish()
    } else {
        if (updateUserViewModel.isProsesSuccess.collectAsState().value.errorMessage.isNotEmpty()) {
            Toast.makeText(
                context,
                updateUserViewModel.isProsesSuccess.collectAsState().value.errorMessage,
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    OnLifecycleEvent { owner, event ->
        // do stuff on event
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                updateUserViewModel.getDetail()
            }

            else -> {}
        }
    }

    Column {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.title_update_profil),
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

        updateUserViewModel.stateInitUser.collectAsState(initial = UiState.Loading).value.let { uiState ->
            when (uiState) {
                is UiState.Error -> {
                    ErrorLayout(
                        modifier = Modifier.fillMaxHeight(),
                        errorMessage = uiState.errorMessage
                    ) {
                        updateUserViewModel.getDetail()
                    }
                }

                UiState.Loading -> {
                    LoadingLayout(modifier = Modifier.fillMaxHeight())
                }

                is UiState.Success -> {
                    FormUpdate(updateUserViewModel)
                }
            }
        }

    }
}

@Composable
fun FormUpdate(updateUserViewModel: UpdateUserViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        MyOutlinedTextField(
            label = "Nama Usaha",
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            value = updateUserViewModel.user.collectAsState().value.businessName,
            onValueChange = {
                updateUserViewModel.setBusinessName(it)
            },
            modifier = Modifier.fillMaxWidth(),
            isError = updateUserViewModel.isBusinessNameValid.collectAsState().value.isError,
            errorMessage = updateUserViewModel.isBusinessNameValid.collectAsState().value.errorMessage
        )

        MyOutlinedTextField(
            label = "Nomor Handphone",
            value = updateUserViewModel.user.collectAsState().value.numberPhone,
            onValueChange = {
                updateUserViewModel.setNumberPhone(it)
            },
            modifier = Modifier
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            isError = updateUserViewModel.isUserNumberPhoneValid.collectAsState().value.isError,
            errorMessage = updateUserViewModel.isUserNumberPhoneValid.collectAsState().value.errorMessage
        )

        MyOutlinedTextField(
            label = "Alamat",
            value = updateUserViewModel.user.collectAsState().value.address,
            onValueChange = {
                updateUserViewModel.setAddress(it)
            },
            modifier = Modifier
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = updateUserViewModel.isAddressValid.collectAsState().value.isError,
            errorMessage = updateUserViewModel.isAddressValid.collectAsState().value.errorMessage
        )
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Tipe Whatsapp", color = FontBlack)
            Row(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .weight(1F)
                        .clickable {
                            updateUserViewModel.setTypeWa("Standard")
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = ("Standard" == updateUserViewModel.user.collectAsState().value.typeWa),
                        onClick = { updateUserViewModel.setTypeWa("Standard") }
                    )
                    Text(text = "Standard", color = FontBlack)
                }
                Row(
                    modifier = Modifier
                        .weight(1F)
                        .clickable {
                            updateUserViewModel.setTypeWa("Business")
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = ("Business" == updateUserViewModel.user.collectAsState().value.typeWa),
                        onClick = { updateUserViewModel.setTypeWa("Business") }
                    )
                    Text(text = "Business", color = FontBlack)
                }
            }
        }
//        InformationBox(value = "Informasi Nama Bank, Nomor Rekening dan Catatan akan muncul saat mengirim penagihan ke penyewa")
//        MyOutlinedTextField(
//            label = "Nama Bank / Nama Dompet Digital",
//            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
//            value = updateUserViewModel.user.collectAsState().value.bankName,
//            onValueChange = {
//                updateUserViewModel.setBankName(it)
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 8.dp),
//        )
//        MyOutlinedTextField(
//            label = "No.Rekening / No.Account",
//            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
//            value = updateUserViewModel.user.collectAsState().value.accountNumber,
//            onValueChange = {
//                updateUserViewModel.setAccountNumber(it)
//            },
//            modifier = Modifier.fillMaxWidth(),
//        )
//        MyOutlinedTextField(
//            label = "Nama Pemilik Rekening/Dompet Digital",
//            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
//            value = updateUserViewModel.user.collectAsState().value.accountOwnerName,
//            onValueChange = {
//                updateUserViewModel.setAccountOwnerName(it)
//            },
//            modifier = Modifier.fillMaxWidth(),
//        )
//        MyOutlinedTextField(
//            label = "Catatan Tambahan",
//            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
//            value = updateUserViewModel.user.collectAsState().value.note,
//            onValueChange = {
//                updateUserViewModel.setNoteBank(it)
//            },
//            modifier = Modifier.fillMaxWidth(),
//        )

        Button(
            onClick = {
                updateUserViewModel.prosesUpdate()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = GreenDark)
        ) {
            Text(text = stringResource(id = R.string.update), color = FontWhite)
        }

    }
}
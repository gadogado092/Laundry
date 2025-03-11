package amat.laundry.ui.screen.user

import amat.laundry.R
import amat.laundry.di.Injection
import amat.laundry.ui.component.MyOutlinedTextField
import amat.laundry.ui.screen.main.MainActivity
import amat.laundry.ui.theme.FontBlack
import amat.laundry.ui.theme.FontWhite
import amat.laundry.ui.theme.GreenDark
import amat.laundry.ui.theme.LaundryAppTheme
import android.app.Activity
import android.content.Intent
import android.os.Bundle
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
import androidx.compose.material.RadioButton
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

class NewUserActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LaundryAppTheme {
                NewUserScreen()
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
fun NewUserScreen() {

    val context = LocalContext.current
    val userViewModel: NewUserViewModel =
        viewModel(
            factory = NewUserViewModelFactory(
                Injection.provideUserRepository(context)
            )
        )

    if (!userViewModel.isProsesSuccess.collectAsState().value.isError) {
        Toast.makeText(context, "Pendaftaran Berhasil Dilakukan", Toast.LENGTH_SHORT)
            .show()
        val activity = (context as? Activity)
        val intentMainActivity = Intent(context, MainActivity::class.java)
        context.startActivity(intentMainActivity)
        activity?.finish()
    } else {
        if (userViewModel.isProsesSuccess.collectAsState().value.errorMessage.isNotEmpty()) {
            Toast.makeText(
                context,
                userViewModel.isProsesSuccess.collectAsState().value.errorMessage,
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    Column {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.title_new_user),
                    color = FontWhite,
                    fontSize = 22.sp
                )
            },
            backgroundColor = GreenDark,
        )
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
                value = userViewModel.user.collectAsState().value.businessName,
                onValueChange = {
                    userViewModel.setBusinessName(it)
                },
                modifier = Modifier.fillMaxWidth(),
                isError = userViewModel.isBusinessNameValid.collectAsState().value.isError,
                errorMessage = userViewModel.isBusinessNameValid.collectAsState().value.errorMessage
            )

            MyOutlinedTextField(
                label = "Nomor Handphone",
                value = userViewModel.user.collectAsState().value.numberPhone,
                onValueChange = {
                    userViewModel.setNumberPhone(it)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = userViewModel.isUserNumberPhoneValid.collectAsState().value.isError,
                errorMessage = userViewModel.isUserNumberPhoneValid.collectAsState().value.errorMessage
            )

            MyOutlinedTextField(
                label = "Alamat",
                value = userViewModel.user.collectAsState().value.address,
                onValueChange = {
                    userViewModel.setAddress(it)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                isError = userViewModel.isAddressValid.collectAsState().value.isError,
                errorMessage = userViewModel.isAddressValid.collectAsState().value.errorMessage
            )
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Tipe Whatsapp", color = FontBlack)
                Row(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .weight(1F)
                            .clickable {
                                userViewModel.setTypeWa("Standard")
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = ("Standard" == userViewModel.user.collectAsState().value.typeWa),
                            onClick = { userViewModel.setTypeWa("Standard") }
                        )
                        Text(text = "Standard", color = FontBlack)
                    }
                    Row(
                        modifier = Modifier
                            .weight(1F)
                            .clickable {
                                userViewModel.setTypeWa("Business")
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = ("Business" == userViewModel.user.collectAsState().value.typeWa),
                            onClick = { userViewModel.setTypeWa("Business") }
                        )
                        Text(text = "Business", color = FontBlack)
                    }
                }
            }
            Button(
                onClick = {
                    userViewModel.prosesRegistration()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = GreenDark)
            ) {
                Text(text = "DAFTAR SEKARANG", color = FontWhite)
            }


        }
    }
}
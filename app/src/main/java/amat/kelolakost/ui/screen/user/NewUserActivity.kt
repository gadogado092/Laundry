package amat.kelolakost.ui.screen.user

import amat.kelolakost.R
import amat.kelolakost.di.Injection
import amat.kelolakost.ui.component.InformationBox
import amat.kelolakost.ui.component.MyOutlinedTextField
import amat.kelolakost.ui.screen.main.MainActivity
import amat.kelolakost.ui.theme.FontWhite
import amat.kelolakost.ui.theme.GreenDark
import amat.kelolakost.ui.theme.KelolaKostTheme
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
            KelolaKostTheme {
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
                Injection.provideUserRepository(context),
                Injection.provideKostRepository(context),
                Injection.provideUnitStatusRepository(context),
                Injection.provideUnitTypeRepository(context),
                Injection.provideTenantRepository(context),
                Injection.provideUnitRepository(context),
                Injection.provideCreditTenantRepository(context),
                Injection.provideCreditDebitRepository(context),
                Injection.provideCustomerCreditDebitRepository(context),
                Injection.provideBookingRepository(context)
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
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {

            MyOutlinedTextField(
                label = "Nama Pemilik",
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                value = userViewModel.user.collectAsState().value.name,
                onValueChange = {
                    userViewModel.setName(it)
                },
                modifier = Modifier.fillMaxWidth(),
                isError = userViewModel.isUserNameValid.collectAsState().value.isError,
                errorMessage = userViewModel.isUserNameValid.collectAsState().value.errorMessage
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
                label = "Alamat Email",
                value = userViewModel.user.collectAsState().value.email,
                onValueChange = {
                    userViewModel.setEmail(it)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = userViewModel.isUserEmailValid.collectAsState().value.isError,
                errorMessage = userViewModel.isUserEmailValid.collectAsState().value.errorMessage
            )
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Tipe Whatsapp")
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
                        Text(text = "Standard")
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
                        Text(text = "Business")
                    }
                }
            }
            MyOutlinedTextField(
                label = "Nama Kost/Kontrakan/Penginapan",
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                value = userViewModel.kost.collectAsState().value.name,
                onValueChange = {
                    userViewModel.setKostName(it)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                isError = userViewModel.isKostNameValid.collectAsState().value.isError,
                errorMessage = userViewModel.isKostNameValid.collectAsState().value.errorMessage
            )

            MyOutlinedTextField(
                label = "Alamat Kost/Kontrakan/Penginapan",
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                value = userViewModel.kost.collectAsState().value.address,
                onValueChange = {
                    userViewModel.setKostAddress(it)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                isError = userViewModel.isKostAddressValid.collectAsState().value.isError,
                errorMessage = userViewModel.isKostAddressValid.collectAsState().value.errorMessage
            )
            MyOutlinedTextField(
                label = "Keterangan Tambahan",
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                value = userViewModel.kost.collectAsState().value.note,
                onValueChange = {
                    userViewModel.setNote(it)
                },
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth(),
                singleLine = false
            )
            InformationBox(value = "Informasi Nama Bank, Nomor Rekening dan Catatan akan muncul saat mengirim penagihan ke penyewa")
            MyOutlinedTextField(
                label = "Nama Bank / Nama Dompet Digital",
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                value = userViewModel.user.collectAsState().value.bankName,
                onValueChange = {
                    userViewModel.setBankName(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            )
            MyOutlinedTextField(
                label = "No.Rekening / No.Account",
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                value = userViewModel.user.collectAsState().value.accountNumber,
                onValueChange = {
                    userViewModel.setAccountNumber(it)
                },
                modifier = Modifier.fillMaxWidth(),
            )
            MyOutlinedTextField(
                label = "Nama Pemilik Rekening/Dompet Digital",
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                value = userViewModel.user.collectAsState().value.accountOwnerName,
                onValueChange = {
                    userViewModel.setAccountOwnerName(it)
                },
                modifier = Modifier.fillMaxWidth(),
            )
            MyOutlinedTextField(
                label = "Catatan Tambahan",
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                value = userViewModel.user.collectAsState().value.note,
                onValueChange = {
                    userViewModel.setNoteBank(it)
                },
                modifier = Modifier.fillMaxWidth(),
            )
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
package amat.kelolakost.ui.screen.user

import amat.kelolakost.R
import amat.kelolakost.di.Injection
import amat.kelolakost.ui.component.MyOutlinedTextField
import amat.kelolakost.ui.theme.FontWhite
import amat.kelolakost.ui.theme.GreenDark
import amat.kelolakost.ui.theme.KelolaKostTheme
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

class NewUserActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KelolaKostTheme {
                NewUserScreen()
            }
        }
    }
}

@Composable
fun NewUserScreen() {

    val context = LocalContext.current
    val _userViewModel: UserViewModel =
        viewModel(
            factory = UserViewModelFactory(
                Injection.provideUserRepository(context),
                Injection.provideKostRepository(context)
            )
        )

    Column {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.title_new_user),
                    color = FontWhite,
                    fontSize = 20.sp
                )
            },
            backgroundColor = GreenDark,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            MyOutlinedTextField(
                label = "Nama Pemilik",
                value = _userViewModel.user.collectAsState().value.name,
                onValueChange = {
                    _userViewModel.setName(it)
                },
                modifier = Modifier.fillMaxWidth(),
            )

            MyOutlinedTextField(
                label = "Nomor Handphone",
                value = _userViewModel.user.collectAsState().value.numberPhone,
                onValueChange = {
                    _userViewModel.setNumberPhone(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            MyOutlinedTextField(
                label = "Alamat Email",
                value = _userViewModel.user.collectAsState().value.email,
                onValueChange = {
                    _userViewModel.setEmail(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            MyOutlinedTextField(
                label = "Nama Kost/Kontrakan/Penginapan",
                value = _userViewModel.kost.collectAsState().value.name,
                onValueChange = {
                    _userViewModel.setKostName(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            )

            MyOutlinedTextField(
                label = "Alamat Kost/Kontrakan/Penginapan",
                value = _userViewModel.kost.collectAsState().value.address,
                onValueChange = {
                    _userViewModel.setKostAddress(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
            MyOutlinedTextField(
                label = "Keterangan Tambahan",
                value = _userViewModel.kost.collectAsState().value.note,
                onValueChange = {
                    _userViewModel.setNote(it)
                },
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                singleLine = false
            )
            Button(
                onClick = {
//                    viewModel.isEntryValid()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = GreenDark)
            ) {
                Text(text = "Daftar Sekarang", color = FontWhite)
            }


        }
    }
}

fun checkData() {

}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
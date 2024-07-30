package amat.kelolakost.ui.screen.tenant

import amat.kelolakost.R
import amat.kelolakost.di.Injection
import amat.kelolakost.ui.component.MyOutlinedTextField
import amat.kelolakost.ui.theme.FontBlack
import amat.kelolakost.ui.theme.FontWhite
import amat.kelolakost.ui.theme.GreenDark
import amat.kelolakost.ui.theme.KelolaKostTheme
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
import androidx.lifecycle.viewmodel.compose.viewModel

class AddTenantActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            KelolaKostTheme {
                AddTenantScreen(context)
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
fun AddTenantScreen(
    context: Context,
    modifier: Modifier = Modifier
) {
    val addTenantViewModel: AddTenantViewModel =
        viewModel(factory = AddTenantViewModelFactory(Injection.provideTenantRepository(context)))

    if (!addTenantViewModel.isInsertSuccess.collectAsState().value.isError) {
        Toast.makeText(context, stringResource(id = R.string.success_add_data), Toast.LENGTH_SHORT)
            .show()
        val activity = (context as? Activity)
        activity?.finish()
    } else {
        if (addTenantViewModel.isInsertSuccess.collectAsState().value.errorMessage.isNotEmpty()) {
            Toast.makeText(
                context,
                addTenantViewModel.isInsertSuccess.collectAsState().value.errorMessage,
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    Column {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.title_add_tenant),
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
                label = "Nama Penyewa",
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                value = addTenantViewModel.tenantUi.collectAsState().value.name,
                onValueChange = {
                    addTenantViewModel.setName(it)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                isError = addTenantViewModel.isNameValid.collectAsState().value.isError,
                errorMessage = addTenantViewModel.isNameValid.collectAsState().value.errorMessage
            )
            MyOutlinedTextField(
                label = "Nomor Handphone",
                value = addTenantViewModel.tenantUi.collectAsState().value.numberPhone,
                onValueChange = {
                    addTenantViewModel.setNumberPhone(it)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = addTenantViewModel.isNumberPhoneValid.collectAsState().value.isError,
                errorMessage = addTenantViewModel.isNumberPhoneValid.collectAsState().value.errorMessage
            )
            MyOutlinedTextField(
                label = "Alamat Email",
                value = addTenantViewModel.tenantUi.collectAsState().value.email,
                onValueChange = {
                    addTenantViewModel.setEmail(it)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = addTenantViewModel.isEmailValid.collectAsState().value.isError,
                errorMessage = addTenantViewModel.isEmailValid.collectAsState().value.errorMessage
            )
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Jenis Kelamin")
                Row(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .weight(1F)
                            .clickable {
                                addTenantViewModel.setGender(false)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (!addTenantViewModel.tenantUi.collectAsState().value.gender),
                            onClick = { addTenantViewModel.setGender(false) }
                        )
                        Text(text = "Pria")
                    }
                    Row(
                        modifier = Modifier
                            .weight(1F)
                            .clickable {
                                addTenantViewModel.setGender(true)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (addTenantViewModel.tenantUi.collectAsState().value.gender),
                            onClick = { addTenantViewModel.setGender(true) }
                        )
                        Text(text = "Wanita")
                    }
                }
            }
            MyOutlinedTextField(
                label = "Alamat",
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                value = addTenantViewModel.tenantUi.collectAsState().value.address,
                onValueChange = {
                    addTenantViewModel.setAddress(it)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                isError = addTenantViewModel.isAddressValid.collectAsState().value.isError,
                errorMessage = addTenantViewModel.isAddressValid.collectAsState().value.errorMessage
            )
            MyOutlinedTextField(
                label = "Keterangan Tambahan",
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                value = addTenantViewModel.tenantUi.collectAsState().value.note,
                onValueChange = {
                    addTenantViewModel.setNote(it)
                },
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth(),
                singleLine = false
            )
            Text(
                modifier = Modifier.padding(top = 8.dp),
                fontSize = 12.sp,
                text = "status penyewa saat ditambahkan adalah check-out", color = FontBlack
            )
            Button(
                onClick = {
                    addTenantViewModel.prosesInsert()
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
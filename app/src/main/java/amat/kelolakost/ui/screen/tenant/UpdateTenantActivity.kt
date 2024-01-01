package amat.kelolakost.ui.screen.tenant

import amat.kelolakost.R
import amat.kelolakost.di.Injection
import amat.kelolakost.ui.common.OnLifecycleEvent
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

class UpdateTenantActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        val id = intent.getStringExtra("id")

        setContent {
            val context = LocalContext.current
            KelolaKostTheme {
                if (id != null) {
                    UpdateTenantScreen(context, id)
                } else {
                    UpdateTenantScreen(context, "")
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
fun UpdateTenantScreen(
    context: Context,
    id: String,
    modifier: Modifier = Modifier
) {
    val updateTenantViewModel: UpdateTenantViewModel =
        viewModel(factory = UpdateTenantViewModelFactory(Injection.provideTenantRepository(context)))

    if (!updateTenantViewModel.isUpdateSuccess.collectAsState().value.isError) {
        Toast.makeText(
            context,
            stringResource(id = R.string.success_update_data),
            Toast.LENGTH_SHORT
        )
            .show()
        val activity = (context as? Activity)
        activity?.finish()
    } else {
        if (updateTenantViewModel.isUpdateSuccess.collectAsState().value.errorMessage.isNotEmpty()) {
            Toast.makeText(
                context,
                updateTenantViewModel.isUpdateSuccess.collectAsState().value.errorMessage,
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    OnLifecycleEvent { owner, event ->
        // do stuff on event
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                updateTenantViewModel.getDetail(id)
            }

            else -> {}
        }
    }

    Column {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.title_update_tenant),
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
                label = "Nama Penyewa",
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                value = updateTenantViewModel.tenantUi.collectAsState().value.name,
                onValueChange = {
                    updateTenantViewModel.setName(it)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                isError = updateTenantViewModel.isNameValid.collectAsState().value.isError,
                errorMessage = updateTenantViewModel.isNameValid.collectAsState().value.errorMessage
            )
            MyOutlinedTextField(
                label = "Nomor Handphone",
                value = updateTenantViewModel.tenantUi.collectAsState().value.numberPhone,
                onValueChange = {
                    updateTenantViewModel.setNumberPhone(it)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = updateTenantViewModel.isNumberPhoneValid.collectAsState().value.isError,
                errorMessage = updateTenantViewModel.isNumberPhoneValid.collectAsState().value.errorMessage
            )
            MyOutlinedTextField(
                label = "Alamat Email",
                value = updateTenantViewModel.tenantUi.collectAsState().value.email,
                onValueChange = {
                    updateTenantViewModel.setEmail(it)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = updateTenantViewModel.isEmailValid.collectAsState().value.isError,
                errorMessage = updateTenantViewModel.isEmailValid.collectAsState().value.errorMessage
            )
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Jenis Kelamin")
                Row(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .weight(1F)
                            .clickable {
                                updateTenantViewModel.setGender(false)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (!updateTenantViewModel.tenantUi.collectAsState().value.gender),
                            onClick = { updateTenantViewModel.setGender(false) }
                        )
                        Text(text = "Pria")
                    }
                    Row(
                        modifier = Modifier
                            .weight(1F)
                            .clickable {
                                updateTenantViewModel.setGender(true)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (updateTenantViewModel.tenantUi.collectAsState().value.gender),
                            onClick = { updateTenantViewModel.setGender(true) }
                        )
                        Text(text = "Wanita")
                    }
                }
            }
            MyOutlinedTextField(
                label = "Alamat",
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                value = updateTenantViewModel.tenantUi.collectAsState().value.address,
                onValueChange = {
                    updateTenantViewModel.setAddress(it)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                isError = updateTenantViewModel.isAddressValid.collectAsState().value.isError,
                errorMessage = updateTenantViewModel.isAddressValid.collectAsState().value.errorMessage
            )
            MyOutlinedTextField(
                label = "Keterangan Tambahan",
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                value = updateTenantViewModel.tenantUi.collectAsState().value.note,
                onValueChange = {
                    updateTenantViewModel.setNote(it)
                },
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth(),
                singleLine = false
            )
            Button(
                onClick = {
                    updateTenantViewModel.prosesUpdate()
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
}
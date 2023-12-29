package amat.kelolakost.ui.screen.user

import amat.kelolakost.R
import amat.kelolakost.di.Injection
import amat.kelolakost.ui.common.OnLifecycleEvent
import amat.kelolakost.ui.common.UiState
import amat.kelolakost.ui.component.ErrorLayout
import amat.kelolakost.ui.component.LoadingLayout
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
            KelolaKostTheme {
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

    if (updateUserViewModel.isUpdateSuccess.collectAsState().value) {
        Toast.makeText(
            context,
            stringResource(id = R.string.success_update_data),
            Toast.LENGTH_SHORT
        )
            .show()
        val activity = (context as? Activity)
        activity?.finish()
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
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        MyOutlinedTextField(
            label = "Nama Pemilik",
            value = updateUserViewModel.user.collectAsState().value.name,
            onValueChange = {
                updateUserViewModel.setName(it)
            },
            modifier = Modifier.fillMaxWidth(),
            isError = updateUserViewModel.isUserNameValid.collectAsState().value.isError,
            errorMessage = updateUserViewModel.isUserNameValid.collectAsState().value.errorMessage
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
            label = "Alamat Email",
            value = updateUserViewModel.user.collectAsState().value.email,
            onValueChange = {
                updateUserViewModel.setEmail(it)
            },
            modifier = Modifier
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = updateUserViewModel.isUserEmailValid.collectAsState().value.isError,
            errorMessage = updateUserViewModel.isUserEmailValid.collectAsState().value.errorMessage
        )
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Tipe Whatsapp")
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
                    Text(text = "Standard")
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
                    Text(text = "Business")
                }
            }
        }

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
package amat.kelolakost.ui.screen.back_up

import amat.kelolakost.ui.common.ValidationResult
import amat.kelolakost.ui.component.LoadingDialog
import amat.kelolakost.ui.component.MyOutlinedTextField
import amat.kelolakost.ui.theme.GreenDark
import amat.kelolakost.ui.theme.KelolaKostTheme
import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

class RegisterActivity : ComponentActivity() {

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KelolaKostTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    RegisterScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun RegisterScreen(viewModel: RegisterViewModel) {
    val context = LocalContext.current

    val focusManager = LocalFocusManager.current

    viewModel.isProsesValid.collectAsState(initial = ValidationResult.None).value.let { uiState ->
        when (uiState) {
            is ValidationResult.Loading -> {
                LoadingDialog(onDismissRequest = {}, text = uiState.loadingMessage)
            }

            is ValidationResult.Success -> {
                val data = uiState.data
                Toast.makeText(
                    context,
                    data,
                    Toast.LENGTH_LONG
                ).show()

                val activity = (context as? Activity)
                activity?.finish()

            }

            is ValidationResult.Error -> {
                Toast.makeText(
                    context,
                    uiState.errorMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {}
        }

    }

    Column {
        TopAppBar(
            title = { Text(text = "Buat Akun Baru", color = Color.White) },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            MyOutlinedTextField(
                label = "Nama Pengguna",
                value = viewModel.stateUi.collectAsState().value.name,
                onValueChange = {
                    viewModel.setName(it)
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier.fillMaxWidth(),
                isError = viewModel.isNameValid.collectAsState().value.isError,
                errorMessage = viewModel.isNameValid.collectAsState().value.errorMessage
            )
            MyOutlinedTextField(
                label = "Nomor Handphone",
                value = viewModel.stateUi.collectAsState().value.numberPhone,
                onValueChange = {
                    viewModel.setNumberPhone(it)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                isError = viewModel.isNumberPhoneValid.collectAsState().value.isError,
                errorMessage = viewModel.isNumberPhoneValid.collectAsState().value.errorMessage
            )
            MyOutlinedTextField(
                label = "Email",
                value = viewModel.stateUi.collectAsState().value.email,
                onValueChange = {
                    viewModel.setEmail(it)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                isError = viewModel.isEmailValid.collectAsState().value.isError,
                errorMessage = viewModel.isEmailValid.collectAsState().value.errorMessage
            )
            MyOutlinedTextField(
                label = "Ulangi Email",
                value = viewModel.stateUi.collectAsState().value.repeatEmail,
                onValueChange = {
                    viewModel.setRepeatEmail(it)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                isError = viewModel.isRepeatEmailValid.collectAsState().value.isError,
                errorMessage = viewModel.isRepeatEmailValid.collectAsState().value.errorMessage
            )
            MyOutlinedTextField(
                label = "Password",
                value = viewModel.stateUi.collectAsState().value.password,
                onValueChange = {
                    viewModel.setPassword(it)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                isError = viewModel.isPasswordValid.collectAsState().value.isError,
                errorMessage = viewModel.isPasswordValid.collectAsState().value.errorMessage
            )
            MyOutlinedTextField(
                label = "Ulangi Password",
                value = viewModel.stateUi.collectAsState().value.repeatPassword,
                onValueChange = {
                    viewModel.setRepeatPassword(it)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                isError = viewModel.isRepeatPasswordValid.collectAsState().value.isError,
                errorMessage = viewModel.isRepeatPasswordValid.collectAsState().value.errorMessage
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (viewModel.dataIsComplete()) {
                        viewModel.register()
                    }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = GreenDark)
            ) {
                Text(text = "Daftar Sekarang", color = Color.White)
            }
        }
    }
}
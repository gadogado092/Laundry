package amat.kelolakost.ui.screen.back_up

import amat.kelolakost.AccountBackupPreference
import amat.kelolakost.ui.common.ValidationResult
import amat.kelolakost.ui.component.LoadingDialog
import amat.kelolakost.ui.component.MyOutlinedTextField
import amat.kelolakost.ui.theme.Blue
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
import androidx.compose.material.TextButton
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

class ForgetPasswordActivity : ComponentActivity() {

    private val viewModel: ForgetPasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KelolaKostTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    ForgetPasswordScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun ForgetPasswordScreen(viewModel: ForgetPasswordViewModel) {

    val context = LocalContext.current

    val focusManager = LocalFocusManager.current

    val accountBackupPreference = AccountBackupPreference(context)

    viewModel.isProsesValid.collectAsState(initial = ValidationResult.None).value.let { uiState ->
        when (uiState) {
            is ValidationResult.Loading -> {
                LoadingDialog(onDismissRequest = {}, text = uiState.loadingMessage)
            }

            is ValidationResult.Success -> {
                Toast.makeText(
                    context,
                    uiState.data,
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

    viewModel.isProsesSendCodeValid.collectAsState(initial = ValidationResult.None).value.let { uiState ->
        when (uiState) {
            is ValidationResult.Loading -> {
                LoadingDialog(onDismissRequest = {}, text = uiState.loadingMessage)
            }

            is ValidationResult.Success -> {
                Toast.makeText(
                    context,
                    uiState.data,
                    Toast.LENGTH_LONG
                ).show()
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

    //START UI
    Column {
        TopAppBar(
            title = { Text(text = "Lupa Password", color = Color.White) },
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
            TextButton(onClick = { viewModel.sendCodeForgetPassword() }) {
                Text(text = "Kirim Kode Lupa Password ke Email", color = Blue)
            }
            MyOutlinedTextField(
                label = "Kode Lupa Password",
                value = viewModel.stateUi.collectAsState().value.code,
                onValueChange = {
                    viewModel.setCode(it)
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier.fillMaxWidth(),
                isError = viewModel.isCodeValid.collectAsState().value.isError,
                errorMessage = viewModel.isCodeValid.collectAsState().value.errorMessage
            )
            MyOutlinedTextField(
                label = "Password Baru",
                value = viewModel.stateUi.collectAsState().value.newPassword,
                onValueChange = {
                    viewModel.setNewPassword(it)
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
                isError = viewModel.isNewPasswordValid.collectAsState().value.isError,
                errorMessage = viewModel.isNewPasswordValid.collectAsState().value.errorMessage
            )
            MyOutlinedTextField(
                label = "Ulangi Password Baru",
                value = viewModel.stateUi.collectAsState().value.repeatNewPassword,
                onValueChange = {
                    viewModel.setRepeatNewPassword(it)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                isError = viewModel.isRepeatNewPasswordValid.collectAsState().value.isError,
                errorMessage = viewModel.isRepeatNewPasswordValid.collectAsState().value.errorMessage
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (viewModel.dataIsComplete()) {
                        viewModel.forgetPassword()
                    }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = GreenDark)
            ) {
                Text(text = "Proses", color = Color.White)
            }
        }
    }
}
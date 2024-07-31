package amat.kelolakost.ui.screen.back_up

import amat.kelolakost.AccountBackupPreference
import amat.kelolakost.R
import amat.kelolakost.data.entity.AccountBackupEntity
import amat.kelolakost.ui.common.ValidationResult
import amat.kelolakost.ui.component.LoadingDialog
import amat.kelolakost.ui.component.MyOutlinedTextField
import amat.kelolakost.ui.theme.Blue
import amat.kelolakost.ui.theme.GreenDark
import amat.kelolakost.ui.theme.GreyLight
import amat.kelolakost.ui.theme.KelolaKostTheme
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

class LoginActivity : ComponentActivity() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContent {
            KelolaKostTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    LoginScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun LoginScreen(viewModel: LoginViewModel) {

    val context = LocalContext.current
    val accountBackupPreference = AccountBackupPreference(context)

    viewModel.isProsesValid.collectAsState(initial = ValidationResult.None).value.let { uiState ->
        when (uiState) {
            is ValidationResult.Loading -> {
                LoadingDialog(onDismissRequest = {}, text = uiState.loadingMessage)
            }

            is ValidationResult.Success -> {
                val data = uiState.data
                accountBackupPreference.setAccount(
                    AccountBackupEntity(
                        isLogin = true,
                        name = data.name,
                        noWa = data.numberWa,
                        token = data.token
                    )
                )
                Toast.makeText(
                    context,
                    "Login ${data.name} Berhasil",
                    Toast.LENGTH_LONG
                ).show()

                val activity = (context as? Activity)
                val intent = Intent(context, BackUpActivity::class.java)
                context.startActivity(intent)
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

    //START UI
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            modifier = Modifier
                .size(128.dp)
                .clip(CircleShape),
            painter = painterResource(id = R.drawable.ic_splash),
            contentDescription = "",
        )
        Spacer(modifier = Modifier.height(16.dp))
        MyOutlinedTextField(
            label = "Email",
            value = viewModel.stateUi.collectAsState().value.email,
            onValueChange = {
                viewModel.setEmail(it)
            },
            modifier = Modifier
                .fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = viewModel.isEmailValid.collectAsState().value.isError,
            errorMessage = viewModel.isEmailValid.collectAsState().value.errorMessage
        )
        Spacer(modifier = Modifier.height(4.dp))
        MyOutlinedTextField(
            label = "Password",
            value = viewModel.stateUi.collectAsState().value.password,
            onValueChange = {
                viewModel.setPassword(it)
            },
            modifier = Modifier
                .fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = viewModel.isPasswordValid.collectAsState().value.isError,
            errorMessage = viewModel.isPasswordValid.collectAsState().value.errorMessage
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                if (viewModel.dataIsComplete()) {
                    viewModel.login()
                }
            }, modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = GreenDark)
        ) {
            Text(text = "Masuk", color = Color.White)
        }
        TextButton(onClick = {
            val intent = Intent(context, ForgetPasswordActivity::class.java)
            context.startActivity(intent)
        }, modifier = Modifier.padding(bottom = 16.dp)) {
            Text(text = "Lupa Password?", color = Blue)
        }
        Divider(
            color = GreyLight,
            thickness = 2.dp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(
            onClick = {
                val intent = Intent(context, RegisterActivity::class.java)
                context.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Blue)
        ) {
            Text(text = "Buat Akun Baru", color = Color.White)
        }
    }
}
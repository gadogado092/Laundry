package amat.kelolakost.ui.screen.back_up

import amat.kelolakost.AccountBackupPreference
import amat.kelolakost.R
import amat.kelolakost.dateUniversalToDisplay
import amat.kelolakost.di.Injection
import amat.kelolakost.ui.common.OnLifecycleEvent
import amat.kelolakost.ui.common.ValidationResult
import amat.kelolakost.ui.component.LoadingDialog
import amat.kelolakost.ui.component.OtherMenuItem
import amat.kelolakost.ui.screen.splash.SplashActivity
import amat.kelolakost.ui.theme.FontBlack
import amat.kelolakost.ui.theme.GreenDark
import amat.kelolakost.ui.theme.GreyLight
import amat.kelolakost.ui.theme.KelolaKostTheme
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.json.JSONObject

class BackUpActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KelolaKostTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    BackUpScreen()
                }
            }
        }
    }
}

@Composable
fun BackUpScreen() {

    val context = LocalContext.current

    val viewModel: BackUpViewModel =
        androidx.lifecycle.viewmodel.compose.viewModel(
            factory = BackUpViewModelFactory(
                Injection.provideBackUpRepository(context),
                AccountBackupPreference(context)
            )
        )

    OnLifecycleEvent { _, event ->
        // do stuff on event
        when (event) {
            Lifecycle.Event.ON_START -> {
                viewModel.getLastBackUp()
            }

            else -> { /* other stuff */
            }
        }
    }

    viewModel.isProsesValid.collectAsState(initial = ValidationResult.None).value.let { uiState ->
        when (uiState) {
            is ValidationResult.Loading -> {
                LoadingDialog(onDismissRequest = {}, text = uiState.loadingMessage)
            }

            is ValidationResult.Success -> {
                Toast.makeText(
                    context,
                    uiState.data,
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.getLastBackUp()
            }

            is ValidationResult.Error -> {
                if (uiState.errorMessage == "Silahkan Login Kembali") {
                    val activity = (context as? Activity)
                    val intent = Intent(context, LoginActivity::class.java)
                    context.startActivity(intent)
                    activity?.finish()
                }
                Toast.makeText(
                    context,
                    uiState.errorMessage,
                    Toast.LENGTH_LONG
                ).show()
            }

            else -> {}
        }

    }

    viewModel.isProsesRestoreValid.collectAsState(initial = ValidationResult.None).value.let { uiState ->
        when (uiState) {
            is ValidationResult.Loading -> {
                LoadingDialog(onDismissRequest = {}, text = uiState.loadingMessage)
            }

            is ValidationResult.Success -> {
                val data = uiState.data
                val dataUnit = data.getJSONArray(viewModel.tableUnit)
                val dataKost = data.getJSONArray(viewModel.tableKost)
                val dataCashFlow = data.getJSONArray(viewModel.tableCashFlow)
                val message = "Sekilas Jumlah Data..." +
                        "\n\t*Data Kost ${dataKost.length() - 1}" +
                        "\n\t*Data Unit/Kamar ${dataUnit.length() - 1}" +
                        "\n\t*Data Alur Kas ${dataCashFlow.length()}" +
                        "\nSemua Data Sebelumnya Akan Terhapus... Tetap Lanjutkan Restore?"

                showBottomRestore(context, viewModel, message, data)
            }

            is ValidationResult.Error -> {
                if (uiState.errorMessage == "Silahkan Login Kembali") {
                    val activity = (context as? Activity)
                    val intent = Intent(context, LoginActivity::class.java)
                    context.startActivity(intent)
                    activity?.finish()
                }
                Toast.makeText(
                    context,
                    uiState.errorMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {}
        }

    }

    viewModel.isProsesCheckTokenValid.collectAsState(initial = ValidationResult.None).value.let { uiState ->
        when (uiState) {
            is ValidationResult.Error -> {
                if (uiState.errorMessage == "Silahkan Login Kembali" || uiState.errorMessage == "Dalam Proses Perbaikan... Kode=7073N") {
                    val activity = (context as? Activity)
                    val intent = Intent(context, LoginActivity::class.java)
                    context.startActivity(intent)
                    activity?.finish()
                }
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
            title = {
                Column {
                    Text(text = viewModel.stateUi.collectAsState().value.name, color = Color.White)
                    Text(
                        text = viewModel.stateUi.collectAsState().value.noWa,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            },
            backgroundColor = GreenDark,
        )

        OtherMenuItem(
            Icons.Default.CloudUpload,
            "Backup Online",
            "Proses ini akan menyimpan data offline ke penyimpanan online dan mengganti data penyimpanan online sebelumnya",
            modifier = Modifier
                .clickable {
                    showBottomBackup(context, viewModel)
                },
        )

        if (viewModel.stateUi.collectAsState().value.lastBackUp.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 4.dp),
                    text = "Backup Terakhir ${dateUniversalToDisplay(viewModel.stateUi.collectAsState().value.lastBackUp)}",
                    style = TextStyle(fontSize = 12.sp),
                    color = FontBlack
                )
            }
        }
        Divider(
            color = GreyLight,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 2.dp)
        )
        OtherMenuItem(
            Icons.Default.CloudDownload,
            "Restore Online",
            "Proses ini akan mengambil penyimpanan online ke data offline  dan menghapus semua data saat ini yang digunakan pada Hp",
            modifier = Modifier
                .clickable {
                    viewModel.restore()
                },
        )
        Divider(
            color = GreyLight,
            thickness = 1.dp,
            modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
        )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
            onClick = {
                val activity = (context as? Activity)
                val intent = Intent(context, SplashActivity::class.java)
                context.startActivity(intent)
                activity?.finish()
            }) {
            Text(text = "Kembali", color = FontBlack)
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
            onClick = {
                val intent = Intent(context, ChangePasswordActivity::class.java)
                context.startActivity(intent)
            }) {
            Text(text = "Ganti Password", color = FontBlack)
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
            onClick = {
                viewModel.logOut()
                val activity = (context as? Activity)
                val intent = Intent(context, LoginActivity::class.java)
                context.startActivity(intent)
                activity?.finish()
            }) {
            Text(text = "LogOut", color = FontBlack)
        }
    }
}

private fun showBottomRestore(
    context: Context,
    viewModel: BackUpViewModel,
    message: String,
    data: JSONObject
) {
    val bottomSheetDialog = BottomSheetDialog(context)
    bottomSheetDialog.setContentView(R.layout.bottom_sheet_confirm)
    val textMessage = bottomSheetDialog.findViewById<TextView>(R.id.text_message)
    val buttonOk = bottomSheetDialog.findViewById<Button>(R.id.ok_button)

    textMessage?.text = message
    buttonOk?.text = "Ok"
    buttonOk?.setOnClickListener {
        bottomSheetDialog.dismiss()
        viewModel.insertDataRestore(data)
    }
    bottomSheetDialog.show()
}

private fun showBottomBackup(
    context: Context,
    viewModel: BackUpViewModel
) {
    val bottomSheetDialog = BottomSheetDialog(context)
    bottomSheetDialog.setContentView(R.layout.bottom_sheet_confirm)
    val textMessage = bottomSheetDialog.findViewById<TextView>(R.id.text_message)
    val buttonOk = bottomSheetDialog.findViewById<Button>(R.id.ok_button)

    textMessage?.text = "Yakin Lanjutkan Proses Backup Data?"
    buttonOk?.text = "Ok"
    buttonOk?.setOnClickListener {
        bottomSheetDialog.dismiss()
        viewModel.backUp(context)
    }
    bottomSheetDialog.show()
}
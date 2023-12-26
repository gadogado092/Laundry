package amat.kelolakost.ui.screen.kost

import amat.kelolakost.R
import amat.kelolakost.di.Injection
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.viewmodel.compose.viewModel

class AddKostActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            KelolaKostTheme {
                AddKostScreen(context)
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
fun AddKostScreen(
    context: Context,
    modifier: Modifier = Modifier
) {
    val addKostViewModel: AddKostViewModel =
        viewModel(factory = AddKostViewModelFactory(Injection.provideKostRepository(context)))

    if (addKostViewModel.isInsertSuccess.collectAsState().value) {
        Toast.makeText(context, stringResource(id = R.string.success_add_data), Toast.LENGTH_SHORT)
            .show()
        val activity = (context as? Activity)
        activity?.finish()
    }

    Column {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.title_add_kost),
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
                label = "Nama Kost/Kontrakan/Penginapan",
                value = addKostViewModel.kost.collectAsState().value.name,
                onValueChange = {
                    addKostViewModel.setKostName(it)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                isError = addKostViewModel.isKostNameValid.collectAsState().value.isError,
                errorMessage = addKostViewModel.isKostNameValid.collectAsState().value.errorMessage
            )

            MyOutlinedTextField(
                label = "Alamat Kost/Kontrakan/Penginapan",
                value = addKostViewModel.kost.collectAsState().value.address,
                onValueChange = {
                    addKostViewModel.setKostAddress(it)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                isError = addKostViewModel.isKostAddressValid.collectAsState().value.isError,
                errorMessage = addKostViewModel.isKostAddressValid.collectAsState().value.errorMessage
            )
            MyOutlinedTextField(
                label = "Keterangan Tambahan",
                value = addKostViewModel.kost.collectAsState().value.note,
                onValueChange = {
                    addKostViewModel.setNote(it)
                },
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth(),
                singleLine = false
            )
            Button(
                onClick = {
                    addKostViewModel.prosesInsert()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = GreenDark)
            ) {
                Text(text = stringResource(id = R.string.save), color = FontWhite)
            }
        }
    }
}
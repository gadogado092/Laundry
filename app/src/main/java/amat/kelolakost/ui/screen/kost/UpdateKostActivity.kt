package amat.kelolakost.ui.screen.kost

import amat.kelolakost.R
import amat.kelolakost.di.Injection
import amat.kelolakost.ui.common.OnLifecycleEvent
import amat.kelolakost.ui.common.UiState
import amat.kelolakost.ui.component.ErrorLayout
import amat.kelolakost.ui.component.LoadingLayout
import amat.kelolakost.ui.component.MyOutlinedTextField
import amat.kelolakost.ui.theme.FontWhite
import amat.kelolakost.ui.theme.GreenDark
import amat.kelolakost.ui.theme.GreyLight
import amat.kelolakost.ui.theme.GreyLight3
import amat.kelolakost.ui.theme.KelolaKostTheme
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

class UpdateKostActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        val id = intent.getStringExtra("id")

        setContent {
            val context = LocalContext.current
            KelolaKostTheme {
                if (id != null) {
                    UpdateKostScreen(context, id)
                } else {
                    UpdateKostScreen(context, "")
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
fun UpdateKostScreen(
    context: Context,
    id: String
) {
    val updateKostViewModel: UpdateKostViewModel =
        viewModel(factory = UpdateKostViewModelFactory(Injection.provideKostRepository(context)))

    if (updateKostViewModel.isUpdateSuccess.collectAsState().value) {
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
                updateKostViewModel.getDetail(id)
            }

            else -> {}
        }
    }

    Column {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.title_update_kost),
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

        updateKostViewModel.stateInitKost.collectAsState(initial = UiState.Loading).value.let { uiState ->
            when (uiState) {
                is UiState.Error -> {
                    ErrorLayout(
                        modifier = Modifier.fillMaxHeight(),
                        errorMessage = uiState.errorMessage
                    ) {
                        updateKostViewModel.getDetail(id)
                    }
                }

                UiState.Loading -> {
                    LoadingLayout(modifier = Modifier.fillMaxHeight())
                }

                is UiState.Success -> {
                    FormUpdate(updateKostViewModel)
                }
            }
        }

    }
}

@Composable
fun FormUpdate(updateKostViewModel: UpdateKostViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        MyOutlinedTextField(
            label = "Nama Kost/Kontrakan/Penginapan",
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            value = updateKostViewModel.kost.collectAsState().value.name,
            onValueChange = {
                updateKostViewModel.setKostName(it)
            },
            modifier = Modifier
                .fillMaxWidth(),
            isError = updateKostViewModel.isKostNameValid.collectAsState().value.isError,
            errorMessage = updateKostViewModel.isKostNameValid.collectAsState().value.errorMessage
        )
        MyOutlinedTextField(
            label = "Alamat Kost/Kontrakan/Penginapan",
            value = updateKostViewModel.kost.collectAsState().value.address,
            onValueChange = {
                updateKostViewModel.setKostAddress(it)
            },
            modifier = Modifier
                .fillMaxWidth(),
            isError = updateKostViewModel.isKostAddressValid.collectAsState().value.isError,
            errorMessage = updateKostViewModel.isKostAddressValid.collectAsState().value.errorMessage
        )
        MyOutlinedTextField(
            label = "Keterangan Tambahan",
            value = updateKostViewModel.kost.collectAsState().value.note,
            onValueChange = {
                updateKostViewModel.setNote(it)
            },
            modifier = Modifier
                .height(120.dp)
                .fillMaxWidth(),
            singleLine = false
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
        {
            Button(
                modifier = Modifier
                    .weight(1F),
                onClick = {
                    //TODO
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = GreyLight
                )
            ) {
                Text(text = stringResource(id = R.string.delete), color = GreyLight3)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                modifier = Modifier
                    .weight(1F),
                onClick = {
                    updateKostViewModel.prosesUpdate()
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = GreenDark)
            ) {
                Text(text = stringResource(id = R.string.update), color = FontWhite)
            }
        }

    }
}
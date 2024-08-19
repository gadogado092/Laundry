package amat.kelolakost.ui.screen.kost

import amat.kelolakost.R
import amat.kelolakost.data.Kost
import amat.kelolakost.di.Injection
import amat.kelolakost.ui.common.UiState
import amat.kelolakost.ui.component.CenterLayout
import amat.kelolakost.ui.component.ErrorLayout
import amat.kelolakost.ui.component.KostItem
import amat.kelolakost.ui.component.LoadingLayout
import amat.kelolakost.ui.theme.FontBlack
import amat.kelolakost.ui.theme.FontWhite
import amat.kelolakost.ui.theme.GreenDark
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun KostScreen(
    context: Context,
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
) {
    val kostViewModel: KostViewModel =
        viewModel(factory = KostViewModelFactory(Injection.provideKostRepository(context)))
    Column {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.title_kost),
                    color = FontWhite,
                    fontSize = 22.sp
                )
            },
            backgroundColor = GreenDark,
            navigationIcon = {
                IconButton(
                    onClick = navigateBack
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "",
                        tint = Color.White
                    )
                }
            }
        )
        Box(
            modifier = modifier.fillMaxSize()
        ) {

            kostViewModel.stateListKost.collectAsState(initial = UiState.Loading).value.let { uiState ->
                when (uiState) {
                    is UiState.Error -> {
                        ErrorLayout(errorMessage = uiState.errorMessage) {
                            kostViewModel.getAllKostInit()
                        }
                    }

                    UiState.Loading -> {
                        LoadingLayout()
                    }

                    is UiState.Success -> {
                        ListKostView(listKost = uiState.data, onItemClick = {
                            val intent = Intent(context, UpdateKostActivity::class.java)
                            intent.putExtra("id", it)
                            context.startActivity(intent)
                        })
                    }
                }
            }

            FloatingActionButton(
                onClick = {
                    val intent = Intent(context, AddKostActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp),
                backgroundColor = GreenDark
            ) {
                Icon(
                    Icons.Filled.Add,
                    "",
                    modifier = Modifier.size(30.dp),
                    tint = Color.White,
                )
            }
        }
    }
}

@Composable
fun ListKostView(
    listKost: List<Kost>,
    onItemClick: (String) -> Unit,
) {
    if (listKost.isEmpty()) {
        CenterLayout(
            content = {
                Text(
                    text = stringResource(
                        id = R.string.note_empty_data,
                        "Lapangan"
                    ), color= FontBlack
                )
            }
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 64.dp)
        ) {
            items(listKost) { data ->
                KostItem(
                    name = data.name,
                    address = data.address,
                    note = data.note,
                    modifier = Modifier.clickable {
                        onItemClick(data.id)
                    })
            }
        }
    }
}
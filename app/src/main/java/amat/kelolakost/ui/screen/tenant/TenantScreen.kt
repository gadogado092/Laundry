package amat.kelolakost.ui.screen.tenant

import amat.kelolakost.R
import amat.kelolakost.data.TenantHome
import amat.kelolakost.di.Injection
import amat.kelolakost.ui.common.OnLifecycleEvent
import amat.kelolakost.ui.common.UiState
import amat.kelolakost.ui.component.CenterLayout
import amat.kelolakost.ui.component.ErrorLayout
import amat.kelolakost.ui.component.FilterItem
import amat.kelolakost.ui.component.LoadingLayout
import amat.kelolakost.ui.component.TenantItem
import amat.kelolakost.ui.theme.GreenDark
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun TenantScreen(
    context: Context,
    modifier: Modifier = Modifier
) {
    val tenantViewModel: TenantViewModel =
        viewModel(factory = TenantViewModelFactory(Injection.provideTenantRepository(context)))

    OnLifecycleEvent { owner, event ->
        // do stuff on event
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                tenantViewModel.getAllTenant()
            }

            else -> {}
        }

    }

    Column {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            ContentStatus(viewModel = tenantViewModel)
        }
        Box(
            modifier = modifier.fillMaxSize()
        ) {

            tenantViewModel.stateListTenant.collectAsState(initial = UiState.Loading).value.let { uiState ->
                when (uiState) {
                    is UiState.Error -> {
                        ErrorLayout(errorMessage = uiState.errorMessage) {
                            tenantViewModel.getAllTenant()
                        }
                    }

                    UiState.Loading -> {
                        LoadingLayout()
                    }

                    is UiState.Success -> {
                        ListTenantView(listData = uiState.data, onItemClick = {
                            val intent = Intent(context, UpdateTenantActivity::class.java)
                            intent.putExtra("id", it)
                            context.startActivity(intent)
                        })
                    }
                }
            }

            FloatingActionButton(
                onClick = {
                    val intent = Intent(context, AddTenantActivity::class.java)
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
fun ContentStatus(viewModel: TenantViewModel) {
    val statusSelected = viewModel.statusSelected.collectAsState()
    viewModel.listStatus.collectAsState().value.let { value ->
        LazyRow(contentPadding = PaddingValues(vertical = 4.dp)) {
            items(value, key = { it.value }) { item ->
                FilterItem(
                    title = item.title,
                    isSelected = item.title == statusSelected.value.title,
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                        .clickable {
                            viewModel.updateStatusSelected(item.title, item.value)
                        }
                )
            }
        }
    }
}

@Composable
fun ListTenantView(
    listData: List<TenantHome>,
    onItemClick: (String) -> Unit,
) {
    if (listData.isEmpty()) {
        CenterLayout(
            content = {
                Text(
                    text = stringResource(
                        id = R.string.note_empty_data,
                        "Penyewa"
                    )
                )
            }
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 64.dp)
        ) {
            items(listData) { data ->
                TenantItem(
                    modifier = Modifier.clickable {
                        onItemClick(data.id)
                    },
                    name = data.name,
                    unitName = data.unitName,
                    kostName = data.kostName,
                    numberPhone = data.numberPhone,
                    unitId = data.unitId,
                    limitCheckOut = data.limitCheckOut,
                    onClickSms = {

                    },
                    onClickPhone = {

                    },
                    onClickWa = {

                    },
                )
            }
        }
    }
}
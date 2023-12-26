package amat.kelolakost.ui.screen.unit

import amat.kelolakost.FilterAdapter
import amat.kelolakost.R
import amat.kelolakost.data.Kost
import amat.kelolakost.data.entity.FilterEntity
import amat.kelolakost.di.Injection
import amat.kelolakost.ui.common.UiState
import amat.kelolakost.ui.component.FilterButton
import amat.kelolakost.ui.component.FilterItem
import android.content.Context
import android.widget.TextView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog

@Composable
fun UnitScreen(
    context: Context,
    modifier: Modifier = Modifier
) {
    val viewModel: UnitViewModel =
        viewModel(factory = UnitViewModelFactory(Injection.provideKostRepository(context)))

    Column {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            viewModel.statusSelected.collectAsState(
                initial = FilterEntity(
                    "Loading...",
                    ""
                )
            ).value.let { value ->
                FilterButton(title = value.title, modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        showBottomSheetSelectStatus(context, viewModel)
                    })
            }

            ContentKost(viewModel)
        }
        Text(text = "UnitScreen")
    }
}

fun showBottomSheetSelectStatus(context: Context, viewModel: UnitViewModel) {
    val bottomSheetDialog = BottomSheetDialog(context)
    bottomSheetDialog.setContentView(R.layout.bottom_sheet_filter)
    val title = bottomSheetDialog.findViewById<TextView>(R.id.text_title)
    val recyclerView = bottomSheetDialog.findViewById<RecyclerView>(R.id.recyclerView)

    title?.text = context.resources.getString(R.string.select_status)

    val filterAdapter = FilterAdapter(viewModel.statusSelected.value) {
        viewModel.updateStatusSelected(it.title, it.value)
        bottomSheetDialog.dismiss()
    }

    with(recyclerView) {
        this?.setHasFixedSize(true)
        this?.layoutManager =
            LinearLayoutManager(context)
        this?.adapter = filterAdapter
    }

    filterAdapter.setData(viewModel.listStatus.value)
    bottomSheetDialog.show()
}

@Composable
fun ContentKost(viewModel: UnitViewModel) {
    val kostSelected =
        viewModel.kostSelected.collectAsState(initial = Kost("", "", "", "", "", false))
    viewModel.stateListKost.collectAsState(initial = UiState.Loading).value.let { uiState ->
        when (uiState) {
            is UiState.Error -> {
                FilterItem(
                    title = uiState.errorMessage,
                    isSelected = false,
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                )
            }

            UiState.Loading -> {
                FilterItem(
                    title = "Loading",
                    isSelected = false,
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                )
            }

            is UiState.Success -> {
                val listStatus: List<Kost> = uiState.data
                LazyRow(contentPadding = PaddingValues(vertical = 4.dp)) {
                    items(listStatus, key = { it.name }) { item ->
                        FilterItem(
                            title = item.name,
                            isSelected = item.id == kostSelected.value.id,
                            modifier = Modifier
                                .padding(horizontal = 4.dp, vertical = 4.dp)
                                .clickable {
                                    viewModel.updateKostSelected(item)
                                }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ContentStatus(viewModel: UnitViewModel) {
    val statusSelected = viewModel.statusSelected.collectAsState(initial = FilterEntity("", ""))
    viewModel.listStatus.collectAsState(initial = mutableListOf()).value.let { data ->
        val listStatus: List<FilterEntity> = data
        LazyRow(contentPadding = PaddingValues(vertical = 4.dp)) {
            items(listStatus, key = { it.value }) { item ->
                FilterItem(
                    title = item.title,
                    isSelected = item.value == statusSelected.value.value,
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
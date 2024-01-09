package amat.kelolakost.ui.screen.unit

import amat.kelolakost.FilterAdapter
import amat.kelolakost.R
import amat.kelolakost.data.Kost
import amat.kelolakost.data.UnitHome
import amat.kelolakost.data.entity.FilterEntity
import amat.kelolakost.dateToDisplayDayMonth
import amat.kelolakost.di.Injection
import amat.kelolakost.generateLimitColor
import amat.kelolakost.generateLimitText
import amat.kelolakost.ui.common.OnLifecycleEvent
import amat.kelolakost.ui.common.UiState
import amat.kelolakost.ui.component.CenterLayout
import amat.kelolakost.ui.component.ErrorLayout
import amat.kelolakost.ui.component.FilterButton
import amat.kelolakost.ui.component.FilterItem
import amat.kelolakost.ui.component.LoadingLayout
import amat.kelolakost.ui.component.UnitItem
import amat.kelolakost.ui.screen.check_in.CheckInActivity
import amat.kelolakost.ui.screen.check_out.CheckOutActivity
import amat.kelolakost.ui.theme.FontBlack
import amat.kelolakost.ui.theme.GreenDark
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.TextView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog

@Composable
fun UnitScreen(
    context: Context,
    modifier: Modifier = Modifier
) {
    val viewModel: UnitViewModel =
        viewModel(
            factory = UnitViewModelFactory(
                Injection.provideKostRepository(context),
                Injection.provideUnitRepository(context)
            )
        )

    OnLifecycleEvent { owner, event ->
        // do stuff on event
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                Log.d("saya", "on resume")
                viewModel.getUnit()
            }

            else -> {}
        }

    }

    Column {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            viewModel.statusSelected.collectAsState(
                initial = FilterEntity(
                    "Loading...",
                    ""
                )
            ).value.let { value ->
                FilterButton(title = value.title, modifier = Modifier
                    .width(135.dp)
                    .padding(8.dp)
                    .clickable {
                        showBottomSheetSelectStatus(context, viewModel)
                    })
            }

            ContentKost(viewModel)
        }
        Box(
            modifier = modifier.fillMaxSize()
        ) {

            viewModel.stateListUnit.collectAsState(initial = UiState.Loading).value.let { uiState ->
                when (uiState) {
                    is UiState.Error -> {
                        ErrorLayout(errorMessage = uiState.errorMessage) {
                            viewModel.getUnit()
                        }
                    }

                    UiState.Loading -> {
                        LoadingLayout()
                    }

                    is UiState.Success -> {
                        ListUnitView(listData = uiState.data, onItemClick = {
                            val intent = Intent(context, UpdateUnitActivity::class.java)
                            intent.putExtra("id", it)
                            context.startActivity(intent)
                        }, onClickCheckIn = { id, name, price, duration, priceGuarantee ->
                            val intent = Intent(context, CheckInActivity::class.java)
                            intent.putExtra("unitId", id)
                            intent.putExtra("unitName", name)
                            intent.putExtra("kostId", viewModel.kostSelected.value.id)
                            intent.putExtra("kostName", viewModel.kostSelected.value.name)
                            intent.putExtra("price", price)
                            intent.putExtra("duration", duration)
                            intent.putExtra("priceGuarantee", priceGuarantee)
                            context.startActivity(intent)
                        },
                            onClickCheckOut = { id ->
                                val intent = Intent(context, CheckOutActivity::class.java)
                                intent.putExtra("unitId", id)
                                context.startActivity(intent)
                            })
                    }
                }
            }

            FloatingActionButton(
                onClick = {
                    val intent = Intent(context, AddUnitActivity::class.java)
                    intent.putExtra("kostId", viewModel.kostSelected.value.id)
                    intent.putExtra("kostName", viewModel.kostSelected.value.name)
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
fun ListUnitView(
    listData: List<UnitHome>,
    onItemClick: (String) -> Unit,
    onClickCheckIn: (String, String, String, String, String) -> Unit,
    onClickCheckOut: (String) -> Unit
) {
    if (listData.isEmpty()) {
        CenterLayout(
            content = {
                Text(
                    text = stringResource(
                        id = R.string.note_empty_data,
                        "Unit/Kamar"
                    )
                )
            }
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 64.dp)
        ) {
            items(listData) { data ->
                UnitItem(
                    modifier = Modifier.clickable {
                        onItemClick(data.id)
                    },
                    id = data.id,
                    name = data.name,
                    tenantName = data.tenantName,
                    noteMaintenance = data.noteMaintenance,
                    limitCheckOut = if (data.limitCheckOut.isNotEmpty()) "${generateLimitText(data.limitCheckOut)}-${
                        dateToDisplayDayMonth(
                            data.limitCheckOut
                        )
                    }" else "",
                    colorLimitCheckOut = if (data.limitCheckOut.isNotEmpty()) generateLimitColor(
                        data.limitCheckOut
                    ) else FontBlack,
                    unitStatusId = data.unitStatusId,
                    unitTypeName = data.unitTypeName,
                    priceDay = data.priceDay,
                    priceWeek = data.priceWeek,
                    priceMonth = data.priceMonth,
                    priceThreeMonth = data.priceThreeMonth,
                    priceSixMonth = data.priceSixMonth,
                    priceYear = data.priceYear,
                    priceGuarantee = data.priceGuarantee,
                    onClickCheckIn = onClickCheckIn,
                    onClickExtend = {

                    },
                    onClickCheckOut = onClickCheckOut,
                    onClickMoveUnit = {

                    },
                    onClickFinishRenovation = {

                    }
                )
            }
        }
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
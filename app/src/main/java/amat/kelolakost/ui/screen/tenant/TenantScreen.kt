package amat.kelolakost.ui.screen.tenant

import amat.kelolakost.R
import amat.kelolakost.data.TenantHome
import amat.kelolakost.dateToDisplayDayMonth
import amat.kelolakost.di.Injection
import amat.kelolakost.generateLimitColor
import amat.kelolakost.generateLimitText
import amat.kelolakost.sendWhatsApp
import amat.kelolakost.ui.common.OnLifecycleEvent
import amat.kelolakost.ui.common.UiState
import amat.kelolakost.ui.component.CenterLayout
import amat.kelolakost.ui.component.ErrorLayout
import amat.kelolakost.ui.component.FilterItem
import amat.kelolakost.ui.component.LoadingLayout
import amat.kelolakost.ui.component.TenantItem
import amat.kelolakost.ui.screen.check_in.CheckInActivity
import amat.kelolakost.ui.theme.FontBlack
import amat.kelolakost.ui.theme.GreenDark
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlin.math.abs

@Composable
fun TenantScreen(
    context: Context,
    modifier: Modifier = Modifier
) {
    val tenantViewModel: TenantViewModel =
        viewModel(
            factory = TenantViewModelFactory(
                Injection.provideTenantRepository(context),
                Injection.provideUserRepository(context)
            )
        )

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
                        ListTenantView(context = context,
                            listData = uiState.data,
                            tenantViewModel = tenantViewModel,
                            onItemClick = {
                                val intent = Intent(context, UpdateTenantActivity::class.java)
                                intent.putExtra("id", it)
                                context.startActivity(intent)
                            },
                            onClickCheckIn = { id, name ->
                                val intent = Intent(context, CheckInActivity::class.java)
                                intent.putExtra("tenantId", id)
                                intent.putExtra("tenantName", name)
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
    context: Context,
    listData: List<TenantHome>,
    tenantViewModel: TenantViewModel,
    onItemClick: (String) -> Unit,
    onClickCheckIn: (String, String) -> Unit
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
                    id = data.id,
                    name = data.name,
                    unitName = data.unitName,
                    kostName = data.kostName,
                    numberPhone = data.numberPhone,
                    unitId = data.unitId,
                    limitCheckOut = if (data.limitCheckOut.isNotEmpty()) "${generateLimitText(data.limitCheckOut)}-${
                        dateToDisplayDayMonth(
                            data.limitCheckOut
                        )
                    }" else "",
                    colorLimitCheckOut = if (data.limitCheckOut.isNotEmpty()) generateLimitColor(
                        data.limitCheckOut
                    ) else FontBlack,
                    onClickSms = {
                        sendSms(context, tenantViewModel, data)
                    },
                    onClickPhone = {
                        val intent = Intent(Intent.ACTION_DIAL)
                        intent.data = Uri.parse("tel:${data.numberPhone}")
                        context.startActivity(intent)
                    },
                    onClickWa = {
                        sendWa(context, tenantViewModel, data)
                    },
                    onClickCheckIn = onClickCheckIn
                )
            }
        }
    }
}

@SuppressLint("SimpleDateFormat")
fun sendSms(context: Context, viewModel: TenantViewModel, tenant: TenantHome) {
    try {
        if (tenant.unitId != "0") {
            val user = viewModel.user.value
            val myFormat = SimpleDateFormat("yyyy-MM-dd")
            val dateNow = myFormat.format(Calendar.getInstance().time)
            val date1 = myFormat.parse(dateNow)
            val date2 = myFormat.parse(tenant.limitCheckOut)
            val diff = date2!!.time - date1!!.time
            val day = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toString()
            val body: String
            var bankInfo = ""
            var note = ""
            if (user.bankName != "" && user.accountNumber != "" && user.accountOwnerName != "") {
                bankInfo =
                    "\nTransfer ke Rek ${user.bankName} ${user.accountNumber} (${user.accountOwnerName})"
            }
            if (user.note != "") {
                note = "\n${user.note}"
            }
            if (day.toInt() == 0) {
                body =
                    "Bpk/Ibu bersama ini kami sampaikan masa berlaku sewa Anda tinggal Hari ini - unit ${tenant.unitName}. Silahkan lakukan pembayaran.$bankInfo$note\n\nTerima Kasih\n\nDari Pengelola\n${user.name}"
            } else if (day.toInt() < 0) {
                body =
                    "Bpk/Ibu bersama ini kami sampaikan masa berlaku sewa Anda lewat ${
                        abs(
                            day.toInt()
                        )
                    } Hari - unit ${tenant.unitName}. Silahkan lakukan pembayaran.$bankInfo$note\n\nTerima Kasih\n\nDari Pengelola\n${user.name}"
            } else {
                body =
                    "Bpk/Ibu bersama ini kami sampaikan masa berlaku sewa Anda $day Hari lagi - Unit ${tenant.unitName}. Silahkan lakukan pembayaran sebelum jatuh tempo.$bankInfo$note\n\nTerima Kasih\n\nDari Pengelola\n${user.name}"
            }
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("smsto:${tenant.numberPhone}")
                putExtra("sms_body", body)
            }
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            }
        } else {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("smsto:${tenant.numberPhone}")
                putExtra("sms_body", "")
            }
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            }
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Kirim SMS gagal", Toast.LENGTH_SHORT).show()
        println("====" + e.message)
    }
}

@SuppressLint("SimpleDateFormat")
fun sendWa(context: Context, viewModel: TenantViewModel, tenant: TenantHome) {
    try {
        if (tenant.unitId != "0") {
            val user = viewModel.user.value
            val myFormat = SimpleDateFormat("yyyy-MM-dd")
            val dateNow = myFormat.format(Calendar.getInstance().time)
            val date1 = myFormat.parse(dateNow)
            val date2 = myFormat.parse(tenant.limitCheckOut)
            val diff = date2!!.time - date1!!.time
            val day = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toString()
            val body: String
            var bankInfo = ""
            var note = ""
            if (user.bankName != "" && user.accountNumber != "" && user.accountOwnerName != "") {
                bankInfo =
                    "\nTransfer ke Rek *${user.bankName} ${user.accountNumber}* (${user.accountOwnerName})"
            }
            if (user.note != "") {
                note = "\n${user.note}"
            }
            if (day.toInt() == 0) {
                body =
                    "Bpk/Ibu bersama ini kami sampaikan masa berlaku sewa Anda tinggal *Hari ini* - unit ${tenant.unitName}. Silahkan lakukan pembayaran.$bankInfo$note\n\nTerima Kasih\n\nDari Pengelola\n${user.name}"

            } else if (day.toInt() < 0) {
                body =
                    "Bpk/Ibu bersama ini kami sampaikan masa berlaku sewa Anda lewat *${
                        abs(
                            day.toInt()
                        )
                    } Hari* - unit ${tenant.unitName}. Silahkan lakukan pembayaran.$bankInfo$note\n\nTerima Kasih\n\nDari Pengelola\n${user.name}"
            } else {
                body =
                    "Bpk/Ibu bersama ini kami sampaikan masa berlaku sewa Anda *$day Hari lagi* - Unit ${tenant.unitName}. Silahkan lakukan pembayaran sebelum jatuh tempo.$bankInfo$note\n\nTerima Kasih\n\nDari Pengelola\n${user.name}"
            }
            sendWhatsApp(context, tenant.numberPhone, body)
        } else {
            sendWhatsApp(context, tenant.numberPhone, "")
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Kirim WA gagal ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
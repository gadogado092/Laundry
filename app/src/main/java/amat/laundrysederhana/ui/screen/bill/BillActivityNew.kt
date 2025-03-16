package amat.laundrysederhana.ui.screen.bill

import amat.laundrysederhana.R
import amat.laundrysederhana.cleanPointZeroFloat
import amat.laundrysederhana.currencyFormatterStringViewZero
import amat.laundrysederhana.dateTimeUniversalToDisplay
import amat.laundrysederhana.di.Injection
import amat.laundrysederhana.leftRightAlign
import amat.laundrysederhana.printConfig
import amat.laundrysederhana.printCustom
import amat.laundrysederhana.ui.common.OnLifecycleEvent
import amat.laundrysederhana.ui.common.UiState
import amat.laundrysederhana.ui.component.BillItem
import amat.laundrysederhana.ui.component.CenterLayout
import amat.laundrysederhana.ui.component.ErrorLayout
import amat.laundrysederhana.ui.component.LoadingLayout
import amat.laundrysederhana.ui.screen.printer.PrinterActivity
import amat.laundrysederhana.ui.screen.transaction.AddTransactionActivity
import amat.laundrysederhana.ui.theme.Blue
import amat.laundrysederhana.ui.theme.FontBlack
import amat.laundrysederhana.ui.theme.FontBlackSoft
import amat.laundrysederhana.ui.theme.FontGrey
import amat.laundrysederhana.ui.theme.FontWhite
import amat.laundrysederhana.ui.theme.GreenDark
import amat.laundrysederhana.ui.theme.GreyLight
import amat.laundrysederhana.ui.theme.LaundryAppTheme
import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Print
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.UUID


class BillActivityNew : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        val id = intent.getStringExtra("id")

        setContent {
            val context = LocalContext.current
            LaundryAppTheme {
                if (id != null) {
                    BillNewScreen(context, id)
                } else {
                    BillNewScreen(context, "")
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, insets ->
            val bottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            view.updatePadding(bottom = bottom)
            insets
        }
    }

    private fun printInvoice(context: Context, dataInvoice: BillUi) {
        if (dataInvoice.printerAddress == "") {
            Toast.makeText(this, "Pilih Printernya Gan", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(
                this,
                "Menghubungkan Ke Printer",
                Toast.LENGTH_SHORT
            )
                .show()

            val bluetoothManager =
                context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            val bluetoothAdapter = bluetoothManager.adapter

            if (bluetoothAdapter == null) {
                Toast.makeText(this, "Bluetooth Adapter Broken", Toast.LENGTH_LONG).show()
            } else {
                val applicationUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
                val bluetoothDevice =
                    bluetoothAdapter.getRemoteDevice(dataInvoice.printerAddress)

                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // : Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                            1
                        )
                    }
//                    Toast.makeText(this, "Printer Permission Problem 1", Toast.LENGTH_LONG)
//                        .show()
                }

                if (!bluetoothAdapter.isEnabled) {
                    Toast.makeText(this, "Bluetooth Tidak Aktif", Toast.LENGTH_LONG).show()
                    return
                }

                val bluetoothSocket =
                    bluetoothDevice.createRfcommSocketToServiceRecord(applicationUUID)

                //check koneksi
                try {
                    bluetoothSocket.connect()
                    if (!bluetoothSocket.isConnected) {
                        Toast.makeText(
                            this,
                            "Printer Tidak Tersambung",
                            Toast.LENGTH_LONG
                        )
                            .show()
                        bluetoothSocket.close()
                        return
                    }
                } catch (e: Exception) {
                    Log.e("ada", e.message.toString())
                    Toast.makeText(
                        this@BillActivityNew,
                        "Printer Socket Tidak Tersambung.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    bluetoothSocket.close()
                    return
                }

                try {

                    if (bluetoothSocket == null) {
                        Toast.makeText(
                            this,
                            "Printer Socket Tidak Tersambung..",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        return
                    } else {
                        Toast.makeText(this, "Printer Mencetak", Toast.LENGTH_SHORT)
                            .show()

                        val outputStream = bluetoothSocket.outputStream

                        //wajib ada ini boss ku
                        val printformat = byteArrayOf(0x1B, 0x21, 0x03)
                        outputStream.write(printformat)


                        //size 1 = large, size 2 = medium, size 3 = small
                        //style 1 = Regular, style 2 = Bold
                        //align 0 = left, align 1 = center, align 2 = right

                        printConfig(outputStream, dataInvoice.businessName, 1, 2, 1)
                        printConfig(outputStream, dataInvoice.businessAddress, 3, 1, 1)
                        printConfig(outputStream, dataInvoice.businessNumberPhone, 3, 1, 1)

                        printCustom(
                            "-------------------------------------------",
                            0,
                            1,
                            outputStream
                        )

                        printCustom(
                            dateTimeUniversalToDisplay(dataInvoice.dateTimeTransaction),
                            0,
                            0,
                            outputStream
                        )
                        printCustom(
                            "Nomor  : ${dataInvoice.invoiceCode}",
                            0,
                            0,
                            outputStream
                        )
                        val status = if (dataInvoice.isFullPayment) "Lunas" else "Belum Lunas"
                        printCustom(
                            "Status : $status",
                            0,
                            0,
                            outputStream
                        )
                        printCustom(
                            "Kasir  : ${dataInvoice.cashierName}",
                            0,
                            0,
                            outputStream
                        )
                        printCustom(
                            "Jumlah Pakaian ${dataInvoice.totalClothes}",
                            0,
                            0,
                            outputStream
                        )

                        printConfig(outputStream, "Nama   : ${dataInvoice.customerName}", 1, 1, 0)

                        if (dataInvoice.noteTransaction != "") {
                            printConfig(
                                outputStream,
                                "Note   : ${dataInvoice.noteTransaction}",
                                3,
                                1,
                                0
                            )
                        }
                        printCustom(
                            "-------------------------------------------",
                            0,
                            1,
                            outputStream
                        )

                        dataInvoice.listDetailTransaction.forEach { item ->
                            printConfig(outputStream, item.productName, 1, 1, 0)

                            printConfig(
                                outputStream,
                                leftRightAlign(
                                    "${cleanPointZeroFloat(item.qty)} ${item.unit}",
                                    currencyFormatterStringViewZero(item.totalPrice),
                                    "2"
                                ), 1, 1, 1
                            )
                            if (item.note != "") {
                                printCustom(
                                    item.note,
                                    0,
                                    0,
                                    outputStream
                                )
                            }

                        }
                        printCustom(
                            "-------------------------------------------",
                            0,
                            1,
                            outputStream
                        )
                        printConfig(
                            outputStream,
                            leftRightAlign(
                                "Total",
                                currencyFormatterStringViewZero(dataInvoice.totalPrice),
                                "2"
                            ), 2, 1, 1
                        )

                        val LF = byteArrayOf(0x0A)
                        outputStream.write(LF)
                        printCustom(dataInvoice.footerNote, 0, 1, outputStream)
                        outputStream.write(LF)
                        outputStream.write(LF)
//                        outputStream.write(LF)

                        outputStream.flush()
                        outputStream.close()
                        bluetoothSocket.close()
                    }

                } catch (e: Exception) {
                    Log.e("bluetooth", e.message.toString())
                    bluetoothSocket.close()
                    Toast.makeText(this, "Error ${e.message}", Toast.LENGTH_LONG)
                        .show()
                }


            }

        }

    }

    @Composable
    fun BillNewScreen(
        context: Context,
        transactionId: String
    ) {

        val viewModel: BillViewModel =
            viewModel(
                factory = BillViewModelFactory(
                    Injection.provideUserRepository(context),
                    Injection.provideTransactionRepository(context),
                    Injection.provideDetailTransactionRepository(context),
                )
            )

        OnLifecycleEvent { owner, event ->
            // do stuff on event
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    if (transactionId != "") {
                        viewModel.getData(transactionId)
                    }
                }

                else -> { /* other stuff */
                }
            }
        }

        if (!viewModel.isProsesDeleteFailed.collectAsState().value.isError) {
            Toast.makeText(context, "Hapus Transaksi Berhasil", Toast.LENGTH_SHORT)
                .show()
            val activity = (context as? Activity)
            activity?.finish()
        } else {
            if (viewModel.isProsesDeleteFailed.collectAsState().value.errorMessage.isNotEmpty()) {
                Toast.makeText(
                    context,
                    viewModel.isProsesDeleteFailed.collectAsState().value.errorMessage,
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

        if (!viewModel.isProsesUpdateStatusFailed.collectAsState().value.isError) {
            Toast.makeText(context, "Update Status Berhasil", Toast.LENGTH_SHORT)
                .show()
            viewModel.getData(transactionId)
        } else {
            if (viewModel.isProsesUpdateStatusFailed.collectAsState().value.errorMessage.isNotEmpty()) {
                Toast.makeText(
                    context,
                    viewModel.isProsesUpdateStatusFailed.collectAsState().value.errorMessage,
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

        Column {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.bill),
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
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (viewModel.dataInvoice.value.printerAddress == "") {
                                Toast.makeText(
                                    context,
                                    "Pilih Printer guys",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                val intent = Intent(context, PrinterActivity::class.java)
                                context.startActivity(intent)
                            } else {
                                printInvoice(context, viewModel.dataInvoice.value)
                            }

                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Print,
                            contentDescription = "",
                            tint = Color.White
                        )
                    }
                }
            )
            if (transactionId == "") {
                ErrorLayout(errorMessage = "ID Transaksi tidak ada") {
                    val activity = (context as? Activity)
                    activity?.finish()
                }
            } else {
                viewModel.stateUi.collectAsState(initial = UiState.Loading).value.let { uiState ->
                    when (uiState) {
                        is UiState.Error -> {
                            ErrorLayout(
                                modifier = Modifier.fillMaxHeight(),
                                errorMessage = uiState.errorMessage
                            ) {
                                viewModel.getData(transactionId)
                            }
                        }

                        UiState.Loading -> {
                            LoadingLayout(modifier = Modifier.fillMaxHeight())
                        }

                        is UiState.Success -> {
                            BillMainArea(viewModel, context, uiState.data, transactionId)
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun BillMainArea(viewModel: BillViewModel, context: Context, data: BillUi, transactionId: String) {
    if (data.listDetailTransaction.isEmpty()) {
        CenterLayout(
            content = {
                Text(
                    text = stringResource(
                        id = R.string.note_empty_data,
                        "List Transaksi"
                    ),
                    color = FontBlack
                )
            }
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        data.businessName, style = TextStyle(
                            fontSize = 18.sp,
                            color = FontBlack,
                        )
                    )
                    Text(
                        data.businessAddress, style = TextStyle(
                            fontSize = 16.sp,
                            color = FontBlack,
                        )
                    )
                    Text(
                        data.businessNumberPhone, style = TextStyle(
                            fontSize = 16.sp,
                            color = FontBlack,
                        )
                    )
                }

                Divider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = GreyLight,
                    thickness = 4.dp
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "Nomor Invoice", style = TextStyle(
                                    fontSize = 14.sp,
                                    color = FontGrey,
                                )
                            )
                            Text(
                                data.invoiceCode, style = TextStyle(
                                    fontSize = 16.sp,
                                    color = FontBlack,
                                )
                            )
                        }

                        Column {
                            Text(
                                "Waktu Transaksi",
                                modifier = Modifier.align(Alignment.End),
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = FontGrey,
                                )
                            )
                            Text(
                                dateTimeUniversalToDisplay(data.dateTimeTransaction),
                                modifier = Modifier.align(Alignment.End),
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    color = FontBlack,
                                )
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column {
                            Text(
                                "Nama Pelanggan", style = TextStyle(
                                    fontSize = 14.sp,
                                    color = FontGrey,
                                )
                            )
                            Text(
                                data.customerName, style = TextStyle(
                                    fontSize = 16.sp,
                                    color = FontBlack,
                                )
                            )
                        }
                        Column {
                            Text(
                                "Status Pembayaran",
                                modifier = Modifier.align(Alignment.End),
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = FontGrey,
                                )
                            )
                            Text(
                                if (data.isFullPayment) "Lunas" else "Belum Lunas",
                                modifier = Modifier.align(Alignment.End),
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    color = FontBlack,
                                )
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column {
                            Text(
                                "Nama Kasir", style = TextStyle(
                                    fontSize = 14.sp,
                                    color = FontGrey,
                                )
                            )
                            Text(
                                data.cashierName, style = TextStyle(
                                    fontSize = 16.sp,
                                    color = FontBlack,
                                )
                            )
                        }
                        Column {
                            Text(
                                "Jumlah Pakaian",
                                modifier = Modifier.align(Alignment.End),
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = FontGrey,
                                )
                            )
                            Text(
                                data.totalClothes,
                                modifier = Modifier.align(Alignment.End),
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    color = FontBlack,
                                )
                            )
                        }
                    }

                    if (data.noteTransaction.isNotEmpty()) {
                        Spacer(Modifier.height(2.dp))
                        Text(
                            "Catatan", style = TextStyle(
                                fontSize = 14.sp,
                                color = FontGrey,
                            )
                        )
                        Text(
                            data.noteTransaction, style = TextStyle(
                                fontSize = 16.sp,
                                color = FontBlack,
                            )
                        )
                    }

                }

                Divider(
                    modifier = Modifier.padding(top = 4.dp),
                    color = GreyLight,
                    thickness = 4.dp
                )

            }

            items(data.listDetailTransaction) { data ->
                BillItem(
                    productName = data.productName,
                    productPrice = currencyFormatterStringViewZero(data.price.toString()),
                    productTotalPrice = currencyFormatterStringViewZero(data.totalPrice),
                    note = data.note,
                    unit = data.unit,
                    qty = data.qty
                )
            }

            item {
                Divider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = GreyLight,
                    thickness = 4.dp
                )

                Row(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total Pembayaran",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = FontBlack,
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    Text(
                        text = currencyFormatterStringViewZero(data.totalPrice),
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = FontBlack,
                        ),
                        fontWeight = FontWeight.Bold,
                    )

                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp, horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = data.footerNote,
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = FontBlack,
                        )
                    )
                }

                Divider(
                    modifier = Modifier.padding(top = 16.dp),
                    color = GreyLight,
                    thickness = 4.dp
                )

                if (!data.isFullPayment) {
                    Button(
                        onClick = {
                            showBottomConfirmUpdateStatus(context, viewModel, transactionId)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp, horizontal = 8.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Blue)
                    ) {
                        Text(text = "Update Status Lunas", color = FontWhite)
                    }
                }

                Button(
                    onClick = {
                        val activity = (context as? Activity)
                        activity?.finish()
                        val intent = Intent(context, AddTransactionActivity::class.java)
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp, horizontal = 8.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Blue)
                ) {
                    Text(text = "Tambah Transaksi Baru", color = FontWhite)
                }

                Button(
                    onClick = {
                        showBottomConfirmDelete(context, viewModel, transactionId)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 8.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = GreyLight)
                ) {
                    Text(text = "Hapus Transaksi", color = FontBlackSoft)
                }
            }

        }
    }

}

private fun showBottomConfirmDelete(
    context: Context,
    viewModel: BillViewModel,
    transactionId: String,
) {
    val bottomSheetDialog = BottomSheetDialog(context)
    bottomSheetDialog.setContentView(R.layout.bottom_sheet_confirm)
    val message = bottomSheetDialog.findViewById<TextView>(R.id.text_message)
    val buttonOk = bottomSheetDialog.findViewById<Button>(R.id.ok_button)

    val messageString =
        "Hapus Transaksi Ini?"

    message?.text = messageString

    buttonOk?.setOnClickListener {
        bottomSheetDialog.dismiss()
        viewModel.deleteTransaction(transactionId)
    }
    bottomSheetDialog.show()

}

private fun showBottomConfirmUpdateStatus(
    context: Context,
    viewModel: BillViewModel,
    transactionId: String,
) {
    val bottomSheetDialog = BottomSheetDialog(context)
    bottomSheetDialog.setContentView(R.layout.bottom_sheet_confirm)
    val message = bottomSheetDialog.findViewById<TextView>(R.id.text_message)
    val buttonOk = bottomSheetDialog.findViewById<Button>(R.id.ok_button)

    val messageString =
        "Update Status Jadi Lunas?"

    message?.text = messageString

    buttonOk?.setOnClickListener {
        bottomSheetDialog.dismiss()
        viewModel.updateStatusTransaction(transactionId)
    }
    bottomSheetDialog.show()

}
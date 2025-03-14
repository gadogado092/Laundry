package amat.laundrysederhana.ui.screen.printer

import amat.laundrysederhana.R
import amat.laundrysederhana.data.User
import amat.laundrysederhana.data.entity.PrinterEntity
import amat.laundrysederhana.di.Injection
import amat.laundrysederhana.ui.common.OnLifecycleEvent
import amat.laundrysederhana.ui.common.UiState
import amat.laundrysederhana.ui.component.CenterLayout
import amat.laundrysederhana.ui.component.ErrorLayout
import amat.laundrysederhana.ui.component.LoadingLayout
import amat.laundrysederhana.ui.theme.FontBlack
import amat.laundrysederhana.ui.theme.FontGrey
import amat.laundrysederhana.ui.theme.FontWhite
import amat.laundrysederhana.ui.theme.GreenDark
import amat.laundrysederhana.ui.theme.GreyLight
import amat.laundrysederhana.ui.theme.LaundryAppTheme
import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import java.util.UUID


class PrinterActivity : ComponentActivity() {

    private val requestEnableBluetooth = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            LaundryAppTheme {
                PrinterScreen(context)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, insets ->
            val bottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            view.updatePadding(bottom = bottom)
            insets
        }

    }

    private fun enableBluetooth(context: Context, myViewModel: PrinterViewModel) {
        Log.d("Bluetooth", "enableBluetooth click")
        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter

        if (!bluetoothAdapter.isEnabled) {
            Log.d("Bluetooth", "Bluetooth disable")
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("Bluetooth", "enableBluetooth permission")
                //Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, requestEnableBluetooth)
                return
            }
        } else {
            Log.d("Bluetooth", "enableBluetooth")
            getPairedDevices(context, myViewModel)
        }
    }

    private fun getPairedDevices(context: Context, myViewModel: PrinterViewModel) {

        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            val pairedDevices = bluetoothAdapter.bondedDevices
            val pairedDevicesList = pairedDevices.toList()
            // Tampilkan daftar perangkat terpasang
            val listPrint = mutableListOf<PrinterEntity>()
            for (device in pairedDevicesList) {
                Log.d("Bluetooth", "${device.name} - ${device.address}")
                listPrint.add(PrinterEntity(device.name, device.address))
            }
            myViewModel.updatePrinterList(listPrint)
            return
        }

    }

    @Composable
    fun PrinterScreen(
        context: Context
    ) {

        val viewModel: PrinterViewModel =
            viewModel(factory = PrinterViewModelFactory(Injection.provideUserRepository(context)))

        OnLifecycleEvent { owner, event ->
            // do stuff on event
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    viewModel.getDetail()
                }

                else -> {}
            }
        }

        //START UI
        Column {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.title_printer),
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

            viewModel.stateInitUser.collectAsState(initial = UiState.Loading).value.let { uiState ->
                when (uiState) {
                    is UiState.Error -> {
                        ErrorLayout(
                            modifier = Modifier.fillMaxHeight(),
                            errorMessage = uiState.errorMessage
                        ) {
                            viewModel.getDetail()
                        }
                    }

                    UiState.Loading -> {
                        LoadingLayout(modifier = Modifier.fillMaxHeight())
                    }

                    is UiState.Success -> {
                        PrinterMainArea(viewModel, context, uiState.data)
                    }
                }
            }

        }
    }

    @Composable
    fun PrinterMainArea(viewModel: PrinterViewModel, context: Context, data: User) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                "Tips Koneksi Ke Printer", style = TextStyle(
                    fontSize = 14.sp,
                    color = FontBlack,
                )
            )
            Text(
                "*Aktifkan Printer Thermal" +
                        "\n*Aktifkan Bluetooth Hp" +
                        "\n*Pairing Hp Ke Printer Thermal" +
                        "\n*Berikan Permission Bluetooth" +
                        "\n*Scan Pairing List" +
                        "\n*Pilih Printer Pada History",
                style = TextStyle(
                    fontSize = 16.sp,
                    color = FontBlack,
                ),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Printer Yang Digunakan Saat Ini", style = TextStyle(
                    fontSize = 16.sp,
                    color = FontGrey,
                )
            )
            Text(
                if (data.printerName == "") "Tidak Ada" else data.printerName,
                style = TextStyle(
                    fontSize = 16.sp,
                    color = FontBlack,
                )
            )
            val coroutineScope = rememberCoroutineScope()

            Button(
                onClick = {
                    if (data.printerAddress == "") {
                        Toast.makeText(context, "Pilih Printer Pada List", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        coroutineScope.launch {
                            testPrint(context, data)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = GreenDark)
            ) {
                Text(
                    text = "Tes Print",
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = FontWhite,
                    )
                )
            }

            Button(
                onClick = {
                    enableBluetooth(context, viewModel)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = GreenDark)
            ) {
                Text(
                    text = "Scan History Printer",
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = FontWhite,
                    )
                )
            }

            Text(
                "List History Printer ",
                style = TextStyle(
                    fontSize = 18.sp,
                    color = FontBlack,
                )
            )

            viewModel.statePrinter.collectAsState(initial = UiState.Loading).value.let { uiState ->
                when (uiState) {
                    is UiState.Error -> {
                        ErrorLayout(
                            modifier = Modifier.fillMaxHeight(),
                            errorMessage = uiState.errorMessage
                        ) {
                            enableBluetooth(context, viewModel)
                        }
                    }

                    UiState.Loading -> {
                        LoadingLayout(modifier = Modifier.fillMaxHeight())
                    }

                    is UiState.Success -> {
                        if (uiState.data.isEmpty()) {
                            CenterLayout(
                                content = {
                                    Text(
                                        text = stringResource(
                                            id = R.string.note_empty_data,
                                            "Printer"
                                        ),
                                        color = FontBlack
                                    )
                                }
                            )
                        } else {
                            LazyColumn {
                                items(uiState.data) { item ->
                                    Column(
                                        modifier = Modifier
                                            .clickable {
                                                viewModel.updatePrinterSelected(data.id, item)
                                            }
                                            .padding(4.dp)
                                    ) {
                                        Spacer(Modifier.height(4.dp))
                                        Text(item.name)
                                        Divider(
                                            modifier = Modifier.padding(top = 4.dp),
                                            color = GreyLight,
                                            thickness = 1.dp
                                        )
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }


    private fun testPrint(context: Context, data: User) {
        if (data.printerAddress == "") {
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
                    bluetoothAdapter.getRemoteDevice(data.printerAddress)

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
                    } else {
                        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        startActivityForResult(enableBtIntent, requestEnableBluetooth)
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
                    Toast.makeText(
                        this,
                        "Printer Socket Tidak Tersambung" + e.message,
                        Toast.LENGTH_LONG
                    )
                        .show()
                    bluetoothSocket.close()
                    return
                }

                try {

                    if (bluetoothSocket == null) {
                        Toast.makeText(
                            this,
                            "Printer Socket Tidak Tersambung",
                            Toast.LENGTH_LONG
                        )
                            .show()
                        return
                    } else {
                        Toast.makeText(this, "Printer Mencetak", Toast.LENGTH_LONG)
                            .show()

                        val data = "ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFG"

                        val outputStream = bluetoothSocket.outputStream

                        val printformat = byteArrayOf(0x1B, 0x21, 0x03)
                        outputStream.write(printformat)

                        val cc = byteArrayOf(0x1B, 0x21, 0x03)
                        outputStream.write(cc)

                        val ESC_ALIGN_LEFT = byteArrayOf(0x1b, 'a'.code.toByte(), 0x00)
                        outputStream.write(ESC_ALIGN_LEFT)

                        val bytes = data.toByteArray()

                        outputStream.write(bytes)

                        val LF = byteArrayOf(0x0A)
                        outputStream.write(LF)
                        outputStream.write(LF)
                        outputStream.write(LF)
                        outputStream.write(LF)

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

}


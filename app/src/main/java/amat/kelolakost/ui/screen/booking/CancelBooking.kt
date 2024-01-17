package amat.kelolakost.ui.screen.booking

import amat.kelolakost.R
import amat.kelolakost.cleanCurrencyFormatter
import amat.kelolakost.currencyFormatterStringViewZero
import amat.kelolakost.dateToDisplayMidFormat
import amat.kelolakost.di.Injection
import amat.kelolakost.ui.common.OnLifecycleEvent
import amat.kelolakost.ui.common.UiState
import amat.kelolakost.ui.component.BoxRectangle
import amat.kelolakost.ui.component.ErrorLayout
import amat.kelolakost.ui.component.InformationBox
import amat.kelolakost.ui.component.LoadingLayout
import amat.kelolakost.ui.component.MyOutlinedTextFieldCurrency
import amat.kelolakost.ui.component.SubBooking
import amat.kelolakost.ui.theme.FontBlack
import amat.kelolakost.ui.theme.FontWhite
import amat.kelolakost.ui.theme.GreenDark
import amat.kelolakost.ui.theme.KelolaKostTheme
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.RadioButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class CancelBooking : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        val bookingId = intent.getStringExtra("bookingId")

        setContent {
            val context = LocalContext.current
            KelolaKostTheme {
                CancelBookingScreen(context = context, bookingId = bookingId)
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
fun CancelBookingScreen(
    modifier: Modifier = Modifier,
    context: Context,
    bookingId: String?
) {

    val myViewModel: CancelBookingViewModel =
        viewModel(
            factory = CancelBookingViewModelFactory(
                Injection.provideBookingRepository(context)
            )
        )

    OnLifecycleEvent { owner, event ->
        // do stuff on event
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                if (bookingId != null) {
                    myViewModel.getBooking(bookingId)
                }
            }

            else -> {}
        }
    }

    if (!myViewModel.isProsesSuccess.collectAsState().value.isError) {
        Toast.makeText(
            context,
            stringResource(id = R.string.success_cancel_booking),
            Toast.LENGTH_SHORT
        )
            .show()
        val activity = (context as? Activity)
        activity?.finish()
    } else {
        if (myViewModel.isProsesSuccess.collectAsState().value.errorMessage.isNotEmpty()) {
            Toast.makeText(
                context,
                myViewModel.isProsesSuccess.collectAsState().value.errorMessage,
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    //START UI
    Column(modifier = modifier) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.cancel_booking),
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

        myViewModel.stateBooking.collectAsState(initial = UiState.Loading).value.let { uiState ->
            when (uiState) {
                is UiState.Error -> {
                    ErrorLayout(errorMessage = uiState.errorMessage) {
                        if (bookingId != null) {
                            myViewModel.getBooking(bookingId)
                        }
                    }
                }

                UiState.Loading -> {
                    LoadingLayout()
                }

                is UiState.Success -> {
                    ContentCancelBooking(myViewModel, context)
                }
            }
        }

    }

}

@Composable
fun ContentCancelBooking(myViewModel: CancelBookingViewModel, context: Context) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        val data = myViewModel.getData()
        InformationBox {
            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = data.unitName[0].uppercase() + data.unitName.drop(1),
                        style = TextStyle(
                            fontSize = 18.sp,
                            color = FontBlack,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    BoxRectangle(title = data.unitTypeName[0].uppercase() + data.unitTypeName.drop(1))
                }
                SubBooking(Icons.Default.Person, data.name)
                SubBooking(Icons.Default.PhoneIphone, data.numberPhone)
                SubBooking(Icons.Default.House, data.kostName)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Rencana Check-In")
                    Text(text = dateToDisplayMidFormat(data.planCheckIn))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Uang Booking")
                    Text(text = currencyFormatterStringViewZero(data.nominalBooking))
                }
            }
        }

        Spacer(modifier = Modifier.padding(top = 8.dp))

        MyOutlinedTextFieldCurrency(
            label = "Nominal Pengembalian Dana",
            value = myViewModel.stateUi.collectAsState().value.nominal.replace(
                ".",
                ""
            ),
            onValueChange = {
                myViewModel.setNominal(it)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth(),
            isError = myViewModel.isNominalValid.collectAsState().value.isError,
            errorMessage = myViewModel.isNominalValid.collectAsState().value.errorMessage,
            currencyValue = myViewModel.stateUi.collectAsState().value.nominal
        )

        Text(
            text = stringResource(id = R.string.payment_return_via),
            style = TextStyle(color = FontBlack),
            fontSize = 16.sp
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .weight(1F)
                    .clickable {
                        myViewModel.setPaymentType(true)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (myViewModel.stateUi.collectAsState().value.isCash),
                    onClick = { myViewModel.setPaymentType(true) }
                )
                Text(
                    text = "Cash", style = TextStyle(color = FontBlack),
                    fontSize = 16.sp
                )
            }
            Row(
                modifier = Modifier
                    .weight(1F)
                    .clickable {
                        myViewModel.setPaymentType(false)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (!myViewModel.stateUi.collectAsState().value.isCash),
                    onClick = { myViewModel.setPaymentType(false) }
                )
                Text(
                    text = "Transfer", style = TextStyle(color = FontBlack),
                    fontSize = 16.sp
                )
            }
        }

        Button(
            onClick = {
                if (myViewModel.dataIsComplete()){
                    showBottomConfirm(context, myViewModel)
                }

            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = GreenDark)
        ) {
            Text(text = stringResource(id = R.string.process), color = FontWhite)
        }


    }
}

private fun showBottomConfirm(
    context: Context,
    myViewModel: CancelBookingViewModel
) {
    val bottomSheetDialog = BottomSheetDialog(context)
    bottomSheetDialog.setContentView(R.layout.bottom_sheet_confirm)
    val message = bottomSheetDialog.findViewById<TextView>(R.id.text_message)
    val buttonOk = bottomSheetDialog.findViewById<Button>(R.id.ok_button)

    var messageString =
        "Batalkan Booking ${myViewModel.stateUi.value.name} dengan pengembalian dana ${myViewModel.stateUi.value.nominal} ?"
    if (cleanCurrencyFormatter(myViewModel.stateUi.value.nominal) < 1) {
        messageString =
            "Batalkan Booking ${myViewModel.stateUi.value.name} tanpa pengembalian dana?"
    }

    message?.text = messageString

    buttonOk?.setOnClickListener {
        bottomSheetDialog.dismiss()
        myViewModel.process()
    }
    bottomSheetDialog.show()

}
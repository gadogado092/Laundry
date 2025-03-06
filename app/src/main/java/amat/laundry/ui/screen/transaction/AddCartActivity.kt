package amat.laundry.ui.screen.transaction

import amat.laundry.R
import amat.laundry.currencyFormatterStringViewZero
import amat.laundry.ui.component.BoxPrice
import amat.laundry.ui.component.MyOutlinedTextField
import amat.laundry.ui.theme.ErrorColor
import amat.laundry.ui.theme.FontBlack
import amat.laundry.ui.theme.FontWhite
import amat.laundry.ui.theme.GreenDark
import amat.laundry.ui.theme.LaundryAppTheme
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

class AddCartActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        val id = intent.getStringExtra("id")

        setContent {
            val context = LocalContext.current
            LaundryAppTheme {
                if (id != null) {
                    AddCartScreen(context, id)
                } else {
                    AddCartScreen(context, "")
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
fun AddCartScreen(
    context: Context,
    productId: String
) {

    //START UI
    Column {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.title_update_cart),
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
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier.padding(bottom = 8.dp),
                text = "Nama Produk",
                style = TextStyle(color = FontBlack),
                fontSize = 18.sp
            )
            Text(
                modifier = Modifier.padding(bottom = 8.dp),
                text = "Harga Produk",
                style = TextStyle(color = FontBlack),
                fontSize = 16.sp
            )
            MyOutlinedTextField(
                label = "Qty(Kg) ex:1.4",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
//                value = userViewModel.user.collectAsState().value.businessName,
                value = "",
                onValueChange = {
//                    userViewModel.setBusinessName(it)
                },
//                isError = userViewModel.isBusinessNameValid.collectAsState().value.isError,
//                errorMessage = userViewModel.isBusinessNameValid.collectAsState().value.errorMessage
            )
            MyOutlinedTextField(
                label = "Catatan",
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                modifier = Modifier.fillMaxWidth(),
//                value = userViewModel.user.collectAsState().value.businessName,
                value = "",
                onValueChange = {
//                    userViewModel.setBusinessName(it)
                },
//                isError = userViewModel.isBusinessNameValid.collectAsState().value.isError,
//                errorMessage = userViewModel.isBusinessNameValid.collectAsState().value.errorMessage
            )

            Text(
                text = "Total Harga",
                style = TextStyle(color = FontBlack, fontWeight = FontWeight.Bold),
                fontSize = 16.sp
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BoxPrice(
                    title = currencyFormatterStringViewZero("25000"),
                    fontSize = 20.sp
                )
            }

            Button(
                onClick = {
//                    userViewModel.prosesRegistration()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = GreenDark)
            ) {
                Text(text = "Simpan", color = FontWhite)
            }
        }
    }
}
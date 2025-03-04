package amat.laundry.ui.screen.transaction

import amat.laundry.R
import amat.laundry.di.Injection
import amat.laundry.ui.screen.user.NewUserViewModel
import amat.laundry.ui.screen.user.NewUserViewModelFactory
import amat.laundry.ui.theme.Blue
import amat.laundry.ui.theme.FontWhite
import amat.laundry.ui.theme.GreenDark
import amat.laundry.ui.theme.LaundryAppTheme
import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.viewmodel.compose.viewModel

class AddTransactionActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            LaundryAppTheme {
                AddTransactionScreen(context = context)
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
fun AddTransactionScreen(
    modifier: Modifier = Modifier,
    context: Context
) {

    val viewModel: AddTransactionViewModel =
        viewModel(
            factory = AddTransactionViewModelFactory(
                Injection.provideProductRepository(context),
                Injection.provideCategoryRepository(context)
            )
        )

    Column {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.title_add_transaction),
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
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1F)
                    .fillMaxWidth()
                    .background(Color.Red)
            ) {
                Text("center")
            }

            //bottom area
            Card(
                modifier = Modifier
                    .padding(4.dp, 8.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(Blue),
                border = BorderStroke(0.5.dp, Blue),
            ) {
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("3 Layanan", color = Color.White, fontSize = 16.sp)
                    Row {
                        Text("30.000", color = Color.White, fontSize = 16.sp)
                        Spacer(Modifier.width(2.dp))
                        Image(
                            imageVector = Icons.Default.ShoppingBasket,
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(
                                Color.White
                            ),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
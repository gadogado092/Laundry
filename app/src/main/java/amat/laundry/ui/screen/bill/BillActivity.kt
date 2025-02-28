package amat.laundry.ui.screen.bill

import amat.laundry.R
import amat.laundry.data.entity.BillEntity
import amat.laundry.getSerializable
import amat.laundry.ui.component.InformationBox
import amat.laundry.ui.theme.FontBlack
import amat.laundry.ui.theme.FontWhite
import amat.laundry.ui.theme.GreenDark
import amat.laundry.ui.theme.KelolaKostTheme
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.drawToBitmap
import androidx.core.view.updatePadding

class BillActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val billEntity = getSerializable(this, "object", BillEntity::class.java)
        val tenantNumberPhone = intent.getStringExtra("tenantNumberPhone")

        setContent {
            val context = LocalContext.current
            KelolaKostTheme {
                BillScreen(
                    context = context,
                    billEntity = billEntity,
                    tenantNumberPhone = tenantNumberPhone
                )
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
fun BillScreen(
    context: Context,
    modifier: Modifier = Modifier,
    billEntity: BillEntity,
    tenantNumberPhone: String?
) {
    Column(modifier = modifier) {
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
            }
        )
        val screenshotableComposable = screenshotableComposable(
            content = {
                InformationBox(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_splash),
                            contentDescription = "Icon App",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                        )
                        Text(
                            text = if (billEntity.kostName.isEmpty()) "Kelola Kost" else billEntity.kostName,
                            style = TextStyle(
                                fontWeight = FontWeight.Medium,
                                fontSize = 18.sp,
                                color = FontBlack
                            )
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = billEntity.nominal,
                                style = TextStyle(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 16.sp,
                                    color = FontBlack
                                )
                            )
                            Text(
                                text = billEntity.createAt,
                                style = TextStyle(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 16.sp,
                                    color = FontBlack
                                )
                            )
                        }
                        Text(text = billEntity.note, textAlign = TextAlign.Justify, color = FontBlack)
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {

            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp, horizontal = 8.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = GreenDark)
        ) {
            Text(text = "Download Bukti Pembayaran", color = FontWhite)
        }

        Button(
            onClick = {

            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp, horizontal = 8.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = GreenDark)
        ) {
            Text(text = "Bagikan Gambar Bukti Via Wa", color = FontWhite)
        }

        if (tenantNumberPhone != null) {
            Button(
                onClick = {

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp, horizontal = 8.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = GreenDark)
            ) {
                Text(text = "Kirim Bukti Via Wa", color = FontWhite)
            }

            Button(
                onClick = {

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp, horizontal = 8.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = GreenDark)
            ) {
                Text(text = "Kirim Bukti Via Sms", color = FontWhite)
            }
        }
    }
}

@Composable
fun screenshotableComposable(content: @Composable () -> Unit): () -> Bitmap {
    val context = LocalContext.current
    val composeView = remember { ComposeView(context = context) }
    fun captureBitmap(): Bitmap = composeView.drawToBitmap()
    AndroidView(
        factory = {
            composeView.apply {
                setContent {
                    content()
                    /*...content...*/
                }
            }
        },
        modifier = Modifier.wrapContentSize(unbounded = true)   //  Make sure to set unbounded true to draw beyond screen area
    )

    return ::captureBitmap
}
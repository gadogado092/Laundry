package amat.laundrysederhana.ui.screen.onboard

import amat.laundrysederhana.R
import amat.laundrysederhana.ui.screen.onboard.Tags.TAG_ONBOARD_SCREEN
import amat.laundrysederhana.ui.screen.onboard.Tags.TAG_ONBOARD_SCREEN_IMAGE_VIEW
import amat.laundrysederhana.ui.screen.onboard.Tags.TAG_ONBOARD_SCREEN_NAV_BUTTON
import amat.laundrysederhana.ui.screen.onboard.Tags.TAG_ONBOARD_TAG_ROW
import amat.laundrysederhana.ui.screen.user.NewUserActivity
import amat.laundrysederhana.ui.theme.FontBlack
import amat.laundrysederhana.ui.theme.FontWhite
import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

val onboardPagesList = listOf(
    OnboardPage(
        imageRes = R.drawable.ic_splash,
        title = "Selamat Datang",
        description = "Aplikasi La-Undry merupakan aplikasi pembukuan untuk pemilik usaha laundry berskala kecil yang dilengkapi dengan fungsi utama membantu dalam memantau pakaian yang sementara diproses, siap diambil maupun selesai, mencatat identitas pelanggan, pembayaran, laporan pemasukkan dan pengeluaran. La-Undry berbasis offline atau data tersimpan pada smartphone pengguna, sehingga dapat digunakan tanpa jaringan internet"
    ), OnboardPage(
        imageRes = R.drawable.ic_splash,
        title = "Jelajahi Fitur Menarik",
        description = "Data Pelanggan yang loyal \r\nData layanan yang tersedia" +
                "\r\nSupport..." +
                "\r\nPembayaran..."
    ), OnboardPage(
        imageRes = R.drawable.ic_splash,
        title = "Ketentuan La-Undry",
        description = "*  Penyimpanan berbasis Offline\r\n* Tambahkan Printer Kasir Pada Menu Lainnya\r\n* Pay As You Go KeKost mulai dari 50.000/Bulan"
    )
)

object Tags {
    const val TAG_ONBOARD_SCREEN = "onboard_screen"
    const val TAG_ONBOARD_SCREEN_IMAGE_VIEW = "onboard_screen_image"
    const val TAG_ONBOARD_SCREEN_NAV_BUTTON = "nav_button"
    const val TAG_ONBOARD_TAG_ROW = "tag_row"
}

@Composable
fun OnboardScreen() {
    val context = LocalContext.current

    val onboardPages = onboardPagesList

    val currentPage = remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .testTag(TAG_ONBOARD_SCREEN)
    ) {
        OnBoardImageView(
            modifier = Modifier
                .weight(0.7f)
                .fillMaxWidth(),
            currentPage = onboardPages[currentPage.value]
        )

        OnBoardDetails(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            currentPage = onboardPages[currentPage.value]
        )

        OnBoardNavButton(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp),
            currentPage = currentPage.value,
            noOfPages = onboardPages.size,
            onCompleteClicked = {
                val activity = (context as? Activity)
                val intent = Intent(context, NewUserActivity::class.java)
                context.startActivity(intent)
                activity?.finish()
            }
        ) {
            currentPage.value++
        }

        TabSelector(
            onboardPages = onboardPages,
            currentPage = currentPage.value
        ) { index ->
            currentPage.value = index
        }
    }
}

@Composable
fun OnBoardImageView(modifier: Modifier = Modifier, currentPage: OnboardPage) {
    val imageRes = currentPage.imageRes
    Box(
        modifier = modifier
            .testTag(TAG_ONBOARD_SCREEN_IMAGE_VIEW + currentPage.title)
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillWidth
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.BottomCenter)
                .graphicsLayer {
                    // Apply alpha to create the fading effect
                    alpha = 0.6f
                }
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            Pair(0.8f, Color.Transparent), Pair(1f, Color.White)
                        )
                    )
                ))
    }
}

@Composable
fun OnBoardDetails(
    modifier: Modifier = Modifier, currentPage: OnboardPage
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = currentPage.title,
            style = MaterialTheme.typography.displaySmall,
            textAlign = TextAlign.Center,
            color = FontBlack,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = currentPage.description,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Justify,
            color = FontBlack,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun OnBoardNavButton(
    modifier: Modifier = Modifier,
    currentPage: Int,
    noOfPages: Int,
    onCompleteClicked: () -> Unit,
    onNextClicked: () -> Unit
) {

    val context = LocalContext.current

    if (currentPage < noOfPages - 1) {
        Button(
            onClick = {
                if (currentPage < noOfPages - 1) {
                    onNextClicked()
                } else {
                    onCompleteClicked()
                }
            }, modifier = modifier
                .testTag(TAG_ONBOARD_SCREEN_NAV_BUTTON)
                .fillMaxWidth(0.6F)
        ) {
            Text(text = "Selanjutnya", color = FontWhite)
        }
    } else {
        Column(modifier = Modifier.fillMaxWidth()) {
//            TextButton(
//                onClick = {
//                    val accountBackupPreference = AccountBackupPreference(context).getAccount()
//                    if (accountBackupPreference.isLogin) {
//                        val activity = (context as? Activity)
//                        val intent = Intent(context, BackUpActivity::class.java)
//                        context.startActivity(intent)
//                        activity?.finish()
//                    } else {
//                        val activity = (context as? Activity)
//                        val intent = Intent(context, LoginActivity::class.java)
//                        context.startActivity(intent)
//                        activity?.finish()
//                    }
//                },
//                modifier = Modifier
//                    .fillMaxWidth(),
//            ) {
//                Text(text = "Restore Data", color = GreenDark)
//            }
            Button(
                onClick = {
                    if (currentPage < noOfPages - 1) {
                        onNextClicked()
                    } else {
                        onCompleteClicked()
                    }
                },
                modifier = modifier
                    .testTag(TAG_ONBOARD_SCREEN_NAV_BUTTON)
                    .fillMaxWidth(0.6F),
            ) {
                Text(text = "Coba Sekarang", color = FontWhite)
            }
        }
    }
}

@Composable
fun TabSelector(onboardPages: List<OnboardPage>, currentPage: Int, onTabSelected: (Int) -> Unit) {
    TabRow(
        selectedTabIndex = currentPage,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .testTag(TAG_ONBOARD_TAG_ROW)

    ) {
        onboardPages.forEachIndexed { index, _ ->
            Tab(selected = index == currentPage, onClick = {
                onTabSelected(index)
            }, modifier = Modifier.padding(16.dp), content = {
                Box(
                    modifier = Modifier
                        .testTag("$TAG_ONBOARD_TAG_ROW$index")
                        .size(8.dp)
                        .background(
                            color = if (index == currentPage) MaterialTheme.colorScheme.onPrimary
                            else Color.LightGray, shape = RoundedCornerShape(4.dp)
                        )
                )
            })
        }
    }
}

data class OnboardPage(
    val imageRes: Int, val title: String, val description: String
)
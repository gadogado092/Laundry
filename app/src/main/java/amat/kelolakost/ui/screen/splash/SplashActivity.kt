package amat.kelolakost.ui.screen.splash

import amat.kelolakost.R
import amat.kelolakost.di.Injection
import amat.kelolakost.ui.common.OnLifecycleEvent
import amat.kelolakost.ui.screen.main.MainActivity
import amat.kelolakost.ui.screen.onboard.OnboardActivity
import amat.kelolakost.ui.theme.FontBlack
import amat.kelolakost.ui.theme.FontGrey
import amat.kelolakost.ui.theme.GreenDark
import amat.kelolakost.ui.theme.KelolaKostTheme
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            val viewModel: SplashViewModel =
                viewModel(factory = SplashViewModelFactory(Injection.provideMainRepository(context)))

            OnLifecycleEvent { owner, event ->
                // do stuff on event
                when (event) {

                    Lifecycle.Event.ON_RESUME -> {
                        val intentMainActivity = Intent(this, MainActivity::class.java)

                        val intentOnboardActivity = Intent(this, OnboardActivity::class.java)

                        viewModel.getAllUser().observe(this@SplashActivity) {
                            lifecycleScope.launch {
                                delay(2000)
                                if (it.isEmpty()) {
                                    launch(context = Dispatchers.Main) {
                                        startActivity(intentOnboardActivity)
                                        finish()
                                    }
                                } else {
                                    launch(context = Dispatchers.Main) {
                                        startActivity(intentMainActivity)
                                        finish()
                                    }
                                }
                            }
                        }
                    }

                    else -> { /* other stuff */
                    }

                }
            }

            //Tampilan
            KelolaKostTheme {
                SplashScreen("0.0.1")
            }

        }
    }

    @Composable
    fun SplashScreen(appVersion: String) {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_splash),
                        contentDescription = "Icon App",
                        modifier = Modifier
                            .size(128.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Kelola Kost",
                        color = FontBlack,
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(fontSize = 20.sp),
                    )
                    Text(
                        text = "Lebih Mudah Kelola Kost",
                        color = GreenDark,
                        style = TextStyle(fontSize = 16.sp),
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = appVersion, color = FontGrey,
                        style = TextStyle(fontSize = 14.sp),
                    )
                }
            }
        }
    }

    @Composable
    @Preview(showBackground = true)
    fun SplashScreenPreview() {
        KelolaKostTheme {
            SplashScreen("0.0.1")
        }
    }
}
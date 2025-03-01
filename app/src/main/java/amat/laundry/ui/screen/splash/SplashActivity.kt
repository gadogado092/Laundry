package amat.laundry.ui.screen.splash

import amat.laundry.R
import amat.laundry.di.Injection
import amat.laundry.ui.common.OnLifecycleEvent
import amat.laundry.ui.screen.main.MainActivity
import amat.laundry.ui.screen.onboard.OnboardActivity
import amat.laundry.ui.theme.FontBlack
import amat.laundry.ui.theme.FontGrey
import amat.laundry.ui.theme.GreenDark
import amat.laundry.ui.theme.LaundryAppTheme
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
            LaundryAppTheme {
                SplashScreen(getVersionName())
            }

        }
    }

    private fun getVersionName(): String {
        return try {
            val packageManager = packageManager
            val packageInfo = packageManager.getPackageInfo(
                packageName, 0
            )
            val versionName = packageInfo.versionName
            versionName
        } catch (e: Exception) {
            "${e.message}"
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
                        text = "La-Undry",
                        color = FontBlack,
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(fontSize = 20.sp),
                    )
                    Text(
                        text = "Lebih Mudah Kelola Laundry",
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
        LaundryAppTheme {
            SplashScreen("0.0.1")
        }
    }
}
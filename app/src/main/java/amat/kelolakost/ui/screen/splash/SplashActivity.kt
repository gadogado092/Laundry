package amat.kelolakost.ui.screen.splash

import amat.kelolakost.di.Injection
import amat.kelolakost.ui.common.OnLifecycleEvent
import amat.kelolakost.ui.screen.information.InformationActivity
import amat.kelolakost.ui.screen.main.MainActivity
import amat.kelolakost.ui.theme.KelolaKostTheme
import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {

    companion object {
        val TAG = "splash"
    }

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
                        intentMainActivity.flags =
                            FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK

                        val intentInformationActivity =
                            Intent(this, InformationActivity::class.java)
                        intentInformationActivity.flags =
                            FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK

                        viewModel.getAllUser().observe(this@SplashActivity) {
                            lifecycleScope.launch {
                                delay(2500)
                                if (it.isEmpty()) {
                                    launch(context = Dispatchers.Main) {
                                        startActivity(intentInformationActivity)
                                    }
                                } else {
                                    launch(context = Dispatchers.Main) {
                                        startActivity(intentMainActivity)
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
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        Text(text = "Splash")
                    }
                }
            }

        }
    }

}
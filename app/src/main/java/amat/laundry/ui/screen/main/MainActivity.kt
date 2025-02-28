package amat.laundry.ui.screen.main

import amat.laundry.R
import amat.laundry.di.Injection
import amat.laundry.sendWhatsApp
import amat.laundry.ui.navigation.NavigationItem
import amat.laundry.ui.navigation.Screen
import amat.laundry.ui.screen.user.UpdateUserActivity
import amat.laundry.ui.theme.FontWhite
import amat.laundry.ui.theme.GreenDark
import amat.laundry.ui.theme.GreyLight2
import amat.laundry.ui.theme.KelolaKostTheme
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KelolaKostTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Text(text = "MainActivity")
                }
            }
        }
    }
}

//@Composable
//fun MainScreen(
//    modifier: Modifier = Modifier,
//    navController: NavHostController = rememberNavController()
//) {
//    val navBackStackEntry by navController.currentBackStackEntryAsState()
//    val currentRoute = navBackStackEntry?.destination?.route
//
//    val context = LocalContext.current
//
//    val myViewModel: MainViewModel =
//        viewModel(
//            factory = MainViewModelFactory(
//                Injection.provideUserRepository(context)
//            )
//        )
//
//    Scaffold(
//        bottomBar = {
//            when (currentRoute) {
//                Screen.Unit.route -> {
//                    BottomBar(navController)
//                }
//
//                Screen.Tenant.route -> {
//                    BottomBar(navController)
//                }
//
//                Screen.CashFlow.route -> {
//                    BottomBar(navController)
//                }
//
//                Screen.Other.route -> {
//                    BottomBar(navController)
//                }
//            }
//        },
//        modifier = modifier
//    )
//    { innerPadding ->
//        NavHost(
//            navController = navController,
//            startDestination = Screen.Unit.route,
//            modifier = Modifier.padding(innerPadding)
//        ) {
//            composable(Screen.Unit.route) {
//                UnitScreen(context = context)
//            }
//            composable(Screen.Tenant.route) {
//                TenantScreen(context = context)
//            }
//            composable(Screen.CashFlow.route) {
//                CashFlowScreen(context = context)
//            }
//            composable(Screen.Other.route) {
//                val urlTutorial = stringResource(R.string.url_tutorial_app)
//                val numberCs = stringResource(R.string.number_cs)
//                val messageCs =
//                    stringResource(R.string.message_cs, stringResource(R.string.app_name))
//                OtherScreen(
//                    context = context,
//                    onClickCsExtend = {
//                        sendWhatsApp(
//                            context,
//                            numberCs,
//                            it,
//                            myViewModel.typeWa.value
//                        )
//                    },
//                    navigateToBooking = {
//                        val intent = Intent(context, BookingActivity::class.java)
//                        context.startActivity(intent)
//                    },
//                    navigateCreditTenant = {
//                        val intent = Intent(context, CreditTenantActivity::class.java)
//                        context.startActivity(intent)
//                    },
//                    navigateToDebtCredit = {
//                        val intent = Intent(context, CreditDebitActivity::class.java)
//                        context.startActivity(intent)
//                    },
//                    navigateToKost = {
//                        navController.navigate(Screen.Kost.route)
//                    },
//                    navigateToUnitType = {
//                        navController.navigate(Screen.UnitType.route)
//                    },
//                    navigateToProfile = {
//                        val intent = Intent(context, UpdateUserActivity::class.java)
//                        context.startActivity(intent)
//                    },
//                    onClickTutorial = {
//                        try {
//                            val webIntent =
//                                Intent(
//                                    Intent.ACTION_VIEW,
//                                    Uri.parse(urlTutorial)
//                                )
//                            context.startActivity(webIntent)
//                        } catch (ex: ActivityNotFoundException) {
//                            Toast.makeText(
//                                context,
//                                "Tidak Bisa Akses Tutorial",
//                                Toast.LENGTH_SHORT
//                            )
//                                .show()
//                        }
//                    },
//                    onClickCostumerService = {
//                        sendWhatsApp(
//                            context,
//                            numberCs,
//                            messageCs,
//                            typeWa = it,
//                        )
//                    },
//                )
//            }
//            composable(Screen.Kost.route) {
//                KostScreen(context = context, navigateBack = {
//                    navController.navigateUp()
//                })
//            }
//            composable(Screen.UnitType.route) {
//                UnitTypeScreen(context = context, navigateBack = {
//                    navController.navigateUp()
//                })
//            }
//        }
//    }
//}
//
//@Composable
//fun BottomBar(
//    navController: NavHostController,
//    modifier: Modifier = Modifier,
//) {
//    BottomNavigation(
//        modifier = modifier
//    ) {
//        val navBackStackEntry by navController.currentBackStackEntryAsState()
//        val currentRoute = navBackStackEntry?.destination?.route
//
//        val navigationItems = listOf(
//            NavigationItem(
//                title = stringResource(R.string.title_unit),
//                icon = Icons.Default.Bed,
//                screen = Screen.Unit
//            ),
//            NavigationItem(
//                title = stringResource(R.string.title_tenant),
//                icon = Icons.Default.People,
//                screen = Screen.Tenant,
//            ),
//            NavigationItem(
//                title = stringResource(R.string.title_cash_flow),
//                icon = Icons.Default.Loop,
//                screen = Screen.CashFlow,
//            ),
//            NavigationItem(
//                title = stringResource(R.string.title_other),
//                icon = Icons.Default.Menu,
//                screen = Screen.Other,
//            ),
//        )
//
//        BottomNavigation {
//            navigationItems.map { item ->
//                BottomNavigationItem(
//                    icon = {
//                        Icon(
//                            imageVector = item.icon,
//                            contentDescription = item.title
//                        )
//                    },
//                    label = {
//                        Text(
//                            item.title,
//                            color = if (currentRoute == item.screen.route) FontWhite else GreyLight2,
//                            maxLines = 1
//                        )
//                    },
//                    selected = currentRoute == item.screen.route,
//                    onClick = {
//                        navController.navigate(item.screen.route) {
//                            popUpTo(navController.graph.findStartDestination().id) {
//                                saveState = true
//                            }
//                            restoreState = true
//                            launchSingleTop = true
//                        }
//                    },
//                    modifier = Modifier.background(GreenDark)
//                )
//            }
//        }
//
//    }
//}
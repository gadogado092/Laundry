package amat.laundry.ui.screen.main

import amat.laundry.R
import amat.laundry.di.Injection
import amat.laundry.sendWhatsApp
import amat.laundry.ui.navigation.NavigationItem
import amat.laundry.ui.navigation.Screen
import amat.laundry.ui.screen.cashflow.CashFlowActivity
import amat.laundry.ui.screen.cashflowcategory.CashFlowCategoryActivity
import amat.laundry.ui.screen.category.CategoryActivity
import amat.laundry.ui.screen.home.HomeScreen
import amat.laundry.ui.screen.other.OtherScreen
import amat.laundry.ui.screen.printer.PrinterActivity
import amat.laundry.ui.screen.product.ProductActivity
import amat.laundry.ui.screen.transaction.TransactionScreen
import amat.laundry.ui.screen.user.UpdateUserActivity
import amat.laundry.ui.theme.FontWhite
import amat.laundry.ui.theme.GreenDark
import amat.laundry.ui.theme.GreyLight2
import amat.laundry.ui.theme.LaundryAppTheme
import android.content.ActivityNotFoundException
import android.content.Intent
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
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
            LaundryAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val context = LocalContext.current

    val myViewModel: MainViewModel =
        viewModel(
            factory = MainViewModelFactory(
                Injection.provideUserRepository(context)
            )
        )

    Scaffold(
        bottomBar = {
            when (currentRoute) {
                Screen.Home.route -> {
                    BottomBar(navController)
                }

                Screen.Transaction.route -> {
                    BottomBar(navController)
                }

                Screen.Other.route -> {
                    BottomBar(navController)
                }
            }
        },
        modifier = modifier
    )
    { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(context = context)
            }
            composable(Screen.Transaction.route) {
                TransactionScreen(context = context)
            }
            composable(Screen.Other.route) {
                val urlTutorial = stringResource(R.string.url_tutorial_app)
                val numberCs = stringResource(R.string.number_cs)
                val messageCs =
                    stringResource(R.string.message_cs, stringResource(R.string.app_name))
                OtherScreen(
                    context = context,
                    onClickCsExtend = {
                        sendWhatsApp(
                            context,
                            numberCs,
                            it,
                            myViewModel.typeWa.value
                        )
                    },
                    navigateToProduct = {
                        val intent = Intent(context, ProductActivity::class.java)
                        context.startActivity(intent)
                    },
                    navigateToCategory = {
                        val intent = Intent(context, CategoryActivity::class.java)
                        context.startActivity(intent)
                    },
                    navigateToPrinter = {
                        val intent = Intent(context, PrinterActivity::class.java)
                        context.startActivity(intent)
                    },
                    navigateToCashFlow = {
                        val intent = Intent(context, CashFlowActivity::class.java)
                        context.startActivity(intent)
                    },
                    navigateToCashFlowCategory = {
                        val intent = Intent(context, CashFlowCategoryActivity::class.java)
                        context.startActivity(intent)
                    },
                    navigateToProfile = {
                        val intent = Intent(context, UpdateUserActivity::class.java)
                        context.startActivity(intent)
                    },
                    onClickTutorial = {
                        try {
                            val webIntent =
                                Intent(
                                    Intent.ACTION_VIEW,
                                    urlTutorial.toUri()
                                )
                            context.startActivity(webIntent)
                        } catch (ex: ActivityNotFoundException) {
                            Toast.makeText(
                                context,
                                "Tidak Bisa Akses Tutorial",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    },
                    onClickCostumerService = {
                        sendWhatsApp(
                            context,
                            numberCs,
                            messageCs,
                            typeWa = it,
                        )
                    },
                )
            }
        }
    }
}

@Composable
fun BottomBar(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    BottomNavigation(
        modifier = modifier
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        val navigationItems = listOf(
            NavigationItem(
                title = stringResource(R.string.title_home),
                icon = Icons.Default.Home,
                screen = Screen.Home
            ),
            NavigationItem(
                title = stringResource(R.string.title_transaction),
                icon = Icons.Default.LocalLaundryService,
                screen = Screen.Transaction,
            ),
            NavigationItem(
                title = stringResource(R.string.title_other),
                icon = Icons.Default.Menu,
                screen = Screen.Other,
            ),
        )

        BottomNavigation {
            navigationItems.map { item ->
                BottomNavigationItem(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title
                        )
                    },
                    label = {
                        Text(
                            item.title,
                            color = if (currentRoute == item.screen.route) FontWhite else GreyLight2,
                            maxLines = 1
                        )
                    },
                    selected = currentRoute == item.screen.route,
                    onClick = {
                        navController.navigate(item.screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            restoreState = true
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.background(GreenDark)
                )
            }
        }

    }
}
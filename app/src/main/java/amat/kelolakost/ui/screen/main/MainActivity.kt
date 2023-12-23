package amat.kelolakost.ui.screen.main

import amat.kelolakost.R
import amat.kelolakost.ui.navigation.NavigationItem
import amat.kelolakost.ui.navigation.Screen
import amat.kelolakost.ui.screen.cash_flow.CashFlowScreen
import amat.kelolakost.ui.screen.other.OtherScreen
import amat.kelolakost.ui.screen.tenant.TenantScreen
import amat.kelolakost.ui.screen.unit.UnitScreen
import amat.kelolakost.ui.theme.FontWhite
import amat.kelolakost.ui.theme.GreenDark
import amat.kelolakost.ui.theme.GreyLight2
import amat.kelolakost.ui.theme.KelolaKostTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
                    MainScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            when (currentRoute) {
                Screen.Unit.route -> {
                    BottomBar(navController)
                }

                Screen.Tenant.route -> {
                    BottomBar(navController)
                }

                Screen.CashFlow.route -> {
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
            startDestination = Screen.Unit.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Unit.route) {
                UnitScreen(context = context)
            }
            composable(Screen.Tenant.route) {
                TenantScreen(context = context)
            }
            composable(Screen.CashFlow.route) {
                CashFlowScreen(context = context)
            }
            composable(Screen.Other.route) {
                OtherScreen(context = context)
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
                title = stringResource(R.string.title_unit),
                icon = Icons.Default.Home,
                screen = Screen.Unit
            ),
            NavigationItem(
                title = stringResource(R.string.title_tenant),
                icon = Icons.Default.CardMembership,
                screen = Screen.Tenant,
            ),
            NavigationItem(
                title = stringResource(R.string.title_cash_flow),
                icon = Icons.Default.Loop,
                screen = Screen.CashFlow,
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
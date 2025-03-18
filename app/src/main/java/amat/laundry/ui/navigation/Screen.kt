package amat.laundry.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Transaction : Screen("transaction")
    object Other : Screen("other")
}

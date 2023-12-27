package amat.kelolakost.ui.navigation

sealed class Screen(val route: String){
    object Unit: Screen("unit")
    object Tenant: Screen("tenant")
    object CashFlow: Screen("cash_flow")
    object Other: Screen("other")
    object Kost: Screen("kost")
    object UnitType: Screen("unit_type")
}

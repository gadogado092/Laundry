package amat.kelolakost.ui.screen.tenant

data class TenantUi(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val numberPhone: String = "",
    val gender: Boolean = false,
    val address: String = "",
    val note: String = "",
    val limitCheckOut: String = "",
    val unitId: String = "0",
    val guaranteeCost: Int = 0,
    val additionalCost: Int = 0,
    val noteAdditionalCost: String = "",
    val createAt: String = "",
    val isDelete: Boolean = false
)

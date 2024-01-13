package amat.kelolakost.ui.screen.check_out

data class CheckOutUi(
    val unitId: String = "",
    val unitName: String = "",
    val noteMaintenance: String = "",
    val statusAfterCheckOut: String = "Siap Digunakan",

    val unitTypeName: String = "",

    val tenantId: String = "",
    val tenantName: String = "",

    val isCash: Boolean = true,

    val kostId: String = "",
    val kostName: String = "",

    val debtTenant: Int = 0,
    val priceGuarantee: Int = 0,
    val limitCheckOut: String = ""
)

package amat.kelolakost.ui.screen.move

data class MoveUi(
    val tenantId: String = "",
    val tenantName: String = "",
    val tenantNumberPhone: String = "",
    val kostId: String = "",
    val kostName: String = "",
    val unitId: String = "",
    val unitName: String = "",
    val unitTypeName: String = "",

    val currentDebtTenant: Int = 0,

    val limitCheckOut: String = "",
    val moveDate: String = "",

    val statusAfterCheckOut: String = "Siap Digunakan",
    val noteMaintenance: String = "",

    val unitIdMove: String = "",
    val unitNameMove: String = "",
    val unitTypeNameMove: String = "",

    val moveType: String = "Gratis",
    val nominal: String = "",

    val isFullPayment: Boolean = true,
    val isCash: Boolean = true,
    val downPayment: String = "",
    val debtTenantMove: String = "",
    val totalDebtTenant: String = "",

    val totalPayment: String = "0",
)

package amat.kelolakost.ui.screen.cash_flow

data class AddCashFlowUi(
    val tenantId: String = "",
    val tenantName: String = "",
    val kostId: String = "",
    val kostName: String = "",
    val unitId: String = "",
    val unitName: String = "",
    val unitTypeName: String = "",

    val nominal: String = "",
    val note: String = "",

    val isCashOut: Boolean = true,

    val isCash: Boolean = true,

    val createAt: String = ""
)

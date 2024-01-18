package amat.kelolakost.ui.screen.credit_tenant

data class PaymentCreditTenantUi(
    val isFullPayment: Boolean = true,
    val isCash: Boolean = true,

    val downPayment: String = "0",
    val debtTenant: String = "0",

    val totalPayment: String = "0",

    val createAt: String = "",

    val tenantId: String = "0",
    val unitId: String = "0",
    val kostId: String = "0",
    val creditTenantId: String = "",
    val note: String = "",
    val remainingDebt: Int = 0
)

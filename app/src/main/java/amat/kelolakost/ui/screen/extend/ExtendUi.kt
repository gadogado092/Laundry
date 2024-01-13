package amat.kelolakost.ui.screen.extend

data class ExtendUi(
    val tenantId: String = "",
    val tenantName: String = "",
    val kostId: String = "",
    val kostName: String = "",
    val unitId: String = "",
    val unitName: String = "",
    val unitTypeName: String = "",

    val currentDebtTenant: Int = 0,
    val limitCheckOut: String = "",

    val price: Int = 0,
    val qty: Int = 1,
    val duration: String = "",

    val additionalCost: String = "0",
    val noteAdditionalCost: String = "",
    val discount: String = "0",

    val checkOutDateNew: String = "",

    val totalPrice: String = "0",
    val isFullPayment: Boolean = true,
    val isCash: Boolean = true,

    val downPayment: String = "0",
    val debtTenantExtend: String = "0",
    val totalDebtTenant: String = "0",

    val totalPayment: String = "0",

    val createAt: String = ""

)

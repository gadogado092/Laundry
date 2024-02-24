package amat.kelolakost.ui.screen.check_in

data class CheckInUi(
    val tenantId: String = "",
    val tenantName: String = "",
    val tenantNumberPhone: String = "",
    val kostId: String = "",
    val kostName: String = "",
    val unitId: String = "",
    val unitName: String = "",
    val unitTypeName: String = "",

    val price: Int = 0,
    val qty: Int = 1,
    val duration: String = "",

    val noteAdditionalCost: String = "",
    val additionalCost: String = "0",
    val discount: String = "0",
    val guaranteeCost: Int = 0,
    val checkInDate: String = "",
    val checkOutDate: String = "",
    val totalPrice: String = "0",

    val isFullPayment: Boolean = true,
    val isCash: Boolean = true,

    val downPayment: String = "0",
    val debtTenant: String = "0",

    val totalPayment: String = "0",

    val createAt: String = ""
)


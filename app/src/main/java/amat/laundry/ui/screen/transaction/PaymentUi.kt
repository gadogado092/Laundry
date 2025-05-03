package amat.laundry.ui.screen.transaction

data class PaymentUi(
    val customerId: String = "",
    val customerName: String = "",
    val note: String = "",
    val paymentDate: String = "",
    val totalPrice: String = "",
    val userId: String = "0",
    val userName: String = "",
    val isFullPayment: Boolean = true
)
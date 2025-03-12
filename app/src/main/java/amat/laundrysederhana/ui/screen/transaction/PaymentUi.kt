package amat.laundrysederhana.ui.screen.transaction

data class PaymentUi(
    val customerName: String = "",
    val note: String = "",
    val paymentDate: String = "",
    val totalPrice: String = "",
    val isFullPayment: Boolean = true
)
package amat.laundry.ui.screen.transaction

data class PaymentUi(

    val isOldCustomer: Boolean = true,
    val customerId: String = "",
    val customerName: String = "Pilih Customer/Pelanggan",
    val customerNumberPhone: String = "",
    val customerNote: String = "",

    val note: String = "",
    val paymentDate: String = "",
    val totalPrice: String = "",

    val cashierId: String = "0",
    val cashierName: String = "",

    val isFullPayment: Boolean = true
)
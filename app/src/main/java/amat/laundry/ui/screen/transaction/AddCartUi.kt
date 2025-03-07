package amat.laundry.ui.screen.transaction

data class AddCartUi(
    val productId: String = "",
    val qty: String = "",
    val note: String = "",
    val price: String = "",
    val totalPrice: String = "",
)
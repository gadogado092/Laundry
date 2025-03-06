package amat.laundry.data

data class ProductCart(
    val productId: String,
    val productName: String,
    val productPrice: Int,
    val categoryName: String,
    val unit: String,
    val qty: Float
)
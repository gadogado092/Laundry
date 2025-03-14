package amat.laundrysederhana.data

data class ProductCategory(
    val productId: String,
    val productName: String,
    val productPrice: Int,
    val categoryId: String,
    val categoryName: String,
    val unit: String
)

data class CartCategory(
    val productId: String,
    val qty: Float,
    val note: String,
    val categoryName: String
)

data class ProductCart(
    val productId: String,
    val productName: String,
    val productPrice: Int,
    val productTotalPrice: String,
    val categoryId: String,
    val categoryName: String,
    val unit: String,
    val qty: Float,
    val note: String
)
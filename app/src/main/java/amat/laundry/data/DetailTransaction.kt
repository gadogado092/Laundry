package amat.laundry.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DetailTransaction(
    @PrimaryKey
    val id: String,
    val transactionId: String,
    val productId: String,
    val productName: String,
    val note: String,
    val unit: String,
    val price: Int,
    val totalPrice: String,
    val qty: Float,
    val createAt: String,
    val isDelete: Boolean
)

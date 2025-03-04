package amat.laundry.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DetailTransaction(
    @PrimaryKey
    val id: String,
    val productId: String,
    val productName: String,
    val price: String,
    val qty: Float,
    val isDelete: Boolean
)

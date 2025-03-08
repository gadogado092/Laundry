package amat.laundry.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Cart(
    @PrimaryKey
    val productId: String,
    val totalPrice: String,
    val qty: Float,
    val note: String
)

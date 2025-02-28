package amat.laundry.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Transaction(
    @PrimaryKey
    val id: String,
    val customerName: String,
    val laundryStatusId: Int,
    val isFullPayment: Boolean,
    val totalPrice: String
)

package amat.laundry.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TransactionLaundry(
    @PrimaryKey
    val id: String,
    val invoiceCode: String,
    val customerId: String,
    val customerName: String,
    val laundryStatusId: Int,
    val isFullPayment: Boolean,
    val totalPrice: String,
    val note: String,
    val cashierId: String,
    val cashierName: String,
    val createAt: String,
    val isDelete: Boolean
)

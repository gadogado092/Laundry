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
    val paymentDate: String, //DATE TIME
    val estimationReadyToPickup: String, //DATE
    val finishAt:String, //DATE
    val totalPrice: String,
    val note: String,
    val cashierId: String,
    val cashierName: String,
    val createAt: String, //DATE
    val isDelete: Boolean
)

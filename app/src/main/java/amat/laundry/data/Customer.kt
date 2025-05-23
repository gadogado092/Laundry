package amat.laundry.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Customer(
    @PrimaryKey
    val id: String,
    val name: String,
    val phoneNumber: String,
    val note: String,
    val isDelete: Boolean
)

package amat.laundry.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Cashier(
    @PrimaryKey
    val id: String,
    val name: String,
    val note: String,
    val isLastUsed: Boolean,
    val isDelete: Boolean
)

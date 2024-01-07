package amat.kelolakost.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CustomerCreditDebit(
    @PrimaryKey
    val id: String,
    val name: String,
    val numberPhone: String,
    val note: String,
    val email: String,
    val createAt: String,
    val isDelete: Boolean
)
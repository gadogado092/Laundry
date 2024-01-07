package amat.kelolakost.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Debit(
    @PrimaryKey
    val id: String,
    val note: String,
    //type 0=belum 1=lunas
    val status: Int,
    val remaining: Int,
    val customerCreditDebitId: String,
    val createAt: String,
    val isDelete: Boolean
)
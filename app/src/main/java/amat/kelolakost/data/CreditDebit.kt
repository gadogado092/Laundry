package amat.kelolakost.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CreditDebit(
    @PrimaryKey
    val id: String,
    val note: String,
    //status 0=hutang 1=piutang
    val status: Int,
    val remaining: Int,
    val customerCreditDebitId: String,
    val dueDate: String,
    val createAt: String,
    val isDelete: Boolean
)
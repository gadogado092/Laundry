package amat.laundry.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CashFlow(
    @PrimaryKey
    val id: String,
    val note: String,
    val nominal: String,
    val qty: Float,
    val cashFlowCategoryId: String,
    //type 0=in 1=out
    val type: Int,
    val createAt: String,// DATE TIME
    val isDelete: Boolean
)

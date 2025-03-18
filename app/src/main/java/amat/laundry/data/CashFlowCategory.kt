package amat.laundry.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CashFlowCategory(
    @PrimaryKey
    val id: String,
    val name: String,
    //type 0=in 1=out
    val type: Int,
    val unit: String,
    val isDelete: Boolean
)

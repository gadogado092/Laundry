package amat.kelolakost.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CashFlow(
    @PrimaryKey
    val id: String,
    val note: String,
    val nominal: String,
    //type 0=transfer 1=cash
    val typePayment: Int,
    //type 0=in 1=out
    val type: Int,
    val creditTenantId: String,
    val creditDebitId: String,
    val unitId: String,
    val tenantId: String,
    val kostId: String,
    val createAt: String,
    val isDelete: Boolean
)
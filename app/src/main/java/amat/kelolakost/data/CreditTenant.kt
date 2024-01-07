package amat.kelolakost.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CreditTenant(
    @PrimaryKey
    val id: String,
    val note: String,
    val tenantId: String,
    //type 0=belum 1=lunas
    val status: Int,
    val remainingDebt: Int,
    val kostId: String,
    val unitId: String,
    val createAt: String,
    val isDelete: Boolean
)
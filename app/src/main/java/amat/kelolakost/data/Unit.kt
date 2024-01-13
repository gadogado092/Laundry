package amat.kelolakost.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Unit(
    @PrimaryKey
    val id: String,
    val name: String,
    val note: String,
    val noteMaintenance: String,
    val unitTypeId: String,
    val unitStatusId: Int,
    val tenantId: String,
    val kostId: String,
    val bookingId: String,
    val isDelete: Boolean
)

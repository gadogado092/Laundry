package amat.kelolakost.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Booking(
    @PrimaryKey
    val id: String,
    val name: String,
    val numberPhone: String,
    val note: String,
    val nominal: String,
    val planCheckIn: String,
    val unitId: String,
    val kostId: String,
    val createAt: String,
    val isDelete: Boolean
)
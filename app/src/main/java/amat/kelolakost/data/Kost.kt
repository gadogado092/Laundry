package amat.kelolakost.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Kost(
    @PrimaryKey
    val id: String,
    val name: String,
    val address: String,
    val note: String,
    val createAt: String
)

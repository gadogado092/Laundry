package amat.kelolakost.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UnitStatus(
    @PrimaryKey
    val id: Int,
    val name: String
)

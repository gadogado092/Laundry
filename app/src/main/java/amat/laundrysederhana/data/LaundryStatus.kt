package amat.laundrysederhana.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LaundryStatus(
    @PrimaryKey
    val id: Int,
    val name: String
)

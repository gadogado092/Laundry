package amat.laundrysederhana.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Customer(
    @PrimaryKey
    val id: String,
    val name: String,
    val numberPhone: String,
    val isDelete: Boolean
)

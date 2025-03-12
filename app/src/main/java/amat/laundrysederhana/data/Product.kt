package amat.laundrysederhana.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Product(
    @PrimaryKey
    val id: String,
    val name: String,
    val price: Int,
    val categoryId: String,
    val isDelete: Boolean
)

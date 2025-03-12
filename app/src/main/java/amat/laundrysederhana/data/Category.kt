package amat.laundrysederhana.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Category(
    @PrimaryKey
    val id: String,
    val name: String,
    val unit: String,
    val isDelete: Boolean
)

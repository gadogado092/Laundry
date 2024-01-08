package amat.kelolakost.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey
    val id: String,
    val name: String,
    val numberPhone: String,
    val email: String,
    val typeWa: String,
    //info penagihan
    val bankName:String,
    val accountNumber:String,
    val accountOwnerName:String,
    val note:String,
    //limit is date encode by Base64
    val limit: String,
    val cost: Int,
    val key: String,
    val createAt: String
)

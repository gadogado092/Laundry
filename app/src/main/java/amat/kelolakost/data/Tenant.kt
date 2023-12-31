package amat.kelolakost.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Tenant(
    @PrimaryKey
    val id: String,
    val name: String,
    val numberPhone: String,
    val email: String,
    //0=M 1=W
    val gender: Boolean,
    val address: String,
    val note: String,
    val limitCheckOut: String,
    val additionalCost: Int,
    val noteAdditionalCost: String,
    val guaranteeCost: Int,
    val unitId: String,
    val isDelete: Boolean
)

package amat.kelolakost.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UnitType(
    @PrimaryKey
    val id: String,
    val name: String,
    val note: String,
    val priceDay: Int,
    val priceWeek: Int,
    val priceMonth: Int,
    val priceThreeMonth: Int,
    val priceSixMonth: Int,
    val priceYear: Int,
    val priceGuarantee: Int,
    val isDelete: Boolean
)

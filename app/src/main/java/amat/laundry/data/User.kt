package amat.laundry.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey
    val id: String,
    val businessName: String,
    val numberPhone: String,
    val address: String,
    val typeWa: String,
    //info printer
    val printerName: String,
    val printerAddress: String,
    val sizeCharacterPrinter: Int,
    val sizeLinePrinter: Int,
    val headerNote: String,
    val footerNote: String,

    val limit: String,
    val cost: Int,
    val key: String,
    val createAt: String
)

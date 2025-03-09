package amat.laundry.data

import amat.laundry.data.entity.InvoiceCode
import amat.laundry.data.entity.Sum
import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query(
        "SELECT MAX(invoiceCode) AS lastCode " +
                "FROM `Transaction` " +
                "WHERE invoiceCode LIKE:dateInvoice"
    )
    suspend fun getLastNumberInvoice(dateInvoice: String): InvoiceCode
}

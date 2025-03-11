package amat.laundry.data

import amat.laundry.data.entity.InvoiceCode
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface TransactionLaundryDao {
    @Query(
        "SELECT MAX(invoiceCode) AS lastCode " +
                "FROM TransactionLaundry " +
                "WHERE invoiceCode LIKE '%' || :dateInvoice || '%'"
    )
    suspend fun getLastNumberInvoice(dateInvoice: String): InvoiceCode

    @Query(
        "SELECT * FROM TransactionLaundry " +
                "WHERE id=:transactionId"
    )
    suspend fun getTransaction(transactionId: String): TransactionLaundry

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetailTransaction(detailTransaction: List<DetailTransaction>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionLaundry)

    @Query("DELETE FROM Cart")
    suspend fun deleteAllCart()

    @Transaction
    suspend fun insertNewTransaction(
        transaction: TransactionLaundry,
        detailTransaction: List<DetailTransaction>
    ) {
        insertTransaction(transaction)
        insertDetailTransaction(detailTransaction)

        deleteAllCart()
    }
}

package amat.laundrysederhana.data

import amat.laundrysederhana.data.entity.InvoiceCode
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

    @Query(
        "SELECT * " +
                "FROM TransactionLaundry WHERE isDelete=0 " +
                "AND TransactionLaundry.createAt >= :startDate " +
                "AND TransactionLaundry.createAt <= :endDate ORDER BY TransactionLaundry.createAt DESC"
    )
    suspend fun getTransaction(startDate: String, endDate: String): List<TransactionLaundry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetailTransaction(detailTransaction: List<DetailTransaction>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionLaundry)

    @Query(
        "UPDATE TransactionLaundry " +
                "SET isFullPayment=:isFullPayment " +
                "WHERE id=:transactionId"
    )
    suspend fun updateTransactionStatusPayment(transactionId: String, isFullPayment: Boolean)

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

    //DELETE AREA
    @Query(
        "UPDATE TransactionLaundry " +
                "SET isDelete=1 " +
                "WHERE id=:transactionId"
    )
    suspend fun deleteTransaction(transactionId: String)

    @Query(
        "UPDATE DetailTransaction " +
                "SET isDelete=1 " +
                "WHERE transactionId=:transactionId"
    )
    suspend fun deleteDetailTransaction(transactionId: String)

    @Transaction
    suspend fun deleteTransactionAndDetailTransaction(transactionId: String) {
        deleteTransaction(transactionId)
        deleteDetailTransaction(transactionId)
    }
}

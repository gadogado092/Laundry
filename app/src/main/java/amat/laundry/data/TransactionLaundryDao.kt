package amat.laundry.data

import amat.laundry.data.entity.InvoiceCode
import amat.laundry.generateDateTimeNow
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
        "SELECT TransactionLaundry.id AS id, TransactionLaundry.invoiceCode AS invoiceCode, " +
                "TransactionLaundry.customerName AS customerName, TransactionLaundry.laundryStatusId AS laundryStatusId, TransactionLaundry.isFullPayment AS isFullPayment, " +
                "TransactionLaundry.paymentDate AS paymentDate, TransactionLaundry.estimationReadyToPickup AS estimationReadyToPickup, TransactionLaundry.finishAt AS finishAt, TransactionLaundry.totalPrice AS totalPrice, TransactionLaundry.createAt AS createAt, " +
                "Customer.phoneNumber AS customerNumberPhone " +
                "FROM TransactionLaundry " +
                "LEFT JOIN (SELECT Customer.id, Customer.phoneNumber FROM Customer) AS Customer ON Customer.id = TransactionLaundry.customerId " +
                "WHERE TransactionLaundry.isDelete=0 " +
                "AND TransactionLaundry.createAt >= :startDate " +
                "AND TransactionLaundry.createAt <= :endDate ORDER BY TransactionLaundry.createAt DESC"
    )
    suspend fun getTransaction(startDate: String, endDate: String): List<TransactionCustomer>

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

    @Query(
        "UPDATE TransactionLaundry " +
                "SET laundryStatusId=:statusId " +
                "WHERE id=:transactionId"
    )
    suspend fun updateStatusLaundry(transactionId: String, statusId: Int)

    @Query(
        "UPDATE TransactionLaundry " +
                "SET paymentDate=:paymentDate " +
                "WHERE id=:transactionId"
    )
    suspend fun updatePaymentDateLaundry(transactionId: String, paymentDate: String)

    @Query(
        "UPDATE TransactionLaundry " +
                "SET finishAt=:finishAt " +
                "WHERE id=:transactionId"
    )
    suspend fun updateDateFinishLaundry(transactionId: String, finishAt: String)

    @Query("DELETE FROM Cart")
    suspend fun deleteAllCart()

    @Transaction
    suspend fun transactionUpdateStatusLaundry(
        transactionId: String,
        statusId: Int,
        isFullPayment: Boolean
    ) {
        if (statusId == 1 || statusId == 2) {
            updateStatusLaundry(transactionId, statusId)
        } else if (statusId == 3) {

            val dateTimeNow = generateDateTimeNow()
            updateStatusLaundry(transactionId, statusId)
            updateDateFinishLaundry(transactionId, dateTimeNow)

            if (isFullPayment) {
                updateTransactionStatusPayment(transactionId, true)
                updatePaymentDateLaundry(transactionId, dateTimeNow)
            }

        }

    }

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

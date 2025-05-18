package amat.laundry.data.repository

import amat.laundry.data.DetailTransaction
import amat.laundry.data.TransactionCustomer
import amat.laundry.data.TransactionLaundry
import amat.laundry.data.TransactionLaundryDao
import amat.laundry.data.entity.InvoiceCode
import amat.laundry.generateDateTimeNow

class TransactionRepository(private val transactionDao: TransactionLaundryDao) {

    suspend fun getLastNumberInvoice(dateInvoice: String): InvoiceCode {
        return transactionDao.getLastNumberInvoice(dateInvoice)
    }

    suspend fun getTransaction(transactionId: String): TransactionLaundry {
        return transactionDao.getTransaction(transactionId)
    }

    suspend fun getTransaction(startDate: String, endDate: String): List<TransactionCustomer> {
        return transactionDao.getTransaction(startDate, endDate)
    }

    suspend fun searchTransaction(invoiceCode: String): List<TransactionCustomer> {
        return transactionDao.searchTransaction(invoiceCode)
    }

    suspend fun insertNewTransaction(
        transaction: TransactionLaundry,
        detailTransaction: List<DetailTransaction>
    ) {
        transactionDao.insertNewTransaction(transaction, detailTransaction)
    }

    suspend fun deleteTransactionAndDetailTransaction(transactionId: String) {
        transactionDao.deleteTransactionAndDetailTransaction(transactionId)
    }

    suspend fun updateTransactionStatusPayment(transactionId: String, isFullPayment: Boolean) {
        transactionDao.updateTransactionStatusPayment(transactionId, isFullPayment)
        if (isFullPayment) {
            transactionDao.updatePaymentDateLaundry(transactionId, generateDateTimeNow())
        }
    }

    suspend fun updateStatusLaundry(transactionId: String, statusId: Int, isFullPayment: Boolean) {
        transactionDao.transactionUpdateStatusLaundry(transactionId, statusId, isFullPayment)
    }

    suspend fun getTotalReadyToPickUp(): String {
        val data = transactionDao.getTotalReadyToPickUp()
        var total = "0"
        if (data.total != null) {
            total = data.total!!
        }

        return total
    }

    suspend fun getTotalDeadline(dateTomorrow: String): String {
        val data = transactionDao.getTotalDeadline(dateTomorrow)
        var total = "0"
        if (data.total != null) {
            total = data.total!!
        }

        return total
    }

    suspend fun getTotalLate(dateToday: String): String {
        val data = transactionDao.getTotalLate(dateToday)
        var total = "0"
        if (data.total != null) {
            total = data.total!!
        }

        return total
    }


    companion object {
        @Volatile
        private var instance: TransactionRepository? = null

        fun getInstance(dao: TransactionLaundryDao): TransactionRepository =
            instance ?: synchronized(this) {
                TransactionRepository(dao).apply {
                    instance = this
                }
            }
    }

}
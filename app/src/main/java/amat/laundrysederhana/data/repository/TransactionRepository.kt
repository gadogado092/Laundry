package amat.laundrysederhana.data.repository

import amat.laundrysederhana.data.DetailTransaction
import amat.laundrysederhana.data.TransactionLaundry
import amat.laundrysederhana.data.TransactionLaundryDao
import amat.laundrysederhana.data.entity.InvoiceCode

class TransactionRepository(private val transactionDao: TransactionLaundryDao) {

    suspend fun getLastNumberInvoice(dateInvoice: String): InvoiceCode {
        return transactionDao.getLastNumberInvoice(dateInvoice)
    }

    suspend fun getTransaction(transactionId: String): TransactionLaundry {
        return transactionDao.getTransaction(transactionId)
    }

    suspend fun getTransaction(startDate: String, endDate: String): List<TransactionLaundry> {
        return transactionDao.getTransaction(startDate, endDate)
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
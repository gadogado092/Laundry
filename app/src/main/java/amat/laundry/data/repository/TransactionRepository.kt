package amat.laundry.data.repository

import amat.laundry.data.DetailTransaction
import amat.laundry.data.TransactionLaundry
import amat.laundry.data.TransactionLaundryDao
import amat.laundry.data.entity.InvoiceCode

class TransactionRepository(private val transactionDao: TransactionLaundryDao) {

    suspend fun getLastNumberInvoice(dateInvoice: String): InvoiceCode {
        return transactionDao.getLastNumberInvoice(dateInvoice)
    }

    suspend fun insertNewTransaction(
        transaction: TransactionLaundry,
        detailTransaction: List<DetailTransaction>
    ) {
        transactionDao.insertNewTransaction(transaction, detailTransaction)
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
package amat.laundry.data.repository

import amat.laundry.data.Category
import amat.laundry.data.CategoryDao
import amat.laundry.data.TransactionDao
import amat.laundry.data.entity.InvoiceCode
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {

    suspend fun getLastNumberInvoice(dateInvoice: String): InvoiceCode {
        return transactionDao.getLastNumberInvoice(dateInvoice)
    }


    companion object {
        @Volatile
        private var instance: TransactionRepository? = null

        fun getInstance(dao: TransactionDao): TransactionRepository =
            instance ?: synchronized(this) {
                TransactionRepository(dao).apply {
                    instance = this
                }
            }
    }

}
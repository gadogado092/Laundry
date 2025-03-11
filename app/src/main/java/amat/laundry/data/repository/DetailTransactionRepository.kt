package amat.laundry.data.repository

import amat.laundry.data.DetailTransaction
import amat.laundry.data.DetailTransactionDao

class DetailTransactionRepository(private val detailTransactionRepository: DetailTransactionDao) {

    suspend fun getDetailTransactionList(): List<DetailTransaction> {
        return detailTransactionRepository.getDetailTransactionList()
    }


    companion object {
        @Volatile
        private var instance: DetailTransactionRepository? = null

        fun getInstance(dao: DetailTransactionDao): DetailTransactionRepository =
            instance ?: synchronized(this) {
                DetailTransactionRepository(dao).apply {
                    instance = this
                }
            }
    }

}
package amat.laundrysederhana.data.repository

import amat.laundrysederhana.data.DetailTransaction
import amat.laundrysederhana.data.DetailTransactionDao

class DetailTransactionRepository(private val detailTransactionRepository: DetailTransactionDao) {

    suspend fun getDetailTransactionList(transactionId: String): List<DetailTransaction> {
        return detailTransactionRepository.getDetailTransactionList(transactionId)
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
package amat.laundrysederhana.data.repository

import amat.laundrysederhana.data.DetailTransaction
import amat.laundrysederhana.data.DetailTransactionDao
import amat.laundrysederhana.data.entity.Sum

class DetailTransactionRepository(private val detailTransactionRepository: DetailTransactionDao) {

    suspend fun getDetailTransactionList(transactionId: String): List<DetailTransaction> {
        return detailTransactionRepository.getDetailTransactionList(transactionId)
    }

    suspend fun getTotalPriceDetailTransaction(
        categoryId: String,
        startDate: String,
        endDate: String
    ): Sum {
        return detailTransactionRepository.getTotalPriceDetailTransaction(
            categoryId,
            startDate,
            endDate
        )
    }

    suspend fun getTotalQtyDetailTransaction(
        categoryId: String,
        startDate: String,
        endDate: String
    ): Sum {
        return detailTransactionRepository.getTotalQtyDetailTransaction(
            categoryId,
            startDate,
            endDate
        )
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
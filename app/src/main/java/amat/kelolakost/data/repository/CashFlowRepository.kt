package amat.kelolakost.data.repository

import amat.kelolakost.data.CashFlow
import amat.kelolakost.data.CashFlowDao
import kotlinx.coroutines.flow.Flow

class CashFlowRepository(private val cashFlowDao: CashFlowDao) {
    fun getAllCashFlow(): Flow<List<CashFlow>> {
        return cashFlowDao.getAllCashFlow()
    }

    suspend fun insert(booking: CashFlow) {
        cashFlowDao.insert(booking)
    }

    suspend fun update(booking: CashFlow) {
        cashFlowDao.update(booking)
    }

    companion object {
        @Volatile
        private var instance: CashFlowRepository? = null

        fun getInstance(dao: CashFlowDao): CashFlowRepository =
            instance ?: synchronized(this) {
                CashFlowRepository(dao).apply {
                    instance = this
                }
            }
    }
}
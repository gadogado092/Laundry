package amat.kelolakost.data.repository

import amat.kelolakost.data.CashFlow
import amat.kelolakost.data.CashFlowDao
import amat.kelolakost.data.CreditTenant
import kotlinx.coroutines.flow.Flow

class CashFlowRepository(private val cashFlowDao: CashFlowDao) {
    fun getAllCashFlow(): Flow<List<CashFlow>> {
        return cashFlowDao.getAllCashFlow()
    }

    suspend fun insert(cashFlow: CashFlow) {
        cashFlowDao.insert(cashFlow)
    }

    suspend fun update(booking: CashFlow) {
        cashFlowDao.update(booking)
    }

    suspend fun insertCheckIn(
        cashFlow: CashFlow,
        creditTenant: CreditTenant,
        isFullPayment: Boolean,
        limitCheckOut: String,
        additionalCost: Int,
        noteAdditionalCost: String,
        guaranteeCost: Int
    ) {
        cashFlowDao.insertCheckIn(
            cashFlow = cashFlow,
            creditTenant = creditTenant,
            isFullPayment = isFullPayment,
            limitCheckOut = limitCheckOut,
            additionalCost = additionalCost,
            noteAdditionalCost = noteAdditionalCost,
            guaranteeCost = guaranteeCost
        )
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
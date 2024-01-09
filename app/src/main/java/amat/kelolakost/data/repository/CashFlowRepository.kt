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

    suspend fun prosesCheckIn(
        cashFlow: CashFlow,
        creditTenant: CreditTenant,
        isFullPayment: Boolean,
        limitCheckOut: String,
        additionalCost: Int,
        noteAdditionalCost: String,
        guaranteeCost: Int
    ) {
        cashFlowDao.prosesCheckIn(
            cashFlow = cashFlow,
            creditTenant = creditTenant,
            isFullPayment = isFullPayment,
            limitCheckOut = limitCheckOut,
            additionalCost = additionalCost,
            noteAdditionalCost = noteAdditionalCost,
            guaranteeCost = guaranteeCost
        )
    }

    suspend fun prosesCheckOut(
        cashFlow: CashFlow,
        priceGuarantee: Int,
        unitStatusId: Int,
        noteMaintenance: String
    ) {
        cashFlowDao.prosesCheckOut(
            cashFlow = cashFlow,
            priceGuarantee = priceGuarantee,
            unitStatusId = unitStatusId,
            noteMaintenance = noteMaintenance
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
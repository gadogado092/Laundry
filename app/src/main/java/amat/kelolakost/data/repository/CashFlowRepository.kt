package amat.kelolakost.data.repository

import amat.kelolakost.data.CashFlow
import amat.kelolakost.data.CashFlowDao
import amat.kelolakost.data.CreditTenant
import amat.kelolakost.data.entity.Sum
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

    suspend fun prosesExtend(
        cashFlow: CashFlow,
        creditTenant: CreditTenant,
        isFullPayment: Boolean,
        limitCheckOut: String,
        additionalCost: Int,
        noteAdditionalCost: String
    ) {
        cashFlowDao.prosesExtend(
            cashFlow = cashFlow,
            creditTenant = creditTenant,
            isFullPayment = isFullPayment,
            limitCheckOut = limitCheckOut,
            additionalCost = additionalCost,
            noteAdditionalCost = noteAdditionalCost
        )
    }

    suspend fun prosesMoveUnit(
        cashFlow: CashFlow,
        creditTenant: CreditTenant,
        unitIdOld: String,
        statusIdUnitOld: Int,
        noteMaintenanceUnitOld: String,
        moveType: String,
        isFullPayment: Boolean
    ) {
        cashFlowDao.prosesMoveUnit(
            cashFlow = cashFlow,
            creditTenant = creditTenant,
            unitIdOld = unitIdOld,
            statusIdUnitOld = statusIdUnitOld,
            noteMaintenanceUnitOld = noteMaintenanceUnitOld,
            moveType = moveType,
            isFullPayment = isFullPayment
        )
    }

    suspend fun prosesFinishMaintenance(cashFlow: CashFlow) {
        cashFlowDao.prosesFinishMaintenance(cashFlow)
    }

    //CASH FLOW HOME
    fun getBalanceFlow(): Flow<Sum> {
        return cashFlowDao.getBalanceFlow()
    }

    fun getTotalIncomeFlow(startDate: String, endDate: String): Flow <Sum> {
        return cashFlowDao.getTotalIncomeFlow(startDate, endDate)
    }

    fun getTotalOutcomeFlow(startDate: String, endDate: String): Flow <Sum> {
        return cashFlowDao.getTotalOutcomeFlow(startDate, endDate)
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
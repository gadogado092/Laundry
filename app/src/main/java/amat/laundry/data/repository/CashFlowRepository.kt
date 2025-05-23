package amat.laundry.data.repository

import amat.laundry.data.CashFlow
import amat.laundry.data.CashFlowAndCategory
import amat.laundry.data.CashFlowDao
import amat.laundry.data.entity.Sum

class CashFlowRepository(private val cashFlowDao: CashFlowDao) {

    suspend fun getBalanceNow(): String {
        val incomeData = cashFlowDao.getTotalIncome()
        val outcomeData = cashFlowDao.getTotalOutcome()

        var balance = "e"
        var income = "0"
        var outcome = "0"

        if (incomeData.total != null) {
            income = incomeData.total!!
        }
        if (outcomeData.total != null) {
            outcome = outcomeData.total!!
        }

        balance = (income.toBigInteger() - outcome.toBigInteger()).toString()

        return balance
    }

    suspend fun getTotalIncomeByDate(startDate: String, endDate: String): String {
        val data = cashFlowDao.getTotalIncomeByDate(startDate, endDate)
        var income = "0"
        if (data.total != null) {
            income = data.total!!
        }
        return income
    }

    suspend fun getTotalOutcomeByDate(startDate: String, endDate: String): String {
        val data = cashFlowDao.getTotalOutcomeByDate(startDate, endDate)
        var outcome = "0"
        if (data.total != null) {
            outcome = data.total!!
        }
        return outcome
    }

    suspend fun insert(cashFlow: CashFlow) {
        cashFlowDao.insert(cashFlow)
    }

    suspend fun update(cashFlow: CashFlow) {
        cashFlowDao.update(cashFlow)
    }

    suspend fun deleteCashFlow(id: String) {
        cashFlowDao.deleteCashFlow(id)
    }

    suspend fun getCashFlowList(startDate: String, endDate: String): List<CashFlowAndCategory> {
        return cashFlowDao.getCashFlowList(startDate, endDate)
    }

    suspend fun getCashFlow(cashFlowId: String): CashFlowAndCategory {
        return cashFlowDao.getCashFlow(cashFlowId)
    }

    suspend fun getTotalNominalCashFlow(
        cashFlowCategoryId: String,
        startDate: String,
        endDate: String
    ): Sum {
        return cashFlowDao.getTotalNominalCashFlow(cashFlowCategoryId, startDate, endDate)
    }

    suspend fun getTotalQtyCashFlow(
        cashFlowCategoryId: String,
        startDate: String,
        endDate: String
    ): Sum {
        return cashFlowDao.getTotalQtyCashFlow(cashFlowCategoryId, startDate, endDate)
    }

    companion object {
        @Volatile
        private var instance: CashFlowRepository? = null

        fun getInstance(cashFlowDao: CashFlowDao): CashFlowRepository =
            instance ?: synchronized(this) {
                CashFlowRepository(cashFlowDao).apply {
                    instance = this
                }
            }
    }
}
package amat.kelolakost.data.repository

import amat.kelolakost.data.CashFlow
import amat.kelolakost.data.CreditDebit
import amat.kelolakost.data.CreditDebitDao
import amat.kelolakost.data.CreditDebitHome

class CreditDebitRepository(private val creditDebitDao: CreditDebitDao) {
    suspend fun getAllCreditDebit(): List<CreditDebitHome> {
        return creditDebitDao.getAllCreditDebit()
    }

    suspend fun payCreditDebit(cashFlow: CashFlow, remaining: Int, dueDate: String) {
        creditDebitDao.payCreditDebit(cashFlow, remaining, dueDate)
    }

    suspend fun getDetailCreditDebit(creditDebitId:String): CreditDebitHome {
        return creditDebitDao.getDetailCreditDebit(creditDebitId)
    }

    suspend fun insert(creditDebit: CreditDebit) {
        creditDebitDao.insert(creditDebit)
    }

    suspend fun insertCreditDebit(creditDebit: CreditDebit, cashFlow: CashFlow) {
        creditDebitDao.insertCreditDebit(creditDebit, cashFlow)
    }

    suspend fun update(credit: CreditDebit) {
        creditDebitDao.update(credit)
    }

    suspend fun deleteCreditDebit(creditDebitId: String){
        creditDebitDao.deleteCreditDebit(creditDebitId)
    }

    companion object {
        @Volatile
        private var instance: CreditDebitRepository? = null

        fun getInstance(dao: CreditDebitDao): CreditDebitRepository =
            instance ?: synchronized(this) {
                CreditDebitRepository(dao).apply {
                    instance = this
                }
            }
    }
}
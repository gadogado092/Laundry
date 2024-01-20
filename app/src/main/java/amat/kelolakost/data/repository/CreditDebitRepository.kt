package amat.kelolakost.data.repository

import amat.kelolakost.data.CashFlow
import amat.kelolakost.data.CreditDebit
import amat.kelolakost.data.CreditDebitDao
import amat.kelolakost.data.CreditDebitHome

class CreditDebitRepository(private val creditDao: CreditDebitDao) {
    suspend fun getAllCreditDebit(): List<CreditDebitHome> {
        return creditDao.getAllCreditDebit()
    }

    suspend fun insert(creditDebit: CreditDebit) {
        creditDao.insert(creditDebit)
    }

    suspend fun insertCreditDebit(creditDebit: CreditDebit, cashFlow: CashFlow) {
        creditDao.insertCreditDebit(creditDebit, cashFlow)
    }

    suspend fun update(credit: CreditDebit) {
        creditDao.update(credit)
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
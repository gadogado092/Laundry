package amat.laundry.data.repository

import amat.laundry.data.Cashier
import amat.laundry.data.CashierDao

class CashierRepository(private val cashierDao: CashierDao) {

    suspend fun getCashier(): List<Cashier> {
        return cashierDao.getCashier()
    }

    suspend fun insert(cashier: Cashier) {
        cashierDao.insert(cashier)
    }

    suspend fun update(cashier: Cashier) {
        cashierDao.update(cashier)
    }

    suspend fun deleteCashier(id: String) {
        cashierDao.deleteCashier(id)
    }

    suspend fun getCashier(id: String): Cashier {
        return cashierDao.getCashier(id)
    }

    companion object {
        @Volatile
        private var instance: CashierRepository? = null

        fun getInstance(cashierDao: CashierDao): CashierRepository =
            instance ?: synchronized(this) {
                CashierRepository(cashierDao).apply {
                    instance = this
                }
            }
    }
}
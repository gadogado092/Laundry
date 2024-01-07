package amat.kelolakost.data.repository

import amat.kelolakost.data.CustomerCreditDebit
import amat.kelolakost.data.CustomerCreditDebitDao
import kotlinx.coroutines.flow.Flow

class CustomerCreditDebitRepository(private val customerCreditDebitDao: CustomerCreditDebitDao) {
    fun getAllCustomerCreditDebit(): Flow<List<CustomerCreditDebit>> {
        return customerCreditDebitDao.getAllCustomerCreditDebit()
    }

    suspend fun insert(customerCreditDebit: CustomerCreditDebit) {
        customerCreditDebitDao.insert(customerCreditDebit)
    }

    suspend fun update(customerCreditDebit: CustomerCreditDebit) {
        customerCreditDebitDao.update(customerCreditDebit)
    }

    companion object {
        @Volatile
        private var instance: CustomerCreditDebitRepository? = null

        fun getInstance(dao: CustomerCreditDebitDao): CustomerCreditDebitRepository =
            instance ?: synchronized(this) {
                CustomerCreditDebitRepository(dao).apply {
                    instance = this
                }
            }
    }
}
package amat.laundry.data.repository

import amat.laundry.data.Customer
import amat.laundry.data.CustomerDao

class CustomerRepository(private val customerDao: CustomerDao) {

    suspend fun getCustomer(): List<Customer> {
        return customerDao.getCustomer()
    }

    suspend fun searchCustomer(value: String): List<Customer> {
        return customerDao.searchCustomer(value)
    }

    suspend fun insert(customer: Customer) {
        customerDao.insert(customer)
    }

    suspend fun update(customer: Customer) {
        customerDao.update(customer)
    }

    suspend fun deleteCustomer(id: String) {
        customerDao.deleteCustomer(id)
    }

    suspend fun getCustomer(id: String): Customer {
        return customerDao.getCustomer(id)
    }

    companion object {
        @Volatile
        private var instance: CustomerRepository? = null

        fun getInstance(customerDao: CustomerDao): CustomerRepository =
            instance ?: synchronized(this) {
                CustomerRepository(customerDao).apply {
                    instance = this
                }
            }
    }
}
package amat.laundry.data.repository

import amat.laundry.data.CashFlowCategory
import amat.laundry.data.Category
import amat.laundry.data.Customer
import amat.laundry.data.LaundryStatus
import amat.laundry.data.Product
import amat.laundry.data.User
import amat.laundry.data.UserDao
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {
    fun getAllUser(): Flow<List<User>> {
        return userDao.getAllUser()
    }

    suspend fun getUser(): List<User> {
        return userDao.getUser()
    }

    suspend fun insertUser(user: User) {
        userDao.insert(user)
    }

    fun getDetail(): Flow<User> {
        return userDao.getDetail()
    }

    suspend fun getProfile(): User {
        return userDao.getProfile()
    }

    suspend fun updateUser(user: User) {
        userDao.update(user)
    }

    suspend fun printerSelected(userId: String, printerName: String, printerAddress: String) {
        userDao.printerSelected(userId, printerName, printerAddress)
    }

    suspend fun extendApp(userId: String, newLimit: String, newKey: String) {
        userDao.extendApp(userId, newLimit, newKey)
    }

    suspend fun transactionInsertNewUser(
        user: List<User>,
        statusList: List<LaundryStatus>,
        categoryList: List<Category>,
        productList: List<Product>,
        customer: Customer,
        cashFlowCategoryList: List<CashFlowCategory>
    ) {
        userDao.transactionInsertNewUser(
            user,
            statusList,
            categoryList,
            productList,
            customer,
            cashFlowCategoryList
        )
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(dao: UserDao): UserRepository =
            instance ?: synchronized(this) {
                UserRepository(dao).apply {
                    instance = this
                }
            }
    }
}
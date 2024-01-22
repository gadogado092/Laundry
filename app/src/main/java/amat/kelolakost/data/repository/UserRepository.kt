package amat.kelolakost.data.repository

import amat.kelolakost.data.User
import amat.kelolakost.data.UserDao
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

    suspend fun updateUser(user: User) {
        userDao.update(user)
    }

    suspend fun extendApp(userId: String, newLimit: String, newKey: String) {
        userDao.extendApp(userId, newLimit, newKey)
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
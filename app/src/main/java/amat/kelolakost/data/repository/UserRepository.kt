package amat.kelolakost.data.repository

import amat.kelolakost.data.User
import amat.kelolakost.data.UserDao
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {
    fun getAllUser(): Flow<List<User>> {
        return userDao.getAllUser()
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
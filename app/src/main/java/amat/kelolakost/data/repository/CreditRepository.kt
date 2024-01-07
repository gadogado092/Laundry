package amat.kelolakost.data.repository

import amat.kelolakost.data.Credit
import amat.kelolakost.data.CreditDao
import kotlinx.coroutines.flow.Flow

class CreditRepository(private val creditDao: CreditDao) {
    fun getAllUser(): Flow<List<Credit>> {
        return creditDao.getAllCredit()
    }

    suspend fun insert(credit: Credit) {
        creditDao.insert(credit)
    }

    suspend fun update(credit: Credit) {
        creditDao.update(credit)
    }

    companion object {
        @Volatile
        private var instance: CreditRepository? = null

        fun getInstance(dao: CreditDao): CreditRepository =
            instance ?: synchronized(this) {
                CreditRepository(dao).apply {
                    instance = this
                }
            }
    }
}
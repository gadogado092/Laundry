package amat.kelolakost.data.repository

import amat.kelolakost.data.Debit
import amat.kelolakost.data.DebitDao
import kotlinx.coroutines.flow.Flow

class DebitRepository(private val debitDao: DebitDao) {
    fun getAllUser(): Flow<List<Debit>> {
        return debitDao.getAllDebit()
    }

    suspend fun insert(debit: Debit) {
        debitDao.insert(debit)
    }

    suspend fun update(debit: Debit) {
        debitDao.update(debit)
    }

    companion object {
        @Volatile
        private var instance: DebitRepository? = null

        fun getInstance(dao: DebitDao): DebitRepository =
            instance ?: synchronized(this) {
                DebitRepository(dao).apply {
                    instance = this
                }
            }
    }
}
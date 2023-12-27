package amat.kelolakost.data.repository

import amat.kelolakost.data.UnitStatus
import amat.kelolakost.data.UnitStatusDao

class UnitStatusRepository(private val unitStatusDao: UnitStatusDao) {
    suspend fun insert(listStatus: List<UnitStatus>) {
        unitStatusDao.insert(listStatus)
    }

    companion object {
        @Volatile
        private var instance: UnitStatusRepository? = null

        fun getInstance(dao: UnitStatusDao): UnitStatusRepository =
            instance ?: synchronized(this) {
                UnitStatusRepository(dao).apply {
                    instance = this
                }
            }
    }
}
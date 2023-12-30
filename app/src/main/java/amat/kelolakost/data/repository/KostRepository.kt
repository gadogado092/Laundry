package amat.kelolakost.data.repository

import amat.kelolakost.data.Kost
import amat.kelolakost.data.KostDao
import kotlinx.coroutines.flow.Flow

class KostRepository(private val kostDao: KostDao) {
    fun getAllKost(): Flow<List<Kost>> {
        return kostDao.getAllKost()
    }

    suspend fun getAllKostOrder(): List<Kost> {
        return kostDao.getAllKostOrder()
    }

    fun getDetail(id: String): Flow<Kost> {
        return kostDao.getDetail(id)
    }

    suspend fun getKost(): List<Kost> {
        return kostDao.getKost()
    }

    suspend fun insertKost(kost: Kost) {
        kostDao.insert(kost)
    }

    suspend fun updateKost(kost: Kost) {
        kostDao.update(kost)
    }

    companion object {
        @Volatile
        private var instance: KostRepository? = null

        fun getInstance(dao: KostDao): KostRepository =
            instance ?: synchronized(this) {
                KostRepository(dao).apply {
                    instance = this
                }
            }
    }
}
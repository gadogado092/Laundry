package amat.kelolakost.data.repository

import amat.kelolakost.data.UnitType
import amat.kelolakost.data.UnitTypeDao
import kotlinx.coroutines.flow.Flow

class UnitTypeRepository(private val unitTypeDao: UnitTypeDao) {
    fun getAllUnitType(): Flow<List<UnitType>> {
        return unitTypeDao.getAllUnitType()
    }

    fun getDetail(id: String): Flow<UnitType> {
        return unitTypeDao.getDetail(id)
    }

    //
//    suspend fun getKost(): List<Kost> {
//        return kostDao.getKost()
//    }
//
    suspend fun insertUnitType(unitType: UnitType) {
        unitTypeDao.insert(unitType)
    }

    suspend fun updateUnitType(unitType: UnitType) {
        unitTypeDao.update(unitType)
    }

    companion object {
        @Volatile
        private var instance: UnitTypeRepository? = null

        fun getInstance(dao: UnitTypeDao): UnitTypeRepository =
            instance ?: synchronized(this) {
                UnitTypeRepository(dao).apply {
                    instance = this
                }
            }
    }
}
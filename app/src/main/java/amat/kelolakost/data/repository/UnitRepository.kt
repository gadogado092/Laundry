package amat.kelolakost.data.repository

import amat.kelolakost.data.Unit
import amat.kelolakost.data.UnitDao

class UnitRepository(private val unitDao: UnitDao) {
//    fun getAllUnitType(): Flow<List<UnitType>> {
//        return unitTypeDao.getAllUnitType()
//    }
//
//    fun getDetail(id: String): Flow<UnitType> {
//        return unitTypeDao.getDetail(id)
//    }

    suspend fun insertUnit(unit: Unit) {
        unitDao.insert(unit)
    }

//    suspend fun updateUnitType(unitType: UnitType) {
//        unitTypeDao.update(unitType)
//    }

    companion object {
        @Volatile
        private var instance: UnitRepository? = null

        fun getInstance(dao: UnitDao): UnitRepository =
            instance ?: synchronized(this) {
                UnitRepository(dao).apply {
                    instance = this
                }
            }
    }
}
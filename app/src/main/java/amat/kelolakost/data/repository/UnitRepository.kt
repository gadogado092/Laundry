package amat.kelolakost.data.repository

import amat.kelolakost.data.Unit
import amat.kelolakost.data.UnitDao
import amat.kelolakost.data.UnitHome

class UnitRepository(private val unitDao: UnitDao) {
    suspend fun getUnitHome(unitStatusId: String, kostId: String): List<UnitHome> {
        if (unitStatusId == "0") {
            return unitDao.getAllUnitHome(kostId = kostId)
        } else {
            return unitDao.getUnitHome(unitStatusId = unitStatusId, kostId = kostId)
        }
    }
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
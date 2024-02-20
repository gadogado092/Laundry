package amat.kelolakost.data.repository

import amat.kelolakost.data.Unit
import amat.kelolakost.data.UnitDao
import amat.kelolakost.data.UnitType
import amat.kelolakost.data.UnitTypeDao
import kotlinx.coroutines.flow.Flow

class UnitTypeRepository(private val unitTypeDao: UnitTypeDao, private val unitDao: UnitDao) {
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
    suspend fun getAllUnitTypeOrder(): List<UnitType> {
        return unitTypeDao.getAllUnitTypeOrder()
    }
    suspend fun insertUnitType(unitType: UnitType) {
        unitTypeDao.insert(unitType)
    }

    suspend fun updateUnitType(unitType: UnitType) {
        unitTypeDao.update(unitType)
    }

    suspend fun deleteUnitType(unitTypeId: String){
        unitTypeDao.deleteUnitType(unitTypeId)
    }

    suspend fun getUnitByUnitType(unitTypeId: String): List<Unit>{
        return unitDao.getUnitByUnitType(unitTypeId)
    }

    companion object {
        @Volatile
        private var instance: UnitTypeRepository? = null

        fun getInstance(dao: UnitTypeDao, unitDao: UnitDao): UnitTypeRepository =
            instance ?: synchronized(this) {
                UnitTypeRepository(dao, unitDao).apply {
                    instance = this
                }
            }
    }
}
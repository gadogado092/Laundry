package amat.kelolakost.data.repository

import amat.kelolakost.data.Tenant
import amat.kelolakost.data.TenantDao

class TenantRepository(private val tenantDao: TenantDao) {
//    fun getAllUnitType(): Flow<List<UnitType>> {
//        return unitTypeDao.getAllUnitType()
//    }
//
//    fun getDetail(id: String): Flow<UnitType> {
//        return unitTypeDao.getDetail(id)
//    }

    suspend fun insertTenant(tenant: Tenant) {
        tenantDao.insert(tenant)
    }

//    suspend fun updateUnitType(unitType: UnitType) {
//        unitTypeDao.update(unitType)
//    }

    companion object {
        @Volatile
        private var instance: TenantRepository? = null

        fun getInstance(dao: TenantDao): TenantRepository =
            instance ?: synchronized(this) {
                TenantRepository(dao).apply {
                    instance = this
                }
            }
    }
}
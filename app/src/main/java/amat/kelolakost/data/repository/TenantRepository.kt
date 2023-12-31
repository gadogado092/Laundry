package amat.kelolakost.data.repository

import amat.kelolakost.data.Tenant
import amat.kelolakost.data.TenantDao
import kotlinx.coroutines.flow.Flow

class TenantRepository(private val tenantDao: TenantDao) {
    fun getAllUnitType(): Flow<List<Tenant>> {
        return tenantDao.getAllTenant()
    }
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
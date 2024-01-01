package amat.kelolakost.data.repository

import amat.kelolakost.data.Tenant
import amat.kelolakost.data.TenantHome
import amat.kelolakost.data.TenantDao
import android.util.Log

class TenantRepository(private val tenantDao: TenantDao) {
    suspend fun getAllTenantHome(status: String = ""): List<TenantHome> {
        Log.d("saya", "getAllTenantHome $status")
        return when (status) {
            "1" -> {
                tenantDao.getAllTenantHomeCheckIn()
            }

            "0" -> {
                tenantDao.getAllTenantHomeCheckOut()
            }

            else -> {
                tenantDao.getAllTenantHome()
            }
        }
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